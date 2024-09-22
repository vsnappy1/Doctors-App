package com.randos.domain.model

data class Contact(
    val id: Long,
    val name: String,
    val number: List<String>
)
