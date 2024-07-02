package com.sleepydev.bookvibe.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SoldResponse (
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("revenue")
    val revenue: Int,
    @SerializedName("sold_count")
    val soldCount: Int,
): Parcelable