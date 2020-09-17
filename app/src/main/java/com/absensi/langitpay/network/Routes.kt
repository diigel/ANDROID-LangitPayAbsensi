package com.absensi.langitpay.network

import com.absensi.langitpay.abstraction.getDeviceUniqueId
import com.absensi.langitpay.network.response.GetLocation
import com.absensi.langitpay.network.response.Notification
import com.absensi.langitpay.network.response.User
import com.absensi.langitpay.network.response.SearchLocation
import io.reactivex.Observable
import retrofit2.http.*

interface Routes {

    @FormUrlEncoded
    @POST("request-login")
    fun login(
        @Field("username") username: String? = null,
        @Field("password") password: String? = null,
        @Field("device_uniq") deviceUniq: String? = null,
        @Field("token") token : String? = null
    ): Observable<User>

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
    ) : Observable<SearchLocation>

    @FormUrlEncoded
    @POST("request-update-token")
    fun updateToken(
        @Field("token") token: String,
        @Field("device_uniq") deviceUniq: String = getDeviceUniqueId(AbsensiLangitPayAplication.getApplicationContext())
    ) : Observable<User>


    @POST("get-notification")
    fun getNotification() : Observable<Notification>

    @FormUrlEncoded
    @POST("get-user")
    fun getUser(
        @Field("device_uniq") deviceUniq: String = getDeviceUniqueId(AbsensiLangitPayAplication.getApplicationContext())
    ) : Observable<User>
}