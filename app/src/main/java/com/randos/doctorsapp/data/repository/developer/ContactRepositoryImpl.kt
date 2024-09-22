package com.randos.doctorsapp.data.repository.developer

import com.randos.domain.manager.ContactManager
import com.randos.domain.model.Contact
import com.randos.domain.repository.ContactRepository
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactManager: ContactManager
) : ContactRepository {

    override suspend fun getContacts(): List<Contact> {
        return contactManager.getContacts()
    }
}