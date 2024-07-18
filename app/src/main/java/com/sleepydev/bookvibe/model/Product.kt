package com.sleepydev.bookvibe.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.Objects


@Parcelize
data class Product(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("desc")
    val desc: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("seller_name")
    val sellerName: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("image")
    val image: @RawValue Any? = null,
    @SerializedName("seller_id")
    val sellerID: Int,
    @SerializedName("sold_count")
    val soldCount: Int,
    @SerializedName("revenue")
    val revenue: Int,
    @SerializedName("satuan")
    val satuan: String,
    ):Parcelable
