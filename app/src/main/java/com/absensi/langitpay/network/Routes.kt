package com.absensi.langitpay.network

import com.absensi.langitpay.network.response.ResponseLogin
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface Routes {

    @FormUrlEncoded
    @POST("login")
    fun login (
        @Field("username")username : String? = null,
        @Field("password")password : String? = null,
        @Field("device_uniq") deviceUniq :String? = null
    ) : Observable<ResponseLogin>
}