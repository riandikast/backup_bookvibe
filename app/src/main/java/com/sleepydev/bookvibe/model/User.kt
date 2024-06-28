package com.sleepydev.bookvibe.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("account_type")
    val accountType: String,
    @SerializedName("history")
    val history: @RawValue Any? = null,
    @SerializedName("cart")
    val cart: @RawValue Any? = null,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("user_token")
    val userToken: String,
    @SerializedName("balance")
    val balance: Int,
    @SerializedName("address")
    val address: String,
    @SerializedName("phone_number")
    val phoneNumber: String,
): Parcelable

