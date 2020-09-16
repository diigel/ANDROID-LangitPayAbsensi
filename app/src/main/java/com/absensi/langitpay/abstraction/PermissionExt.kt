package com.absensi.langitpay.abstraction

import android.app.Activity
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

fun Fragment.withPermissions(listPermission: List<String>, action: (granted: Boolean) -> Unit) {
    Dexter.withActivity(activity)
        .withPermissions(listPermission)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    action.invoke(true)
                } else {
                    action.invoke(false)
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }

        })
        .check()
}

fun Fragment.withPermission(permission: String, action: (granted: Boolean) -> Unit) {
    Dexter.withActivity(activity)
        .withPermission(permission)
        .withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                action.invoke(true)
            }

            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                action.invoke(false)
            }

        })
        .check()
}

fun Activity.withPermissions(listPermission: List<String>, action: (grantedList: List<String>, deniedList: List<String>) -> Unit) {
    Dexter.withActivity(this)
        .withPermissions(listPermission)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    action.invoke(
                        report.grantedPermissionResponses.map { it.permissionName },
                        report.deniedPermissionResponses.map { it.permissionName })
                } else {
                    toast("Kamu harus menginzinkan jika ingin melanjutkan")
                }
            }

            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }

        })
        .check()
}

fun Activity.withPermission(permission: String, action: (granted: Boolean) -> Unit) {
    Dexter.withActivity(this)
        .withPermission(permission)
        .withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                action.invoke(true)
            }

            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                token?.continuePermissionRequest()
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                action.invoke(false)
            }

        })
        .check()
}