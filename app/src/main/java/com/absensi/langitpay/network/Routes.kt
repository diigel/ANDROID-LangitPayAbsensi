package com.absensi.langitpay.network

import com.absensi.langitpay.abstraction.getDeviceUniqueId
import com.absensi.langitpay.network.response.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface Routes {

    @FormUrlEncoded
    @POST("request-login")
    fun login(
        @Field("username") username: String? = null,
        @Field("password") password: String? = null,
        @Field("device_uniq") deviceUniq: String? = null,
        @Field("token") token: String? = null
    ): Observable<User>

    @GET("/v1/revgeocode")
    fun getLocation(
        @Query("at") at: String,
        @Query("apiKey") apiKey: String
    ): Observable<GetLocation>

    @GET("autosuggest")
    fun getLocationSearch(
        @Query("at") lat: String,
        @Query("q") placeName: String,
        @Query("apikey") apiKey: String
    ): Observable<SearchLocation>

    @FormUrlEncoded
    @POST("request-update-token")
    fun updateToken(
        @Field("token") token: String,
        @Field("device_uniq") deviceUniq: String = getDeviceUniqueId(AbsentLangitPayAplication.getApplicationContext())
    ): Observable<User>


    @FormUrlEncoded
    @POST("get-notification")
    fun getNotification(
        @Field("user_id", encoded = true) userId: String? = null
    ): Observable<Notification>

    @FormUrlEncoded
    @POST("get-user")
    fun getUser(
        @Field("device_uniq") deviceUniq: String = getDeviceUniqueId(AbsentLangitPayAplication.getApplicationContext())
    ): Observable<User>

    @Multipart
    @POST("request-absen")
    fun requestAbsent(
        @Part("user_id") userId: RequestBody? = null,
        @Part("name") name: RequestBody? = null,
        @Part("type_absensi") typeAbsent: RequestBody? = null,
        @Part image: MultipartBody.Part? = null,
        @Part("address") address: RequestBody? = null,
        @Part("latitude") latitude: RequestBody? = null,
        @Part("longitude") longitude: RequestBody? = null,
        @Part("division") division: RequestBody? = null,
        @Part("noted") noted: RequestBody? = null,
        @Part("device_uniq") deviceUniq: RequestBody? = null
    ): Observable<RequestAbsent>

    @POST("get-OfficeLocation")
    fun getOfficeLocation() : Observable<GetOfficeLocation>
}