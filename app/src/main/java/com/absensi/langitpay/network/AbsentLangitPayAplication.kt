package com.absensi.langitpay.network

import android.app.Application
import android.content.Context

class AbsentLangitPayAplication : Application() {

    companion object {
        lateinit var instance: AbsentLangitPayAplication
        fun getApplicationContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        instance = this
        super.onCreate()

    }
}