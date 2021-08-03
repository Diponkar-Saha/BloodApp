package com.example.splash.commons

import com.example.splash.model.UserData
import io.paperdb.Paper

object TwitterCloneAppData {

    private var user: UserData? = null

    fun getUser(): UserData? {
        return Paper.book().read<UserData>("user")
    }

    fun setUserData(user: UserData?) {
        user?.let {
            Paper.book().write("user", it)
        }
    }
}