package com.example.data.dataSource

import com.example.data.model.User

interface UserDataSource {
    suspend fun getUserByUsername(username:String) : User?
    suspend fun insertUser(user: User) : Boolean
}