package com.randos.domain.store

interface TokenStore {
    fun getToken(): String?
    fun setToken(token: String)
}