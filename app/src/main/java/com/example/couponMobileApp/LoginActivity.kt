package com.example.couponMobileApp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.couponMobileApp.activity.GoogleSignInActivity
import com.example.couponMobileApp.activity.HomeActivity
import com.example.couponMobileApp.activity.SetNewPasswordActivity
import com.example.couponMobileApp.activity.SignupActivity
import com.example.couponMobileApp.models.ChangePasswordResponse
import com.example.couponMobileApp.models.ForgotPsResponse
import com.example.couponMobileApp.models.LoginResponse
import com.example.couponMobileApp.utils.Utils
import com.example.couponMobileApp.utils.Utils.hideKeyboard
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_pin_authentication.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

var title = ""
var message = ""

class LoginActivity : AppCompatActivity() {
    var dtoken = ""

    companion object {
        private const val RC_SIGN_IN = 120
    }

    private lateinit var myAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setStatusBarColor(ContextCompat.getColor(applicationContext, R.color.dark_blue))

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (intent.extras != null) {
            for (key in intent.extras!!.keySet()) {
                if (key == "title") {
                    title = intent.extras!!.getString("title", "")
                }
                if (key == "message") {
                    message = intent.extras!!.getString("message", "")
                }
            }
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("696367015267-lmrdrhlme8ko5ak6uqppn7lv4lgv9fe6.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        myAuth = FirebaseAuth.getInstance()

        val passsword = findViewById<EditText>(R.id.txtPass)
        val email = findViewById<EditText>(R.id.txtEm)
        val btnGgle = findViewById<Button>(R.id.btnGoogle)

        btnGgle.setOnClickListener {
            signIn()
        }

        //validation

        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {

                } else {
                    email.setError("Invalid Email")
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })


        var a = 0
        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.text = "Log In"
//        val ibk = findViewById<ImageView>(R.id.imgBack)
//        ibk.isVisible = false
        val txtFr = findViewById<TextView>(R.id.txtForgot)


        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("deviceId", deviceId)

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultName")

        txtFr.setOnClickListener {
            val email = findViewById<EditText>(R.id.txtEm)
            val e = email.text.toString().trim()

            if (e.isNotEmpty()) {
                val map: MutableMap<String, RequestBody> = HashMap()
                val e = email.text.toString().trim()
                map["email"] = toPart(e) as RequestBody
                mAPIService.forgotPs(token!!, "Forgot_Password", map).enqueue(object :
                    Callback<ForgotPsResponse> {
                    override fun onResponse(
                        call: Call<ForgotPsResponse>,
                        response: retrofit2.Response<ForgotPsResponse>
                    ) {
                        try {
                            if (response.body()?.success == true) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_LONG
                                ).show()


                                val dialogBuilder: AlertDialog.Builder =
                                    AlertDialog.Builder(this@LoginActivity)

                                val inflater: LayoutInflater = this@LoginActivity.layoutInflater
                                val dialogView: View = inflater.inflate(
                                    R.layout.fragment_pin_authentication,
                                    null
                                )


                                dialogBuilder.setView(dialogView)
                                val e = email.text.toString().trim()
                                val alertDialog: AlertDialog = dialogBuilder.create()

                                alertDialog.show()

                                dialogView.pin_code_et.setOnPinEnteredListener {

                                    val otp = it.toString()
                                    Log.e("PINNNNN", otp)
                                    Log.e("PINNNNN", e)

                                    val map: MutableMap<String, RequestBody> = HashMap()
                                    map["email"] = toPart(e) as RequestBody
                                    map["otp"] = toPart(otp)
//
//                                        //start
                                    mAPIService.changePas(token!!, "OTP_Verify", map)
                                        .enqueue(object :
                                            Callback<ChangePasswordResponse> {
                                            override fun onResponse(
                                                call: Call<ChangePasswordResponse>,
                                                response: retrofit2.Response<ChangePasswordResponse>
                                            ) {
                                                alertDialog.dismiss()
                                                if (response.body()?.success == true) {
                                                    var i = (Intent(
                                                        this@LoginActivity,
                                                        SetNewPasswordActivity::class.java
                                                    ))
                                                    i.putExtra("em", e)
                                                    i.flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    startActivity(i)
                                                    finish()
                                                } else {
                                                    Toast.makeText(
                                                        this@LoginActivity,
                                                        "Wrong OTP! Try Again",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<ChangePasswordResponse>,
                                                t: Throwable
                                            ) {
                                                Toast.makeText(
                                                    this@LoginActivity,
                                                    t.message,
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        })
                                    //end
                                    //alertDialog.dismiss()
                                }
                                // alertDialog.dismiss()

                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    response.body()?.data,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            Log.d("error", response.toString())
                        }
                    }

                    override fun onFailure(call: Call<ForgotPsResponse>, t: Throwable) {
                        Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                email.error = "Enter Email.."
            }

        }
        var btnReg = findViewById<TextView>(R.id.txtSignup)
        btnReg.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        var btn_Lg = findViewById<Button>(R.id.btnLg)
        btn_Lg.setOnClickListener {

            //For hide keyboard on click
            hideKeyboard(this)

            val SHARED_PREF_NAME2 = "my_shared_preff"
            val sharedPreference1 = getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE)
            val dtoken = sharedPreference1.getString("device_token", "defaultToken")
            Log.d("test","detok"+dtoken!!)


            val e = email.text.toString().trim()
            val p = passsword.text.toString().trim()

            Log.d("test","Email : "+e)
            Log.d("test","Password : "+p)

            if (e.isEmpty()) {
                email.error = "Email is required"
                email.requestFocus()
                return@setOnClickListener
            }
            if (p.isEmpty()) {
                passsword.error = "Password is required"
                passsword.requestFocus()
                return@setOnClickListener
            }

            val map: MutableMap<String, RequestBody> = HashMap()
            map["email"] = toPart(e) as RequestBody
            map["password"] = toPart(p)
            map["device_token"] = toPart(dtoken.toString())


            // val em = sharedPreference.getString("email","defaultName")
            RetrofitClient.instance.loginUser(token!!, "login", map).enqueue(object :
                Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: retrofit2.Response<LoginResponse>
                ) {
                    try {
                        if (response.body()?.success == true) {
                            Toast.makeText(
                                this@LoginActivity,
                                response.body()?.message,
                                Toast.LENGTH_LONG
                            ).show()
                            SharedPrefManager.getInstance(applicationContext)
                                .saveUser(response.body()?.data!!)
                            val i = (Intent(this@LoginActivity, HomeActivity::class.java))
                            i.putExtra("Username", response.body()?.data!!.name)
//                             Log.d("token",response.body()?.data!!.token)
//                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Invalid User", Toast.LENGTH_LONG)
                                .show()
                        }
                    } catch (e: Exception) {
                        Log.d("error", response.toString())
                    }
                }


                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    //Google signin
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            var mAPIService: UserApi? = null
            mAPIService = ApiUtils.apiService

            if (task.isSuccessful) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("Tag", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                    val personName: String = account.displayName.toString()
                    val perEmail: String = account.email.toString()
                    Log.d("nmmmmm", personName)
                    val personId: String = account.getId().toString()
                    val personPhoto: Uri? = account.getPhotoUrl()

                    val map: MutableMap<String, RequestBody> = HashMap()
                    map["email"] = toPart(perEmail) as RequestBody
                    map["providerId"] = toPart(personId)


                    mAPIService.sociallogin("", "socialRegisterCheck", map).enqueue(object :
                        Callback<LoginResponse> {
                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: retrofit2.Response<LoginResponse>
                        ) {

                            Log.d("ressssgg", response.body()?.success.toString())
                            if (response.body()?.success == true) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                SharedPrefManager.getInstance(applicationContext)
                                    .saveUser(response.body()?.data!!)
                                val i = (Intent(this@LoginActivity, HomeActivity::class.java))
                                i.putExtra("Username", response.body()?.data!!.name)
                                // Log.d("token",response.body()?.data!!.token)
//                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(i)
                                finish()
                            } else if (response.body()?.data == null) {
                                var i = Intent(this@LoginActivity, GoogleSignInActivity::class.java)
                                i.putExtra("personName", personName)
                                i.putExtra("perEmail", perEmail)
                                i.putExtra("personId", personId)
                                i.putExtra("personPhoto", personPhoto.toString())
                                startActivity(i)
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Something went wrong!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_LONG).show()
                        }
                    })

                } catch (e: Exception) {
                    // Google Sign In failed, update UI appropriately
                    Log.w("Tag", "Google sign in failed", e)
                }
            } else {
                Toast.makeText(this, "Failed to Sign in", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        myAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Tag", "signInWithCredential:success")
                    // startActivity(Intent(this,GoogleSignInActivity::class.java))

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Tag", "signInWithCredential:failure", task.exception)

                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
