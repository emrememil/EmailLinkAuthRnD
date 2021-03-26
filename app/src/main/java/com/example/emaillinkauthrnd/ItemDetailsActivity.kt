package com.example.emaillinkauthrnd

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_item_details.*

class ItemDetailsActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_details)

        mAuth = FirebaseAuth.getInstance()

        tvItemDetails.text = "Contains the details of "+ intent?.getStringExtra(EXTRA_CONTENT_ID) + " Here.."
    }

    companion object {
        const val TAG = "ItemDetailsActivity"
        private const val EXTRA_CONTENT_ID = "itemContentID"

        fun startActivity(context: Context, item:String) {
            val intent = Intent(context,ItemDetailsActivity::class.java)
            intent.putExtra(EXTRA_CONTENT_ID,item)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()

        if (mAuth.currentUser != null) {
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginOptionsActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}