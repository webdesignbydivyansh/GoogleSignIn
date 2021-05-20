package com.example.googlesignin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.*
import com.google.firebase.ktx.Firebase


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private companion object{
        private const val TAG="MainActivity"
        private const val RC_GOOGLE_SIGN_IN=4926
    }
    private lateinit var auth: FirebaseAuth
// ...
    var FirebaseAuth:FirebaseAuth?=null
    lateinit var callbackManager:CallbackManager
// Initialize Firebase Auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = Firebase.auth
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        val client:GoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val btnSignIn: SignInButton =findViewById(R.id.btnSignIn)
        btnSignIn.setOnClickListener{
            val signInIntent=client.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)

        }

        FirebaseAuth= com.google.firebase.auth.FirebaseAuth.getInstance()

//
//        val btn_login:com.facebook.login.widget.LoginButton=findViewById(R.id.btn_login)
//        btn_login.setReadPermissions("email")
//        btn_login.setOnContextClickListener {
//            signIn()
//        }
        callbackManager= CallbackManager.Factory.create()

        // If you are using in a fragment, call loginButton.setFragment(this)

        // Callback registration
        val loginButton = findViewById<LoginButton>(R.id.login_button)
        loginButton.setReadPermissions(listOf("public_profile", "email"))


        // Callback registration
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.d("TAG", "Success Login")
                // Get User's Info
//                getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)
                handleFacebookAccessToken(loginResult?.accessToken)
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_LONG).show()
            }
        })


//        if (isLoggedIn()) {

//            startActivity(Intent(this, LogoutActivity::class.java))
//            finish()

            // Show the Activity with the logged in user
//        }else{
//            return
            // Show the Home Activity
//        }

    }


//    private fun signIn() {
//        val btn_login: com.facebook.login.widget.LoginButton = findViewById(R.id.btn_login)
//        btn_login.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
//            override fun onSuccess(result: LoginResult?) {
//                handleFacebookAccessToken(result!!.accessToken)
//            }
//
//            override fun onCancel() {
//                TODO("Not yet implemented")
//            }
//
//            override fun onError(error: FacebookException?) {
//                TODO("Not yet implemented")
//            }
//
//        })
//    }


    private fun handleFacebookAccessToken(accessToken: AccessToken?) {
        Log.d(TAG, "handleFacebookAccessToken:$accessToken")
        val credential = FacebookAuthProvider.getCredential(accessToken!!.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun getUserProfile(){
        val user = Firebase.auth.currentUser
        val account: GoogleSignInAccount? =GoogleSignIn.getLastSignedInAccount(this)
        val i=Intent(this, MainActivity2::class.java)
        i.putExtra("ACC",account)
        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            // Check if user's email is verified
            val emailVerified = user.isEmailVerified

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            val uid = user.uid
//            i.putExtra("Name", name)
//            i.putExtra("email", email)
//            i.putExtra("photo", photoUrl)

        }
        startActivity(i)
    }

    private fun updateUI(user: FirebaseUser?){
        if(user==null)
        {
            Log.w(TAG, "User is null, try again!")
            return
        }
        getUserProfile()
        finish()
    }
    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {

        val parameters = Bundle()
        parameters.putString(
                "fields",
                "id, first_name, middle_name, last_name, name, picture, email"
        )
        GraphRequest(token,
                "/$userId/",
                parameters,
                HttpMethod.GET,
                GraphRequest.Callback { response ->
                    val jsonObject = response.jsonObject

                    // Facebook Access Token
                    // You can see Access Token only in Debug mode.
                    // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
                    if (BuildConfig.DEBUG) {
                        FacebookSdk.setIsDebugEnabled(true)
                        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                    }

                    // Facebook Id
                    if (jsonObject.has("id")) {
                        val facebookId = jsonObject.getString("id")
                        Log.i("Facebook Id: ", facebookId.toString())
                    } else {
                        Log.i("Facebook Id: ", "Not exists")
                    }


                    // Facebook First Name
                    if (jsonObject.has("first_name")) {
                        val facebookFirstName = jsonObject.getString("first_name")
                        Log.i("Facebook First Name: ", facebookFirstName)
                    } else {
                        Log.i("Facebook First Name: ", "Not exists")
                    }


                    // Facebook Middle Name
                    if (jsonObject.has("middle_name")) {
                        val facebookMiddleName = jsonObject.getString("middle_name")
                        Log.i("Facebook Middle Name: ", facebookMiddleName)
                    } else {
                        Log.i("Facebook Middle Name: ", "Not exists")
                    }


                    // Facebook Last Name
                    if (jsonObject.has("last_name")) {
                        val facebookLastName = jsonObject.getString("last_name")
                        Log.i("Facebook Last Name: ", facebookLastName)
                    } else {
                        Log.i("Facebook Last Name: ", "Not exists")
                    }


                    // Facebook Name
                    if (jsonObject.has("name")) {
                        val facebookName = jsonObject.getString("name")
                        Log.i("Facebook Name: ", facebookName)
                    } else {
                        Log.i("Facebook Name: ", "Not exists")
                    }


                    // Facebook Profile Pic URL
                    if (jsonObject.has("picture")) {
                        val facebookPictureObject = jsonObject.getJSONObject("picture")
                        if (facebookPictureObject.has("data")) {
                            val facebookDataObject = facebookPictureObject.getJSONObject("data")
                            if (facebookDataObject.has("url")) {
                                val facebookProfilePicURL = facebookDataObject.getString("url")
                                Log.i("Facebook Profile Pic URL: ", facebookProfilePicURL)
                            }
                        }
                    } else {
                        Log.i("Facebook Profile Pic URL: ", "Not exists")
                    }

                    // Facebook Email
                    if (jsonObject.has("email")) {
                        val facebookEmail = jsonObject.getString("email")
                        Log.i("Facebook Email: ", facebookEmail)
                    } else {
                        Log.i("Facebook Email: ", "Not exists")
                    }
                }).executeAsync()
    }
    fun isLoggedIn() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired

//        return isLoggedIn
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken!!)
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }



    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(this, "Autentication failed!", Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
    }



}
