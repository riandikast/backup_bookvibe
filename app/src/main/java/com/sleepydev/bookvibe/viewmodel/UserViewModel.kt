package com.sleepydev.bookvibe.viewmodel


import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.sleepydev.bookvibe.datastore.UserManager
import com.sleepydev.bookvibe.model.ApiClient
import com.sleepydev.bookvibe.model.UpdateResponse


import com.sleepydev.bookvibe.model.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserViewModel : ViewModel(){
    lateinit var userManager: UserManager
    var liveDataNewUserItem : MutableLiveData<List<User>> = MutableLiveData()
    var liveDataRegis : MutableLiveData<User> = MutableLiveData()
    var liveDataUpdate : MutableLiveData<User> = MutableLiveData()


    fun getLiveUserObserver() : MutableLiveData<List<User>> {
        return liveDataNewUserItem
    }

    fun getLiveRegisObserver() : MutableLiveData<User> {
        return liveDataRegis
    }

    fun getLiveUpdateObserver() : MutableLiveData<User> {
        return liveDataUpdate
    }

    fun updateDataUser(id : Int, dataUser : UpdateResponse){
        ApiClient.instance.updateUser(dataUser, id.toString() )
            .enqueue(object : retrofit2.Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    liveDataUpdate.postValue(response.body())

                }
                override fun onFailure(call: Call<User>, t: Throwable) {

                }
            })
    }

    fun userApi(){
        ApiClient.instance.getAllUser()
            .enqueue(object : Callback<List<User>>{
                override fun onResponse(
                    call: Call<List<User>>,
                    getAllItem: Response<List<User>>
                ) {
                    if (getAllItem.isSuccessful){
                        liveDataNewUserItem.postValue(getAllItem.body())



                    }else{


                    }
                }
                override fun onFailure(call: Call<List<User>>, t: Throwable) {
                    Log.d(t.toString(), "woi")
                }
            })
    }


    fun regisUser(name: String, email: String, password: String, accountType: String,  balance: Int) {
        ApiClient.instance.register(name, email, password, accountType ,
            ArrayList<Int>(), ArrayList<Int>(), "",  balance, "", "")
            .enqueue(object : Callback<User> {
                override fun onResponse(
                    call: Call<User>,
                    response: Response<User>
                ) {
                    if (response.isSuccessful) {
                        liveDataRegis.postValue(response.body())
                    } else {

                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {

                }
            })
    }

    fun userName(context: Context): LiveData<String> {
        userManager = UserManager(context)
        return userManager.userName.asLiveData()
    }

    fun userBalance(context: Context): LiveData<Any> {
        userManager = UserManager(context)
        return userManager.userBalance.asLiveData()
    }

    fun userID(context: Context): LiveData<Int> {
        userManager = UserManager(context)
        return userManager.userID.asLiveData()
    }

}