package com.sleepydev.bookvibe.viewmodel


import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sleepydev.bookvibe.R
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.utils.SingleLiveEvent
import com.sleepydev.bookvibe.model.ApiClient
import com.sleepydev.bookvibe.model.ApiService
import com.sleepydev.bookvibe.model.TopUpResponse
import com.sleepydev.bookvibe.model.TransactionItemResponse
import com.sleepydev.bookvibe.model.TransactionResponse
import com.sleepydev.bookvibe.model.UpdateResponse


import com.sleepydev.bookvibe.model.User
import com.sleepydev.bookvibe.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor (val userRepository: UserRepository): ViewModel(){

    lateinit var userManager: UserManager

    fun userID(context: Context): LiveData<Int> {
        userManager = UserManager(context)
        return userManager.userID.asLiveData()
    }



    var getAllUserObserver: MutableLiveData<List<User>> = MutableLiveData()
    var registerObserver:  SingleLiveEvent<User> = SingleLiveEvent()
    var currentUserObserver:SingleLiveEvent<User>  = SingleLiveEvent()
    var updateProfileObserver: SingleLiveEvent<User?>  = SingleLiveEvent()
    var balanceObserver: MutableLiveData<User>  = MutableLiveData()

    var getAllUserResponseCode: MutableLiveData<String> = MutableLiveData()
    var registerResponseCode: SingleLiveEvent<String> = SingleLiveEvent()
    var updateUserResponseCode: SingleLiveEvent<String> = SingleLiveEvent()
    var topUpResponseCode: MutableLiveData<String> = MutableLiveData()
    var currentUserResponseCode: MutableLiveData<String> = MutableLiveData()

    var currentUserForSellerImageObserver:MutableLiveData<User?>  = SingleLiveEvent()
    var currentUserForSellerImageResponseCode: SingleLiveEvent<String> = SingleLiveEvent()


    var updateTransactionObserver: SingleLiveEvent<User>  = SingleLiveEvent()
    var updateTransactionResponseCode: MutableLiveData<String> = MutableLiveData()

    var updateTransactionForSellerObserver: SingleLiveEvent<User>  = SingleLiveEvent()
    var updateTransactionForSellerResponseCode: MutableLiveData<String> = MutableLiveData()

    var updateTransactionSellerObserver: SingleLiveEvent<User>  = SingleLiveEvent()
    var updateTransactionSellerResponseCode: MutableLiveData<String> = MutableLiveData()

    fun getCurrentUser(id:Int) {
        viewModelScope.launch {
          userRepository.currentUser(id, currentUserObserver, currentUserResponseCode)
        }
    }

    fun getCurrentUserForSellerImage(id:Int) {
        viewModelScope.launch {
            userRepository.currentUserSeller(id, currentUserForSellerImageObserver, currentUserForSellerImageResponseCode)
        }
    }

    fun getAllUser(){
        viewModelScope.launch {
            userRepository.getAllUser(getAllUserObserver, getAllUserResponseCode)
        }
    }

    fun register(name: String, email: String, password: String, accountType: String,  balance: Int){
        viewModelScope.launch {
            userRepository.register(name, email, password, accountType, balance, registerObserver, registerResponseCode)
        }
    }

    fun updateUser(id : Int, dataUser : UpdateResponse,){
        viewModelScope.launch {
            userRepository.updateUser(id, dataUser, updateProfileObserver, updateUserResponseCode)
        }
    }

    fun updateTransaction(id : Int, dataProduct : TransactionResponse, listItem:TransactionItemResponse ){
        viewModelScope.launch {
            userRepository.updateTransaction(id, dataProduct, listItem, updateTransactionObserver, updateTransactionResponseCode)
        }
    }

    fun updateTransactionForSeller(id : Int, dataUser: TransactionResponse, listItem:TransactionItemResponse ){
        viewModelScope.launch {
            userRepository.updateTransaction(id, dataUser, listItem, updateTransactionForSellerObserver, updateTransactionForSellerResponseCode)
        }
    }




    fun topUpBalance(id : Int, dataUser : TopUpResponse,){
        viewModelScope.launch {
            userRepository.updateBalance(id, dataUser, balanceObserver, topUpResponseCode)
        }
    }





}