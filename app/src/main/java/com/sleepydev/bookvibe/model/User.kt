package com.sleepydev.bookvibe.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Parcelize
data class User(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("account_type")
    val accountType: String,
    @SerializedName("history")
    val history: List<TransactionItemResponse> = emptyList(),
    @SerializedName("cart")
    val cart: @RawValue List<Any> = emptyList(),
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("image")
    val image: @RawValue Any? = null,
    @SerializedName("user_token")
    val userToken: String? = null,
    @SerializedName("balance")
    val balance: Int,
    @SerializedName("address")
    val address: String,
    @SerializedName("phone_number")
    val phoneNumber: String
): Parcelable

