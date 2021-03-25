package com.example.emaillinkauthrnd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null){
           tvEmail.text = currentUser.email
        }else{
            tvEmail.text = "user not found.."
        }

        btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            finish()
            val intent = Intent(this, LoginOptionsActivity::class.java)
            startActivity(intent)
        }
    }
}