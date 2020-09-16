package com.absensi.langitpay.abstraction

import com.google.firebase.iid.FirebaseInstanceId

fun getToken(token: (String) -> Unit) {
    FirebaseInstanceId.getInstance()
        .instanceId
        .addOnSuccessListener {task ->
            val resultToken = task?.token
            resultToken?.let { token(it) }
            logi("token is -> $resultToken")
        }
        .addOnCanceledListener {
            logi("canceled")
        }
        .addOnFailureListener {
            it.printStackTrace()
        }
}