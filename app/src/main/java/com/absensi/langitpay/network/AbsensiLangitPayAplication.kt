package com.absensi.langitpay.network

import android.app.Application
import android.content.Context

class AbsensiLangitPayAplication : Application() {

    companion object {
        lateinit var instance: AbsensiLangitPayAplication
        fun getApplicationContext() = instance.applicationContext
    }

    override fun getApplicationContext(): Context {
        instance = this
        return super.getApplicationContext()
    }
}