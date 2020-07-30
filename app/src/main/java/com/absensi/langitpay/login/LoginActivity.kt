package com.absensi.langitpay.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.absensi.langitpay.MainActivity
import com.absensi.langitpay.R
import com.absensi.langitpay.network.Network
import com.absensi.langitpay.network.SharedPref
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import java.util.UUID
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val composite = CompositeDisposable()
    private val uniqueID: String = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bt_login.setOnClickListener {
            setLogin()
        }
    }

    private fun setLogin() {
        composite += et_username.textChanges()
            .subscribeOn(Schedulers.io())
            .map { it.toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ username ->
                if (username.length > 4) {
                    composite += et_password.textChanges()
                        .subscribeOn(Schedulers.io())
                        .map { it.toString() }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ password ->
                            if (password.length > 4) {
                                requestLogin(username,password)
                            }else{
                                text_input_password.error = "Password harus lebih dari 4 karakter"
                            }
                        }, {
                            it.printStackTrace()
                        })
                } else {
                    text_input_username.error = "username harus lebih dari 4 karakter"
                }
            }, {
                it.printStackTrace()
            })
    }

    private fun requestLogin(username: String, password: String) {
        composite += Network.getRoutes().login(username, password, uniqueID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ login ->
                if (login.status) {
                    SharedPref.savePrefDeviceUniqId(login.data?.deviceUniq)
                    SharedPref.saveValue(
                        resources.getString(R.string.pref_username),
                        login.data?.name
                    )
                    SharedPref.saveValue(
                        resources.getString(R.string.pref_password),
                        login.data?.password
                    )
                    SharedPref.savePrefDeviceUniqId(login.data?.deviceUniq)
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }, {
                it.printStackTrace()
            })
    }
}
