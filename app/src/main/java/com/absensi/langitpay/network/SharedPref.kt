package com.absensi.langitpay.network

import android.content.Context
import android.content.SharedPreferences

object SharedPref {
    private const val KEY_PREFS = "KEY_PREFS"
    private const val KEY_AUTH_DEVICE_UNIQ_ID = "KEY_AUTH_DEVICE_UNIQ_ID"
    private const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
    private const val PREFS_NAME = "MR_PREFS"

    private fun getApplication() = AbsentLangitPayAplication.getApplicationContext()
    private fun sharedPrefs() = getApplication().getSharedPreferences(
        KEY_PREFS,
        Context.MODE_PRIVATE
    )

    fun savePrefDeviceUniqId(deviceUniqId: String?) {
        val editor = sharedPrefs().edit()
        editor.putString(KEY_AUTH_DEVICE_UNIQ_ID, deviceUniqId).apply()
    }

    fun savePrefToken(token: String?) {
        val editor = sharedPrefs().edit()
        editor.putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getPrefDeviceUniqId(): String? = sharedPrefs().getString(KEY_AUTH_DEVICE_UNIQ_ID, "")

    fun getPrefToken(): String? = sharedPrefs().getString(KEY_AUTH_TOKEN, "")

    fun saveValue(key: String, value: String?) {
        val settings: SharedPreferences = getApplication().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor
        editor = settings.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getValue(key: String): String? {
        val settings: SharedPreferences = getApplication().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val text: String?
        text = settings.getString(key, null)
        return text
    }

    fun clearValue() {
        val settings: SharedPreferences = getApplication().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor
        editor = settings.edit()
        editor.clear()
        editor.apply()
    }

    fun removeValue(key: String) {
        val settings: SharedPreferences = getApplication().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor
        editor = settings.edit()
        editor.remove(key)
        editor.apply()
    }

//    fun getAllSharedPref(): MutableList<SharedModel> {
//        val list: MutableList<SharedModel> = mutableListOf()
//        val settings: SharedPreferences = getApplication().getSharedPreferences(
//            PREFS_NAME,
//            Context.MODE_PRIVATE
//        )
//        val allKey= settings.all.map { it.key }
//        val allValue = settings.all.map { it.value }
//        list.clear()
//        list.add(SharedModel(allKey,allValue))
//
//        return list
//    }
//
//    data class SharedModel(
//        val key: List<String> = listOf(),
//        val value: List<Any?> = listOf()
//    )

    fun getAllValueSharedPref() : String {
        val settings: SharedPreferences = getApplication().getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )

        return settings.all.map { it.value }.toString()
    }
}