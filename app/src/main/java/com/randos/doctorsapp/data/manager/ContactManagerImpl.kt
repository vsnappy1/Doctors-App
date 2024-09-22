package com.randos.doctorsapp.data.manager

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.randos.doctorsapp.di.Dispatcher
import com.randos.domain.manager.ContactManager
import com.randos.domain.manager.PermissionManager
import com.randos.domain.model.Contact
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume

class ContactManagerImpl @Inject constructor(
    private val application: Application,
    private val permissionManager: PermissionManager,
    @Dispatcher.Io private val dispatcher: CoroutineDispatcher
) : ContactManager {

    companion object {
        private const val TAG = "ContactManagerImpl"
    }

    override suspend fun getContacts(): List<Contact> = withContext(dispatcher) {
        suspendCancellableCoroutine { continuation ->

            if (!permissionManager.isReadContactPermissionGranted()) {
                Log.e(TAG, "Contact read permission is not granted.")
                continuation.resume(emptyList())
                return@suspendCancellableCoroutine
            }

            val cursor = getContactCursor()

            val contacts = mutableListOf<Contact>()

            cursor?.use {
                val idIndex = it.getColumnIndex(ContactsContract.Contacts._ID)
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY)

                while (it.moveToNext()) {
                    val id = it.getLong(idIndex)
                    val name: String? = it.getString(nameIndex)
                    val numbers = getPhoneNumbers(id)
                    Log.d(TAG, "getContacts: $id $name $numbers")
                    name?.let { contacts.add(Contact(id, name, numbers)) }
                }
            }

            continuation.resume(contacts.sortedBy { it.name }) { _, _, _ ->
                cursor?.close()  // Ensure the cursor is closed in case of cancellation
            }
        }
    }

    private fun getContactCursor(): Cursor? {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME
        )

        val cursor = application.contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        return cursor
    }

    private fun getPhoneNumbers(id: Long): MutableList<String> {
        // Query phone numbers for the current contact
        val phoneCursor = getPhoneCursor(id)

        val numbers = mutableListOf<String>()

        phoneCursor?.use {
            while (it.moveToNext()) {
                val phoneNumberIndex =
                    it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val number = it.getString(phoneNumberIndex)
                numbers.add(number)
            }
        }
        return numbers
    }

    private fun getPhoneCursor(id: Long) = application.contentResolver.query(
        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
        arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
        "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
        arrayOf(id.toString()),
        null
    )
}