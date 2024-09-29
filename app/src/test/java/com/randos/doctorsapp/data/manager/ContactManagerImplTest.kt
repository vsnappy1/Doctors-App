package com.randos.doctorsapp.data.manager

import android.app.Application
import android.database.Cursor
import android.provider.ContactsContract
import com.randos.domain.manager.ContactManager
import com.randos.domain.manager.PermissionManager
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class ContactManagerImplTest {

    @MockK
    private lateinit var application: Application

    @MockK
    private lateinit var permissionManager: PermissionManager

    private lateinit var contactManagerImpl: ContactManager

    private val dispatcher = Dispatchers.Unconfined

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        contactManagerImpl = ContactManagerImpl(application, permissionManager, dispatcher)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun getContacts_whenPermissionNotGranted_shouldReturnEmptyList() = runTest {
        // Given
        every { permissionManager.isReadContactPermissionGranted() } returns false

        // When
        val result = contactManagerImpl.getContacts()

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun getContacts_whenPermissionGranted_shouldReturnContactList() = runTest {
        // Given
        val cursor: Cursor = mockk()
        val phoneCursor: Cursor = mockk()

        every { permissionManager.isReadContactPermissionGranted() } returns true
        every { application.contentResolver.query(ContactsContract.Contacts.CONTENT_URI, any(), any(), any(), any()) } returns cursor
        every { application.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, any(), any(), any(), any()) } returns phoneCursor
        every { cursor.getColumnIndex(ContactsContract.Contacts._ID) } returns 0
        every { cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY) } returns 1
        every { cursor.moveToNext() } returns true andThen false
        every { cursor.getLong(0) } returns 1
        every { cursor.getString(1) } returns "John Doe"
        every { phoneCursor.moveToNext() } returns true andThen false
        every { phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER) } returns 0
        every { phoneCursor.getString(0) } returns "1234567890"
        every { cursor.close() } returns Unit
        every { phoneCursor.close() } returns Unit

        // When
        val result = contactManagerImpl.getContacts()

        // Then
        assertEquals(1, result.size)
        assertEquals("John Doe", result[0].name)
    }
}