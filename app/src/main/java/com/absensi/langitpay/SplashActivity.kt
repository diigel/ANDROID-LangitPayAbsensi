package com.absensi.langitpay

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.absensi.langitpay.abstraction.intentTo
import com.absensi.langitpay.home.HomeActivity
import com.absensi.langitpay.login.LoginActivity
import com.absensi.langitpay.network.SharedPref

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (!SharedPref.getPrefDeviceUniqId().isNullOrEmpty()) {
                intentTo(HomeActivity::class.java)
                finish()
            } else {
                intentTo(LoginActivity::class.java)
                finish()
            }
        }, 3000)
    }
}