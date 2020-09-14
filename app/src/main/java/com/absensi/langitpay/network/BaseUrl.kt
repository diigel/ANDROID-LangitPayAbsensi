package com.absensi.langitpay.network

import android.content.Context
import com.absensi.langitpay.R

enum class BaseUrl {
    BASE_URL,
    MAPS_URL,
    MAPS_SEARCH_URL
}

object GetBaseUrl {
    private val context: Context = AbsensiLangitPayAplication.getApplicationContext().applicationContext
    val baseUrl = context.resources.getString(R.string.base_url)
    val mapsUrl = context.resources.getString(R.string.maps_url)
    val mapsSearchUrl = context.resources.getString(R.string.maps_search_url)
}