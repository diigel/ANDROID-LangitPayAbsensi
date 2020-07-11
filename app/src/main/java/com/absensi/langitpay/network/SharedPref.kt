package com.absensi.langitpay.network

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

object SharedPref {
    private const val KEY_PREFS = "KEY_PREFS"
    private const val KEY_AUTH_DEVICE_UNIQ_ID = "KEY_AUTH_DEVICE_UNIQ_ID"
    private const val KEY_AUTH_DEVICE_DEV = "KEY_AUTH_DEVICE_DEV"

    private fun getApplication() = AbsensiLangitPayAplication.getApplicationContext()
    private fun sharedPrefs() = getApplication().getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE)

    fun savePrefDeviceUniqId(deviceUniqId: String?) {
        val editor = sharedPrefs().edit()
        editor.putString(KEY_AUTH_DEVICE_UNIQ_ID, deviceUniqId).apply()
    }

    fun savePrefDeviceDev(deviceUniqId: String?) {
        val editor = sharedPrefs().edit()
        editor.putString(KEY_AUTH_DEVICE_DEV, deviceUniqId).apply()
    }

    fun getPrefDeviceUniqId(): String? = sharedPrefs().getString(KEY_AUTH_DEVICE_UNIQ_ID, "")

    fun getPrefhDeviceDev(): String? = sharedPrefs().getString(KEY_AUTH_DEVICE_DEV, "")
    private val PREFS_NAME = "MR_PREFS"

    fun saveValue(key: String, value: String?) {
        val settings: SharedPreferences = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor
        editor = settings.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getValue(key: String): String? {
        val settings: SharedPreferences = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val text: String?
        text = settings.getString(key, null)
        return text
    }

    fun clearValue() {
        val settings: SharedPreferences = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor
        editor = settings.edit()
        editor.clear()
        editor.apply()
    }

    fun removeValue(key: String) {
        val settings: SharedPreferences = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor
        editor = settings.edit()
        editor.remove(key)
        editor.apply()
    }
}