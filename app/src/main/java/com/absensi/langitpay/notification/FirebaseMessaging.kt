package com.absensi.langitpay.notification

import androidx.core.app.NotificationCompat
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.logi
import com.absensi.langitpay.abstraction.route
import com.absensi.langitpay.network.BaseUrl
import com.absensi.langitpay.network.Network
import com.absensi.langitpay.network.SharedPref
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.disposables.CompositeDisposable

class FirebaseMessaging : FirebaseMessagingService() {

    private val compositeDisposable = CompositeDisposable()
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (SharedPref.getPrefToken().isNullOrEmpty()) {
            logi("fcm -> User is not logged in.")
        }

        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]
        logi("message is -> ${remoteMessage.data}")

        val builder = NotificationCompat.Builder(this, "LP-DEFAULT")
            .setSmallIcon(R.drawable.ic_langitpay_absensi)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        route(Network.getRoutes(BaseUrl.BASE_URL).updateToken(token),result = {
            logi("update token is -> ${it.message}")
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}