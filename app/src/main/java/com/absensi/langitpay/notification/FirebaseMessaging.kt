package com.absensi.langitpay.notification

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.logi
import com.absensi.langitpay.abstraction.route
import com.absensi.langitpay.network.BaseUrl
import com.absensi.langitpay.network.Network
import com.absensi.langitpay.network.SharedPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (SharedPref.getPrefToken().isNullOrEmpty()) {
            logi("fcm -> User is not logged in.")
        }

        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        val status = remoteMessage.data["status"]
        val millis = System.currentTimeMillis()
        logi("message is -> ${remoteMessage.data}")

        val builder = NotificationCompat.Builder(this, "LP-ABSENSI")
            .setSmallIcon(R.drawable.ic_langitpay_absensi)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.InboxStyle()
                    .addLine(status)
                    .addLine(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val intent = Intent(this, NotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentNotification = PendingIntent.getActivity(
            this,
            millis.toInt(),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        builder.setContentIntent(pendingIntentNotification)

        NotificationManagerCompat.from(this).run {
            notify(getRandomId(), builder.build())
        }
    }

    private fun getRandomId(): Int {
        return System.currentTimeMillis().toString().takeLast(4).toInt()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        route(Network.getRoutes(BaseUrl.BASE_URL).updateToken(token),result = {
            logi("update token is -> ${it.message}")
        })
    }
}