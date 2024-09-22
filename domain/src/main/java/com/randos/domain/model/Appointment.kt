package com.randos.domain.model

data class Appointment(
    val id: Long,
    val date: String,
    val time: Time
)

data class Time(
    val from: String,
    val to: String
)