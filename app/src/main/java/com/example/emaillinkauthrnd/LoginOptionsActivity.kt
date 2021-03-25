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

class LoginOptionsActivity : AppCompatActivity() {


    private lateinit var mAuth: FirebaseAuth

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_options)

        mAuth = FirebaseAuth.getInstance()

        if (mAuth.currentUser != null) {
            finish()
            val intent2 = Intent(applicationContext, FeedActivity::class.java)
            startActivity(intent2)
            finish()
        } else {
            val lastSignedUser = GoogleSignIn.getLastSignedInAccount(this)
            if (lastSignedUser != null){
                btnGoogle.text = "${lastSignedUser.displayName} olarak giriş yap"
            }
            checkVerifyEmail()
        }

        btnEmail.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnGoogle.setOnClickListener {
            signInWithGoogle()
        }


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

                        val email = GlobalCache.userEmailAddress // Email should be fetched sharedPreferences. Because, if app is close, this email not found..

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
                                        Log.e(TAG, "Error signing in with email link", it.exception)

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