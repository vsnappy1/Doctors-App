package com.randos.domain.store

import com.randos.domain.model.User

interface UserStore {
    fun getUser(): User?
    fun setUser(user: User)
}