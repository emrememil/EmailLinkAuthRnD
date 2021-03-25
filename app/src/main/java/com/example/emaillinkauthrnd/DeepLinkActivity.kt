package com.example.emaillinkauthrnd

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

class DeepLinkActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deep_link)

        Log.e(TAG,"work")

        mAuth = FirebaseAuth.getInstance()

        checkVerifyEmail()


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkVerifyEmail()
        Log.e(TAG," on new intent work")
    }

    private fun checkVerifyEmail() {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) {
                val deepLink: Uri?
                if (it != null) {
                    deepLink = it.link
                    Log.e(TAG, deepLink?.path.toString())


                    val intent = intent
                    val emailLink = intent.data.toString()

                    if (mAuth.isSignInWithEmailLink(emailLink)) {
                        Log.e(TAG, "worked, $emailLink")
                        Toast.makeText(this,"Worked: $emailLink", Toast.LENGTH_LONG).show()

                        val email = GlobalCache.userEmailAddress //
                        if (email != null) {
                            mAuth.signInWithEmailLink(email, emailLink)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        Log.e(TAG, "Successfully signed in with email link!")
                                        val result = it.result

                                        Log.e(TAG, result?.user?.email.toString())
                                        Log.e(TAG, result?.user?.uid.toString())

                                        val intent2 = Intent(this, FeedActivity::class.java)
                                        startActivity(intent2)
                                        finish()
                                    } else {
                                        Log.e(LoginOptionsActivity.TAG, "Error signing in with email link", it.exception)

                                        Toast.makeText(
                                            this,
                                            "Bir hatayla karşılaşıldı. Lütfen tekrar deneyin",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    } else {
                        Log.e(TAG, "not worked ")
                    }
                } else {
                    Log.e(TAG, "deeplink null")
                }
            }
            .addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }

    }

    companion object {
        const val TAG = "DeepLinkActivity"

        fun startActivity(context: Context) {
            Intent(context, DeepLinkActivity::class.java).run {
                context.startActivity(this)
            }
        }
    }
}