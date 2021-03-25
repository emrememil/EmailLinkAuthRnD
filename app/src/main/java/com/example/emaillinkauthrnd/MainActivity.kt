package com.example.emaillinkauthrnd

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.actionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.logging.Logger

class MainActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        val actionCodeSettings = ActionCodeSettings.newBuilder()
            .setUrl("https://google.com/finishSignUp")
            .setHandleCodeInApp(true)
            .setAndroidPackageName("com.example.emaillinkauthrnd", true, null)
            .build()



        btnNext.setOnClickListener {
            if (Patterns.EMAIL_ADDRESS.matcher(mail.text.toString()).matches()) {
                mAuth.sendSignInLinkToEmail(mail.text.toString().trim(), actionCodeSettings)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            GlobalCache.userEmailAddress = mail.text.toString()
                            Log.d(TAG, "Email sent.")

                            val intent = Intent(this,MailSentActivity::class.java)
                            startActivity(intent)
                        }
                    }
            } else {
                Toast.makeText(this, "Invalid Mail Address", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}