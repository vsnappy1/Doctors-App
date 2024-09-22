package com.randos.domain.manager

import com.randos.domain.model.Contact

interface ContactManager {
    suspend fun getContacts(): List<Contact>
}