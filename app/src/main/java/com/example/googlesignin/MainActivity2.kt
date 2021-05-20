package com.example.googlesignin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class MainActivity2 : AppCompatActivity() {
    companion object{
        private const val TAG="MainActivity2"
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        auth = Firebase.auth

//        val name=intent.getStringExtra("Name")
//        val email=intent.getStringExtra("email")
        val textView:TextView=findViewById(R.id.textView)

        val imgView:ImageView=findViewById(R.id.imgView)



        val value :GoogleSignInAccount  = intent.getParcelableExtra("ACC")
        val name1=value.displayName
        val email1=value.email
//        val photo  = intent.getStringExtra("photo")
//        Glide.with(this).load(photo).into(imgView)
        textView.text="$name1 \n$email1"
        Picasso.with(this).load(value.photoUrl).into(imgView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.miLogout)
        {
            Log.i(TAG, "Logout")
            auth.signOut()
            val logoutIntent=Intent(this, MainActivity::class.java)
            logoutIntent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(logoutIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}