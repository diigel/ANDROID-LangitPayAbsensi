package com.absensi.langitpay.network.response


import com.google.gson.annotations.SerializedName

data class RequestAbsent(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: DataRequestAbsent? = null,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)
data class DataRequestAbsent(
    @SerializedName("address")
    val address: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("division")
    val division: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("noted")
    val noted: String,
    @SerializedName("type_absensi")
    val typeAbsensi: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("verification")
    val verification: String
)