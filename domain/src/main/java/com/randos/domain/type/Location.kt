package com.randos.domain.type

sealed class Location {
    data class LatLng(val lat: Double, val lng: Double) : Location()
    data class Address(val address: com.randos.domain.model.Address) : Location()
}