package com.absensi.langitpay.network

import android.content.Context
import com.absensi.langitpay.BuildConfig
import com.absensi.langitpay.R
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Network {

    private val context: Context = AbsensiLangitPayAplication.getApplicationContext().applicationContext
    private fun provideLoggingInterceptor(): HttpLoggingInterceptor {

        /**
         * @BODY if you need show all response
         * @BASIC only show end_point response
         * @NONE nothing
         * */
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
        return loggingInterceptor
    }

    private fun provideOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(provideLoggingInterceptor())
            .retryOnConnectionFailure(false)
            .callTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(1, TimeUnit.MINUTES)

        return builder.build()
    }

    private fun provideRetrofit(): Retrofit {

        /**
         * setFieldNamingPolicy()
         * for convert lowercase with underscores
         * json:`user_name`, you can use `userName` as variable
         */
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setLenient()
            .setPrettyPrinting()
            .create()

        val builder = Retrofit.Builder()
            .baseUrl(context.resources.getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(provideOkHttpClient())

        return builder.build()
    }

    fun getRoutes(): Routes = provideRetrofit().create(Routes::class.java)
}