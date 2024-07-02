package com.sleepydev.bookvibe.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.sleepydev.bookvibe.model.ApiClient
import com.sleepydev.bookvibe.model.ApiService
import com.sleepydev.bookvibe.model.TopUpResponse
import com.sleepydev.bookvibe.model.TransactionItemResponse
import com.sleepydev.bookvibe.model.TransactionResponse
import com.sleepydev.bookvibe.model.UpdateResponse
import com.sleepydev.bookvibe.model.User
import com.sleepydev.bookvibe.utils.CustomToast
import com.sleepydev.bookvibe.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class UserRepository  @Inject constructor (private val apiService: ApiService) {


    suspend fun currentUser(id: Int, livedata: MutableLiveData<User>, responseCode: MutableLiveData<String>){
        val apiClient: Call<User> = apiService.getCurrentUser(id)
        apiClient.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                livedata.postValue(response.body())
                responseCode.postValue(response.code().toString())
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }

    suspend fun currentUserSeller(id: Int, livedata: MutableLiveData<User?>, responseCode: MutableLiveData<String>){
        val apiClient: Call<User> = apiService.getCurrentUser(id)
        apiClient.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                livedata.postValue(response.body())
                responseCode.postValue(response.code().toString())
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }

    suspend fun getAllUser(  livedata: MutableLiveData<List<User>>, responseCode : MutableLiveData<String>){
        val apiClient: Call<List<User>> = apiService.getAllUser()
        apiClient.enqueue(object : Callback<List<User>> {
            override fun onResponse(
                call: Call<List<User>>,
                getAllItem: Response<List<User>>
            ) {

                livedata.postValue(getAllItem.body())
                responseCode.postValue(getAllItem.code().toString())

            }
            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }
    suspend fun register( name: String, email: String, password: String, accountType: String,  balance: Int, livedata: MutableLiveData<User>, responseCode : MutableLiveData<String>){
        val historyList: List<TransactionItemResponse> = listOf()
        val apiClient: Call<User> = apiService.register(User(0, name, email, password, accountType, historyList, listOf(), null, "", null, 0, "", "") )
        apiClient.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                getAllItem: Response<User>
            ) {
                livedata.postValue(getAllItem.body())
                responseCode.postValue(getAllItem.code().toString())

            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }

    suspend fun updateUser(id : Int, dataUser : UpdateResponse,  livedata: MutableLiveData<User?>, responseCode : MutableLiveData<String>){
        val apiClient: Call<User> = apiService.updateUser(dataUser, id)
        apiClient.enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                        livedata.postValue(response.body())
                        responseCode.postValue(response.code().toString())
                }
                override fun onFailure(call: Call<User>, t: Throwable) {
                    livedata.postValue(null)
                }
            })

    }

    suspend fun updateTransaction(id : Int, user:TransactionResponse, newTransaction:TransactionItemResponse,  livedata: MutableLiveData<User>, responseCode : MutableLiveData<String>){
        val updatedHistory = user.history.toMutableList().apply {
            add(newTransaction)
        }
        val updatedUser = user.copy(history = updatedHistory)
        val apiClient: Call<User> = apiService.updateTransaction(updatedUser, id)
        apiClient.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                livedata.postValue(response.body())
                responseCode.postValue(response.code().toString())
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }

    suspend fun updateTransactionForSeller(id : Int, user:TransactionResponse, newTransaction:TransactionItemResponse,  livedata: MutableLiveData<User>, responseCode : MutableLiveData<String>){
        val updatedHistory = user.history.toMutableList().apply {
            add(newTransaction)
        }
        val updatedUser = user.copy(history = updatedHistory)
        val apiClient: Call<User> = apiService.updateTransaction(updatedUser, id)
        apiClient.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                livedata.postValue(response.body())
                responseCode.postValue(response.code().toString())
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }

    suspend fun updateBalance(id : Int, dataUser : TopUpResponse,  livedata: MutableLiveData<User>, responseCode: MutableLiveData<String>){
        val apiClient: Call<User> = apiService.topUpBalance(dataUser, id)
        apiClient.enqueue(object : Callback<User> {
            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                livedata.postValue(response.body())
                responseCode.postValue(response.code().toString())
            }
            override fun onFailure(call: Call<User>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }



}