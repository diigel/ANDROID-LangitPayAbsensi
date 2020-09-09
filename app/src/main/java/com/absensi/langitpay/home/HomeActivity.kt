package com.absensi.langitpay.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.absensi.langitpay.R
import com.absensi.langitpay.absen.ConfirmationAbsenActivity
import com.absensi.langitpay.abstraction.clicked
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView(){
        btn_notification.clicked {
            startActivity(Intent(this,NotificationActivity::class.java))
        }
        btn_absen.clicked {
            startActivity(Intent(this,ConfirmationAbsenActivity::class.java))
        }
    }
}
