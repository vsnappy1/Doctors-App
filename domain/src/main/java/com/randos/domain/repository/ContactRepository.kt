package com.randos.domain.repository

import com.randos.domain.model.Contact

interface ContactRepository {
    suspend fun getContacts(): List<Contact>
}