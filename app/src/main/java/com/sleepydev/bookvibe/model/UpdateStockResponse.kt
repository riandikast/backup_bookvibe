package com.sleepydev.bookvibe.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateStockResponse (
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("price")
    val price: Int,
): Parcelable