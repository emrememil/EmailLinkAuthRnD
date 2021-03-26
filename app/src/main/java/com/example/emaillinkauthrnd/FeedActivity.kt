package com.example.emaillinkauthrnd

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_feed.*

class FeedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            tvEmail.text = currentUser.email
        } else {
            tvEmail.text = "user not found.."
        }

        btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            finish()
            val intent = Intent(this, LoginOptionsActivity::class.java)
            startActivity(intent)
        }

        val list = ArrayList<String>()
        list.add("Content1")
        list.add("Content2")
        list.add("Content3")
        list.add("Content4")
        list.add("Content5")
        val itemAdapter = ItemAdapter(list)

        rvItems.layoutManager = LinearLayoutManager(this)
        rvItems.adapter = itemAdapter

        itemAdapter.setOnItemClickListener {

            createLink(it)
        }
    }


    private fun createLink(id: String) {

        val shareLinkText = "https://testt2tt.page.link/?"+
                "link=https://www.google.com/contents/contentid=$id"+
                "&apn="+ packageName +
                "&st="+"$id"+ //title
                "&sd="+"$id - will be description here"+ //description
                "&si="+"https://www.logolynx.com/images/logolynx/b9/b93487dcfb8da30114817422077bf2d4.jpeg" //logo

        Log.e(TAG, "shareLinkText: $shareLinkText")

        createShortLink(Uri.parse(shareLinkText)) // Manual Link
    }

    private fun createShortLink(dynamicLinkUri: Uri) {
        Firebase.dynamicLinks.createDynamicLink()
            .setLongLink(dynamicLinkUri)
            .buildShortDynamicLink()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val shortLink = it.result?.shortLink
                    val flowchartLink = it.result?.previewLink

                    Log.e(TAG, "short link: $shortLink")
                    Log.e(TAG, "flowchart link: $flowchartLink")

                    val intent = Intent()
                    intent.action = Intent.ACTION_SEND
                    intent.putExtra(Intent.EXTRA_TEXT,shortLink.toString())
                    intent.type = "text/plain"
                    startActivity(intent)
                } else {
                    Log.e(TAG, "error" + it.exception)
                }
            }
    }

    companion object {
        const val TAG = "FeedActivity"
    }
}