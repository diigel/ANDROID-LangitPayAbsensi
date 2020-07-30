package com.absensi.langitpay.abstraction

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.absensi.langitpay.R
import com.google.android.material.bottomsheet.BottomSheetDialog


fun Context.toast(msg: String?) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
fun Fragment.toast(msg: String?) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
fun View.toast(msg: String?) = Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

fun logi(msg: String?) = Log.d("LANGITPAY_LOG_INFO", msg)
fun loge(msg: String?) = Log.e("LANGITPAY_LOG_INFO", msg)

@SuppressLint("HardwareIds")
fun getDeviceUniqueId(context: Context): String =
    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

@SuppressLint("CheckResult")
fun View.clicked(action: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        action(it)

        try {
            val idResource = it.resources.getResourceEntryName(it.id)
            logi("--- id clicked -> $idResource ---")
        } catch (e: Resources.NotFoundException) {
            logi("--- id clicked -> not found ---")
        }
    }
    setOnClickListener(safeClickListener)
}

fun Context.showDialogInfo(){
    val dialog = BottomSheetDialog(this)
    val sheetView = LayoutInflater.from(this).inflate(R.layout.dialog_info, null)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    dialog.setCancelable(true)
    dialog.setContentView(sheetView)
}