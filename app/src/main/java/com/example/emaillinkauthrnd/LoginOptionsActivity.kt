package com.example.emaillinkauthrnd

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login_options.*
import java.lang.Exception
import java.util.logging.Logger

class LoginOptionsActivity : AppCompatActivity() {


    private lateinit var mAuth: FirebaseAuth

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_options)

        Log.e(TAG, "onCreate work")

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            Log.e(TAG, "current user not null")

            val intent2 = Intent(applicationContext, FeedActivity::class.java)
            startActivity(intent2)
            finish()
        } else {
            Log.e(TAG, "current user is null")
            val lastSignedUser = GoogleSignIn.getLastSignedInAccount(this)
            if (lastSignedUser != null) {
                btnGoogle.text = "${lastSignedUser.displayName} olarak giriş yap"
            }
            //checkVerifyEmail()
        }

        handleIntent(intent)

        btnEmail.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnGoogle.setOnClickListener {
            signInWithGoogle()
        }


    }

    private fun handleIntent(intent: Intent) {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                val deepLink: Uri?
                Log.e(TAG, "pending")

                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    val intent = intent
                    val link = intent.data.toString()

                    if (link.contains("bizdeappuserlogin")) {

                        if (mAuth.isSignInWithEmailLink(link)) {
                            Log.e(TAG, "worked, $link")
                            Toast.makeText(this, "Worked: $link", Toast.LENGTH_LONG).show()

                            val email =
                                GlobalCache.userEmailAddress // Email should be fetched sharedPreferences. Because, if app is close, this email not found..

                            if (email != null) {
                                mAuth.signInWithEmailLink(email, link)
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
                                            Log.e(
                                                TAG,
                                                "Error signing in with email link",
                                                it.exception
                                            )

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
                        var contentLink = deepLink?.path.toString()
                        contentLink = contentLink.substring(contentLink.lastIndexOf("=") + 1)
                        ItemDetailsActivity.startActivity(this, contentLink)
                        finish()
                    }
                } else {
                    Log.e(TAG, "deeplink null")
                }
            }.addOnFailureListener(this) { e -> Log.w(TAG, "getDynamicLink:onFailure", e) }

    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, SIGN_IN_GOOGLE_REQ_CODE)

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            handleIntent(it)
        }
        Log.e(TAG, "new intent tetiklendi")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SIGN_IN_GOOGLE_REQ_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI(account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    companion object {
        const val TAG = "LoginOptionsActivity"

        const val SIGN_IN_GOOGLE_REQ_CODE = 100
    }
}