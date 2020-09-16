package com.absensi.langitpay.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.absensi.langitpay.R
import com.absensi.langitpay.absen.ConfirmationAbsenActivity
import com.absensi.langitpay.abstraction.clicked
import com.absensi.langitpay.abstraction.intentTo
import com.absensi.langitpay.network.SharedPref
import com.absensi.langitpay.notification.NotificationActivity
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_username.text = SharedPref.getValue(resources.getString(R.string.pref_user_name))
        text_devision.text = SharedPref.getValue(resources.getString(R.string.pref_user_division))
        text_email.text = SharedPref.getValue(resources.getString(R.string.pref_user_email))
        text_nik.text = SharedPref.getValue(resources.getString(R.string.pref_user_nik))
        btn_notification.clicked {
            intentTo(NotificationActivity::class.java)
        }
        btn_absen.clicked {
            intentTo(ConfirmationAbsenActivity::class.java)
        }
    }
}
