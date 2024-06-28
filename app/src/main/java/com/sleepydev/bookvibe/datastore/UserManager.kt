package com.sleepydev.bookvibe.datastore

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.clear
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class UserManager (context : Context) {
    private val dataStore : DataStore<Preferences> = context.createDataStore(name = "user_prefs")
    private val loginDataStore : DataStore<Preferences> = context.createDataStore(name = "login_prefs")

    companion object{
        val ID = preferencesKey<Int>("USER_ID")
        val NAME = preferencesKey<String>("NAME")
        val EMAIL = preferencesKey<String>("USER_EMAIL")
        val PASSWORD = preferencesKey<String>("USER_PASSWORD")
        val ACCOUNT_TYPE = preferencesKey<String>("ACCOUNT_TYPE")
        val HISTORY = preferencesKey<String>("HISTORY")
        val CART = preferencesKey<String>("CART")
        val CREATED_AT = preferencesKey<String>("CREATED_AT")
        val USER_IMAGE  = preferencesKey<String>("USER_IMAGE")
        val USER_TOKEN  = preferencesKey<String>("USER_TOKEN")
        val BALANCE  = preferencesKey<Int>("BALANCE")
    }

    suspend fun saveDataUser(id : Int, name:String, email:String, password : String,
                             accountType: String, history: String, cart: String, createdAt : String, userToken: String, userBalance: Int) {
        dataStore.edit {
            it[ID] = id
            it[NAME] = name
            it[EMAIL] = email
            it[PASSWORD] = password
            it[ACCOUNT_TYPE] = accountType
            it[HISTORY] = history
            it[CART] = cart
            it[CREATED_AT] = createdAt
            it[USER_IMAGE] = cart
            it[USER_TOKEN] = userToken
            it[BALANCE] = userBalance

        }
    }

    suspend fun deleteDataUser() {
        dataStore.edit{
            it.clear()

        }
    }

    suspend fun saveDataLogin(userToken : String) {
        loginDataStore.edit {
            it[USER_TOKEN] = userToken
        }
    }

    suspend fun deleteDataLogin(){
        loginDataStore.edit{
            it.clear()

        }
    }

    val userID : kotlinx.coroutines.flow.Flow<Int> = dataStore.data.map {
        it [ID] ?: 0
    }
    val userName : kotlinx.coroutines.flow.Flow<String> = dataStore.data.map {
        it [NAME] ?: ""
    }
    val userEmail : kotlinx.coroutines.flow.Flow<String> = dataStore.data.map {
        it [EMAIL] ?: ""
    }
    val userPass : kotlinx.coroutines.flow.Flow<String> = dataStore.data.map {
        it [PASSWORD] ?: ""
    }
    val userAccountType : kotlinx.coroutines.flow.Flow<String> = dataStore.data.map {
        it [ACCOUNT_TYPE] ?: ""
    }
    val userHistory: kotlinx.coroutines.flow.Flow<String> = dataStore.data.map {
        it [HISTORY] ?: ""
    }
    val userCart : kotlinx.coroutines.flow.Flow<String> = dataStore.data.map {
        it [CART] ?: ""
    }
    val userCreatedAt : kotlinx.coroutines.flow.Flow<String> = loginDataStore.data.map {
        it [CREATED_AT] ?: ""
    }
    val userImage : kotlinx.coroutines.flow.Flow<String> = dataStore.data.map {
        it [USER_IMAGE] ?: ""
    }
    val userToken : kotlinx.coroutines.flow.Flow<String> = dataStore.data.map {
        it [USER_TOKEN] ?: ""
    }

    val userBalance : Flow<Any> = dataStore.data.map {
        it [BALANCE] ?: 0
    }

}