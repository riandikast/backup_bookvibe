package com.sleepydev.bookvibe.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class TransactionResponse(
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("history")
    val history:  List<TransactionItemResponse>
):Parcelable
