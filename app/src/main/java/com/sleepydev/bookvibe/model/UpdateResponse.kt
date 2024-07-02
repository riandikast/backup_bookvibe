package com.sleepydev.bookvibe.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class UpdateResponse(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("phone_number")
    val phone_number: String,
    @SerializedName("image")
    val image: String? = null,
): Parcelable