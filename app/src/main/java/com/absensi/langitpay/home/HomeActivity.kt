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
import com.absensi.langitpay.abstraction.showDialogInfo
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
        if (SharedPref.getPrefDeviceUniqId().isNullOrEmpty()){
            loader.dismiss()
            viewModel.getUser().observe(this, Observer {data ->
                loader.dismiss()
                if (data != null){
                    setupView(
                        data.name,
                        data.division,
                        data.email,
                        data.nik.toString()
                    )
                    requestSaveShared(data)
                }else {
                    showDialogInfo("Maaf Terjadi Kesalahan")
                }
            })
        }else{
            loader.dismiss()
            setupView(
                SharedPref.getValue(resources.getString(R.string.pref_user_name)),
                SharedPref.getValue(resources.getString(R.string.pref_user_division)),
                SharedPref.getValue(resources.getString(R.string.pref_user_email)),
                SharedPref.getValue(resources.getString(R.string.pref_user_nik))
            )
        }
    }

    private fun requestSaveShared(data : DataUser? = null) {
        SharedPref.saveValue(resources.getString(R.string.pref_user_name),data?.name)
        SharedPref.saveValue(resources.getString(R.string.pref_user_division),data?.division)
        SharedPref.saveValue(resources.getString(R.string.pref_user_email),data?.email)
        SharedPref.saveValue(resources.getString(R.string.pref_user_nik),data?.nik.toString())
        SharedPref.saveValue(resources.getString(R.string.pref_user_gender),data?.gender)
        SharedPref.saveValue(resources.getString(R.string.pref_user_id),data?.id.toString())
        SharedPref.saveValue(resources.getString(R.string.pref_user_password),data?.password)
    }


    private fun setupView(name : String?, division : String?,email : String?,nik : String?){
        text_username.text = name
        text_devision.text = division
        text_email.text = email
        text_nik.text = nik
    }
}
