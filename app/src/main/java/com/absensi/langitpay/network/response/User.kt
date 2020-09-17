package com.absensi.langitpay.network.response


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: DataUser? = null,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)

data class DataUser(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("device_uniq")
    val deviceUniq: String,
    @SerializedName("division")
    val division: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("NIK")
    val nik: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("updated_at")
    val updatedAt: String
)