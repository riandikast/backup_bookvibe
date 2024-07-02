package com.sleepydev.bookvibe.datastore

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.datastore.DataStore
import androidx.datastore.preferences.Preferences
import androidx.datastore.preferences.clear
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okhttp3.Address


class UserManager (context : Context) {

    private val dataStore : DataStore<Preferences> = context.createDataStore(name = "user_prefs")
    private val loginDataStore : DataStore<Preferences> = context.createDataStore(name = "log_pref")
    private val productDataStore : DataStore<Preferences> = context.createDataStore(name = "pro_pref")

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
        val ADDRESS = preferencesKey<String>("ADDRESS")
        val PHONE = preferencesKey<String>("PHONE")
        val PRODUCTID = preferencesKey<Int>("PRO_ID")
        val PRODUCTIMG = preferencesKey<String>("PRO_IMG")
        val SELLERID = preferencesKey<Int>("PRO_IMG")
    }

    suspend fun saveDataUser(id : Int, name:String, email:String, password : String,
                             accountType: String, history: String, cart: String, createdAt : String, userToken: String, userBalance: Int, address: String, phone : String) {
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
            it[ADDRESS] = address
            it[PHONE] = phone

        }
    }

    suspend fun updateProfile(  name:String, address : String, phone: String) {
        dataStore.edit {
            it[NAME] = name
            it[ADDRESS] = address
            it[PHONE] = phone

        }
    }

    suspend fun updateBalance( userBalance: Int) {
        dataStore.edit {
            it[BALANCE] = userBalance
        }
    }

    suspend fun deleteDataUser() {
        dataStore.edit{
            it.clear()

        }
        loginDataStore.edit{
            it.clear()

        }
    }

    suspend fun saveDataLogin(userToken : String) {
        loginDataStore.edit {
            it[USER_TOKEN] = userToken
        }
    }

    suspend fun saveTempProduct(id : Int, img: String, sellerID:Int) {
        productDataStore.edit {
            it[PRODUCTID] = id
            it[PRODUCTIMG] = img
            it[SELLERID] =  sellerID
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

    val productID : kotlinx.coroutines.flow.Flow<Int> = productDataStore.data.map {
        it [PRODUCTID] ?: 0
    }

    val sellerID : kotlinx.coroutines.flow.Flow<Int> = productDataStore.data.map {
        it [SELLERID] ?: 0
    }


    val productIMG : kotlinx.coroutines.flow.Flow<String> = productDataStore.data.map {
        it [PRODUCTIMG] ?: ""
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
    val userToken : kotlinx.coroutines.flow.Flow<String> = loginDataStore.data.map {
        it [USER_TOKEN] ?: ""
    }

    val userBalance : Flow<Int> = dataStore.data.map {
        it [BALANCE] ?: 0
    }

    val userAddress : kotlinx.coroutines.flow.Flow<String> = dataStore.data.map {
        it [ADDRESS] ?: ""
    }

    val userPhone : Flow<String> = dataStore.data.map {
        it [PHONE] ?: ""
    }

}