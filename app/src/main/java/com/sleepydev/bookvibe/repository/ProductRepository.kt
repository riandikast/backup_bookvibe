package com.sleepydev.bookvibe.repository

import androidx.lifecycle.MutableLiveData
import com.sleepydev.bookvibe.model.ApiService
import com.sleepydev.bookvibe.model.Product
import com.sleepydev.bookvibe.model.SoldResponse
import com.sleepydev.bookvibe.model.UpdateResponse
import com.sleepydev.bookvibe.model.UpdateStockResponse
import com.sleepydev.bookvibe.model.User
import kotlinx.android.parcel.RawValue
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class ProductRepository @Inject constructor (private val apiService: ApiService) {

    suspend fun addProduct(name: String, desc:String, price:Int, stock:Int, sellerName:String, image: @RawValue Any? = null, sellerID: Int, soldCount: Int, revenue: Int, satuan:String, livedata: MutableLiveData<Product>, responseCode : MutableLiveData<String>){
        val apiClient: Call<Product> = apiService.addProduct(name, desc, price, stock, sellerName,  "", image, sellerID, soldCount, revenue, satuan)
        apiClient.enqueue(object : Callback<Product> {
            override fun onResponse(
                call: Call<Product>,
                getAllItem: Response<Product>
            ) {
                livedata.postValue(getAllItem.body())
                responseCode.postValue(getAllItem.code().toString())

            }
            override fun onFailure(call: Call<Product>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }

    suspend fun getAllProduct(  livedata: MutableLiveData<List<Product>>, responseCode : MutableLiveData<String>){
        val apiClient: Call<List<Product>> = apiService.getAllProduct()
        apiClient.enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                getAllItem: Response<List<Product>>
            ) {

                livedata.postValue(getAllItem.body())
                responseCode.postValue(getAllItem.code().toString())

            }
            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }

    suspend fun getSellerProduct( id: Int, livedata: MutableLiveData<Product>, responseCode: MutableLiveData<String>){
        val apiClient: Call<Product> = apiService.getSellerProduct(id)
        apiClient.enqueue(object :  Callback<Product> {

            override fun onFailure(call:  Call<Product>, t: Throwable) {
                livedata.postValue(null)
            }

            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                livedata.postValue(response.body())
                responseCode.postValue(response.code().toString())

            }
        })

    }

    suspend fun deleteProduct( id: Int, livedata: MutableLiveData<Product>, responseCode: MutableLiveData<String>){
        val apiClient:Call<Product> = apiService.deleteProduct(id)
        apiClient.enqueue(object :  Callback<Product> {

            override fun onFailure(call:  Call<Product>, t: Throwable) {
                livedata.postValue(null)
            }

            override fun onResponse(call:Call<Product>, response: Response<Product>) {
                livedata.postValue(response.body())
                responseCode.postValue(response.code().toString())

            }
        })

    }

    suspend fun updateStock(id : Int, dataProduct : UpdateStockResponse, livedata: MutableLiveData<Product>, responseCode : MutableLiveData<String>){
        val apiClient: Call<Product> = apiService.updateStock(dataProduct, id)
        apiClient.enqueue(object : Callback<Product> {
            override fun onResponse(
                call: Call<Product>,
                response: Response<Product>
            ) {
                livedata.postValue(response.body())
                responseCode.postValue(response.code().toString())
            }
            override fun onFailure(call: Call<Product>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }

    suspend fun soldProduct(id : Int, dataProduct : SoldResponse, livedata: MutableLiveData<Product>, responseCode : MutableLiveData<String>){
        val apiClient: Call<Product> = apiService.productSold(dataProduct, id)
        apiClient.enqueue(object : Callback<Product> {
            override fun onResponse(
                call: Call<Product>,
                response: Response<Product>
            ) {
                livedata.postValue(response.body())
                responseCode.postValue(response.code().toString())
            }
            override fun onFailure(call: Call<Product>, t: Throwable) {
                livedata.postValue(null)
            }
        })

    }



}
