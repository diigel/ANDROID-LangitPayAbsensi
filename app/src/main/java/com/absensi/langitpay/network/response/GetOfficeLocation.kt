package com.absensi.langitpay.network.response

import com.google.gson.annotations.SerializedName

data class GetOfficeLocation(
    @SerializedName("status")
    val status: Boolean? = null,
    @SerializedName("code")
    val code: Int? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: List<DataGetOfficeLocation>? = null
)

data class DataGetOfficeLocation(
    @SerializedName("id")
    val id: Int? = null,
    @SerializedName("long")
    val long: String? = null,
    @SerializedName("lat")
    val lat: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("office_name")
    val officeName : String = "",
    @SerializedName("created_at")
    val created_at : String? = null,
    @SerializedName("updated_at")
    val updated_at : String? = null
){
    override fun toString(): String {
        return officeName
    }
}
