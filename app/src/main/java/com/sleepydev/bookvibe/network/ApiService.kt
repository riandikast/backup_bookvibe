package com.sleepydev.bookvibe.model


import com.sleepydev.bookvibe.model.User
import kotlinx.android.parcel.RawValue
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("user")
    fun getAllUser(): Call<List<User>>

    @GET("product")
    fun getAllProduct(): Call<List<Product>>

    @GET("product/{id}")
    fun getSellerProduct(@Path("id")id : Int): Call<Product>

    @DELETE("product/{id}")
    fun deleteProduct(@Path("id")id : Int): Call<Product>

    @GET("user/{id}")
    fun getCurrentUser(
        @Path("id")id : Int
    ) : Call<User>

    @PUT("user/{id}")
    fun updateUser(
        @Body user : UpdateResponse, @Path("id") id : Int
    ): Call<User>

    @PUT("user/{id}")
    fun updateTransaction(
        @Body user : TransactionResponse, @Path("id") id : Int
    ): Call<User>

    @PUT("product/{id}")
    fun updateStock(
        @Body user : UpdateStockResponse, @Path("id") id : Int
    ): Call<Product>


    @PUT("product/{id}")
    fun productSold(
        @Body user : SoldResponse, @Path("id") id : Int
    ): Call<Product>

    @PUT("user/{id}")
    fun topUpBalance(
        @Body user : TopUpResponse, @Path("id") id : Int
    ): Call<User>



    @POST("user")
    fun register(
        @Body user:User
        ): Call<User>

    @POST("product")
    @FormUrlEncoded
    fun addProduct(
        @Field("name") name: String,
        @Field("desc") desc: String,
        @Field("price") price: Int,
        @Field("stock") stock:Int,
        @Field("seller_name") seller_name: String,
        @Field("cart") cart: Any,
        @Field("image") image: @RawValue Any? = null,
        @Field("seller_id") seller_id: Int,
        @Field("sold_count") sold_count: Int,
        @Field("revenue") revenue: Int,
    ): Call<Product>





}