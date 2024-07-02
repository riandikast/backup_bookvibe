package com.sleepydev.bookvibe.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransactionItemResponse(
    @SerializedName("id")
    @Expose
    val id: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("product_id")
    val productID: Int,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_img")
    val adjustedImg: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("adjusted_balance")
    val adjustedBalance: String,
    @SerializedName("time")
    val time: String,

    ): Parcelable