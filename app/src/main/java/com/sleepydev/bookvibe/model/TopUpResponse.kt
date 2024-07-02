package com.sleepydev.bookvibe.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TopUpResponse(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("balance")
    val balance: Int,

): Parcelable