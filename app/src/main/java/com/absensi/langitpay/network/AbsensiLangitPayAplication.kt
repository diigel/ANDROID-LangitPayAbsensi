package com.absensi.langitpay.network

import android.app.Application
import android.content.Context
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.toast

class AbsensiLangitPayAplication : Application() {

    companion object {
        lateinit var instance: AbsensiLangitPayAplication
        fun getApplicationContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        instance = this
        super.onCreate()

    }
}