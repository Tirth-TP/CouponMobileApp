package com.example.stocardapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.alimuzaffar.lib.pin.PinEntryEditText
import com.example.stocardapp.R.*
import com.example.stocardapp.models.ChangePasswordResponse
import com.example.stocardapp.models.ForgotPsResponse
import com.example.stocardapp.models.LoginResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_pin_authentication.*
import kotlinx.android.synthetic.main.fragment_pin_authentication.view.*
import kotlinx.android.synthetic.main.header_black.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

var title = ""
var message = ""

class LoginActivity : AppCompatActivity() {
    var dtoken = ""

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        window.setStatusBarColor(ContextCompat.getColor(applicationContext,R.color.dark_blue))

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

        val tkn = FirebaseInstanceId.getInstance().token
        Log.d("tkkkk", tkn.toString())

        val passsword = findViewById<EditText>(R.id.txtPass)
        val email = findViewById<EditText>(R.id.txtEm)

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

//        passsword.addTextChangedListener(object:TextWatcher{
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if(passsword.length()<8 || passsword.length()>16)
//                {
//                    passsword.setError("Invalid Password")
//                }
//            }
//
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//
//        })


        var a = 0
        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Log In")
//        val ibk = findViewById<ImageView>(R.id.imgBack)
//        ibk.isVisible = false
        val txtFr = findViewById<TextView>(R.id.txtForgot)
        /* var pass = findViewById<EditText>(R.id.txtPass)
         pass.setOnTouchListener(View.OnTouchListener { v, event ->
             val DRAWABLE_RIGHT = 2
             if (event.action == MotionEvent.ACTION_UP) {
                 if (a == 0 && event.rawX >= pass.getRight() - pass.getCompoundDrawables()
                         .get(DRAWABLE_RIGHT).getBounds().width()
                 ) {
                     pass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                     a = 1
                     pass.setCompoundDrawablesWithIntrinsicBounds(
                         ContextCompat.getDrawable(
                             this,
                             drawable.ic_baseline_lock_24
                         ),
                         null,
                         ContextCompat.getDrawable(this, drawable.ic_baseline_hide_source_24),
                         null
                     )
                     return@OnTouchListener true
                 }
                 if (a == 1) {
                     pass.transformationMethod = PasswordTransformationMethod.getInstance()
                     a = 0
                       pass.setCompoundDrawablesWithIntrinsicBounds(
                         ContextCompat.getDrawable(
                             this,
                             drawable.ic_baseline_lock_24
                         ),
                         null,
                         ContextCompat.getDrawable(this, drawable.ic_baseline_remove_red_eye_24),
                         null
                     )
                     return@OnTouchListener true
                 }
             }
             false
         })*/

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val deviceId = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("deviceid", deviceId)

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultName")

        txtFr.setOnClickListener {
//            val builder = AlertDialog.Builder(this@LoginActivity)
//            val view = layoutInflater.inflate(R.layout.forgot_ps, null)
//            var em = view.findViewById<EditText>(R.id.txtFem)
//            em.setHint("Email")
//            var alertDialog: AlertDialog? =null
            val email = findViewById<EditText>(R.id.txtEm)
            val e = email.text.toString().trim()

//            with(builder)
//            {
//                val title = SpannableString("Enter Your Email")
//                title.setSpan(
//                        ForegroundColorSpan(Color.parseColor("#342ea9")),
//                        0,
//                        title.length,
//                        0
//                )
//                setTitle(title)
//                setTheme(R.style.Theme_StocardApp)
//                setPositiveButton("Send"){ dialog, which ->
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
                                                if(response.body()?.status==true) {
                                                    var i = (Intent(
                                                        this@LoginActivity,
                                                        SetNewPasswordActivity::class.java
                                                    ))
                                                    i.putExtra("em", e)
                                                    i.flags =
                                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    startActivity(i)
                                                    finish()
                                                }
                                                else
                                                {
                                                    Toast.makeText(this@LoginActivity,"Wrong OTP! Try Again",Toast.LENGTH_SHORT).show()
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
                email.setError("Enter Email..")
            }
//                }
//                setNegativeButton("Cancel"){ dialog, which->
//                        dialog.dismiss()
//                }
//                setView(view)
//                show()
//
//            }
        }
        var btnReg = findViewById(R.id.txtSignup) as TextView
        btnReg.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        var btn_Lg = findViewById(R.id.btnLg) as Button
        btn_Lg.setOnClickListener {

            val SHARED_PREF_NAME2 = "my_shared_preff"
            val sharedPreference1 = getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE)
            val dtoken = sharedPreference1.getString("device_token", "defaultToken")
            Log.d("detok", dtoken!!)

            val e = email.text.toString().trim()
            val p = passsword.text.toString().trim()

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
            map["device_id"] = toPart(dtoken.toString())
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
                            // Log.d("token",response.body()?.data!!.token)
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

    override fun onStart() {
        super.onStart()
        if (SharedPrefManager.getInstance(this).isLoggedIn) {
            val i = (Intent(this, HomeActivity::class.java))
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
