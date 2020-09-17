package com.absensi.langitpay.home

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.absensi.langitpay.R
import com.absensi.langitpay.absen.ConfirmationAbsenActivity
import com.absensi.langitpay.abstraction.clicked
import com.absensi.langitpay.abstraction.intentTo
import com.absensi.langitpay.network.SharedPref
import com.absensi.langitpay.notification.NotificationActivity
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    private val viewModel : HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
        userHandle()

        btn_notification.clicked {
            intentTo(NotificationActivity::class.java)
        }
        btn_absen.clicked {
            intentTo(ConfirmationAbsenActivity::class.java)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("LP-ABSENSI", "default", importance).apply {
                description = "This is notification default"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun userHandle (){
        viewModel.getUser().observe(this, Observer {
            val data = it.data
            if (data != null){
                text_username.text = data.name
                text_devision.text = data.division
                text_email.text = data.email
                text_nik.text = data.nik.toString()
            }else{
                text_username.text = SharedPref.getValue(resources.getString(R.string.pref_user_name))
                text_devision.text = SharedPref.getValue(resources.getString(R.string.pref_user_division))
                text_email.text = SharedPref.getValue(resources.getString(R.string.pref_user_email))
                text_nik.text = SharedPref.getValue(resources.getString(R.string.pref_user_nik))
            }
        })
    }
}
