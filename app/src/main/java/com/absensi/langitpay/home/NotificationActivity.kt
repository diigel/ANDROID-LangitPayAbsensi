package com.absensi.langitpay.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.absensi.langitpay.R
import kotlinx.android.synthetic.main.activity_notification.*

class NotificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        initView()
    }

    private fun initView(){
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}