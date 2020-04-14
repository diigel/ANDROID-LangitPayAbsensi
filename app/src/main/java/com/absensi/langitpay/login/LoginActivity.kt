package com.absensi.langitpay.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.absensi.langitpay.MainActivity
import com.absensi.langitpay.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        bt_login.setOnClickListener {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(et_email.text.toString().trim(),et_password.text.toString().trim())
                .addOnCompleteListener{
                    return@addOnCompleteListener if (it.isSuccessful){
                        startActivity(Intent(this,MainActivity::class.java))
                    }
                    else
                        Toast.makeText(this, "Succesfully Login", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Log.d("Main", "Failed Login: ${it.message}")
                    Toast.makeText(this, "Email/Password incorrect", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
