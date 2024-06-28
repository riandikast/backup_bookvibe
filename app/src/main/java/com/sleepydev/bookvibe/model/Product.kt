package com.sleepydev.bookvibe.model

import com.google.gson.annotations.SerializedName
import java.util.Objects

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
    val stock: Double,
    @SerializedName("seller_name")
    val sellerName: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("image")
    val image: String,

)
