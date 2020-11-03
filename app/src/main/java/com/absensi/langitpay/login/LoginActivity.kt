package com.absensi.langitpay.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.*
import com.absensi.langitpay.home.HomeActivity
import com.absensi.langitpay.network.BaseUrl
import com.absensi.langitpay.network.Network
import com.absensi.langitpay.network.SharedPref
import com.absensi.langitpay.network.response.User
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val composite = CompositeDisposable()

    private val isValidArray = mutableListOf("", "")

    private val loader by lazy {
        loaderDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupTextInput()
        btn_login.clicked {
            requestLogin(
                isValidArray[0],
                isValidArray[1]
            )
        }
    }

    private fun setupTextInput() {
        composite += et_username.textChanges()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.toString() }
            .subscribe({ username ->
                when  {
                    username.length in 1..3 -> {
                        isValidArray[0] = ""
                        username_text_alert.error = "Username harus lebih dari 4 karakter"
                    }
                    username.length >= 4 -> {
                        isValidArray[0] = username
                        username_text_alert.error = null
                    }
                }
                checkEnableButtonNext()
            }, {
                it.printStackTrace()
            })

        composite += et_password.textChanges()
            .subscribeOn(Schedulers.io())
            .map { it.toString() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ password ->
                when  {
                    password.length in 1..5 -> {
                        isValidArray[1] = ""
                        password_text_alert.error = "Password harus lebih dari 6 karakter"
                    }
                    password.length >= 6 -> {
                        isValidArray[1] = password
                        password_text_alert.error = null
                    }
                }
                checkEnableButtonNext()
            }, {
                it.printStackTrace()
            })
    }

    private fun requestLogin(username: String, password: String) {
        loader.show()
        getToken { token ->
            composite += Network.getRoutes(BaseUrl.BASE_URL)
                .login(username, password, getDeviceUniqueId(this), token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ login ->
                    loader.dismiss()
                    if (login.status) {
                        requestSavePref(token,login)
                        logi("data login is ->${login.data}")
                        intentTo(HomeActivity::class.java)
                    } else {
                        showDialogInfo(login.message)
                    }
                }, {
                    loader.dismiss()
                    showDialogInfo("Maaf terjadi kesalahan")
                    it.printStackTrace()
                })
        }
    }

    private fun requestSavePref(token : String, login: User){
        SharedPref.savePrefDeviceUniqId(login.data?.deviceUniq)
        SharedPref.savePrefToken(token)
        SharedPref.saveValue(
            resources.getString(R.string.pref_user_division),
            login.data?.division
        )
        SharedPref.saveValue(
            resources.getString(R.string.pref_user_name),
            login.data?.name
        )
        SharedPref.saveValue(
            resources.getString(R.string.pref_user_password),
            login.data?.password
        )
        SharedPref.saveValue(
            resources.getString(R.string.pref_id_user),
            login.data?.id.toString()
        )
        SharedPref.saveValue(
            resources.getString(R.string.pref_user_nik),
            login.data?.nik.toString()
        )
        SharedPref.saveValue(
            resources.getString(R.string.pref_user_gender),
            login.data?.gender
        )
        SharedPref.saveValue(
            resources.getString(R.string.pref_user_email),
            login.data?.email
        )
    }

    private fun checkEnableButtonNext() {
        btn_login.isEnabled = !isValidArray.contains("")
    }
}
