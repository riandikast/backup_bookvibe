package com.sleepydev.bookvibe.model


import com.sleepydev.bookvibe.model.User
import retrofit2.Call
import retrofit2.http.*

interface ApiService {


    @GET("user")
    fun getAllUser(): Call<List<User>>

    @PUT("user/{id}")
    fun updateUser(
        @Body user : UpdateResponse, @Path("id") id : String
    ): Call<User>



    @POST("user")
    @FormUrlEncoded
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("account_type") account_type: String,
        @Field("history") history: Any,
        @Field("cart") cart: Any,
        @Field("image") image: String,
        @Field("balance") balance: Int,
        @Field("address") address: String,
        @Field("phone_number") phone_number: String,
        ): Call<User>


}