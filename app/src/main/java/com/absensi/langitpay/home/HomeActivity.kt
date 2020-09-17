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
import com.absensi.langitpay.absent.ConfirmationAbsenActivity
import com.absensi.langitpay.abstraction.clicked
import com.absensi.langitpay.abstraction.intentTo
import com.absensi.langitpay.abstraction.loaderDialog
import com.absensi.langitpay.network.SharedPref
import com.absensi.langitpay.network.response.DataUser
import com.absensi.langitpay.notification.NotificationActivity
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity() {

    private val viewModel : HomeViewModel by viewModels()
    private val loader by lazy {
        loaderDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loader.show()
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
        if (requestSaveShared()){
            loader.dismiss()
            text_username.text = SharedPref.getValue(resources.getString(R.string.pref_user_name))
            text_devision.text = SharedPref.getValue(resources.getString(R.string.pref_user_division))
            text_email.text = SharedPref.getValue(resources.getString(R.string.pref_user_email))
            text_nik.text = SharedPref.getValue(resources.getString(R.string.pref_user_nik))
        }else{
            viewModel.getUser().observe(this, Observer {
                loader.dismiss()
                val data = it.data
                if (data != null){
                    text_username.text = data.name
                    text_devision.text = data.division
                    text_email.text = data.email
                    text_nik.text = data.nik.toString()
                    requestSaveShared(data)
                }
            })
        }
    }

    private fun requestSaveShared(data : DataUser? = null) : Boolean{
        SharedPref.saveValue(resources.getString(R.string.pref_user_name),data?.name)
        SharedPref.saveValue(resources.getString(R.string.pref_user_division),data?.division)
        SharedPref.saveValue(resources.getString(R.string.pref_user_email),data?.email)
        SharedPref.saveValue(resources.getString(R.string.pref_user_nik),data?.nik.toString())
        SharedPref.saveValue(resources.getString(R.string.pref_user_gender),data?.gender)
        SharedPref.saveValue(resources.getString(R.string.pref_user_id),data?.id.toString())
        SharedPref.saveValue(resources.getString(R.string.pref_user_password),data?.password)
        return data != null
    }
}
