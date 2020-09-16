package com.absensi.langitpay.network.response


import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<DataNotification>? = null,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Boolean
)

data class DataNotification(
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int
)