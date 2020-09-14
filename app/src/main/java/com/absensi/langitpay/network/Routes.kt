package com.absensi.langitpay.network

import com.absensi.langitpay.network.response.GetLocation
import com.absensi.langitpay.network.response.ResponseLogin
import io.reactivex.Observable
import retrofit2.http.*

interface Routes {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username") username: String? = null,
        @Field("password") password: String? = null,
        @Field("device_uniq") deviceUniq: String? = null
    ): Observable<ResponseLogin>

    @GET("/v1/revgeocode")
    fun getLocation(
        @Query("at") at: String,
        @Query("apiKey") apiKey: String
    ): Observable<GetLocation>

    @GET("autosuggest")
    fun getLocationSearch(
        @Query("at") lat: String,
        @Query("q") placeName : String,
        @Query("apikey") apiKey: String
    ) : Observable<GetLocation>
}