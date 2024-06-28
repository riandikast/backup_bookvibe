package com.sleepydev.bookvibe.viewmodel


import android.content.Context

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sleepydev.bookvibe.datastore.UserManager

class LoginViewModel() :ViewModel() {
    lateinit var userManager: UserManager
    fun userToken(context: Context): LiveData<String> {
        userManager = UserManager(context)
        return userManager.userToken.asLiveData()
    }

    fun userType(context: Context): LiveData<String> {
        userManager = UserManager(context)
        return userManager.userAccountType.asLiveData()
    }

}