package com.absensi.langitpay

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.absensi.langitpay.absen.ConfirmationAbsenActivity
import com.absensi.langitpay.abstraction.clicked
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_absen.clicked {
            startActivity(Intent(this,ConfirmationAbsenActivity::class.java))
        }
    }
}
