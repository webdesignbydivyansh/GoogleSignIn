package com.example.googlesignin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.HttpMethod
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class LogoutActivity : AppCompatActivity() {
    var firebaseAuth:FirebaseAuth?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logout)

        val user=firebaseAuth?.currentUser
        val tvNmae:TextView=findViewById(R.id.tvName)
        val tvEmail:TextView=findViewById(R.id.tvEmail)
        val pic:ImageView=findViewById(R.id.imgageview)
        tvNmae.text=user?.displayName
        tvEmail.text=user?.email
        Picasso.with(this).load(user?.photoUrl).into(pic)

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener(View.OnClickListener {
            // Logout
            if (AccessToken.getCurrentAccessToken() != null) {
                GraphRequest(
                    AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE,
                    GraphRequest.Callback {
                        AccessToken.setCurrentAccessToken(null)
                        LoginManager.getInstance().logOut()
                        startActivity(Intent(this,MainActivity::class.java))
                    }
                ).executeAsync()
            }
        })
    }
}