package com.example.stocardapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import com.example.stocardapp.models.ChangePasswordResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class SetNewPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_new_password)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.header_black)

        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Set New  Password")

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val nps = findViewById(R.id.chNewP) as EditText
        val cps = findViewById(R.id.chConP) as EditText
        val btn_sv = findViewById(R.id.btn_yes1) as Button
        val btnCl = findViewById(R.id.btn_cl1) as Button

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultName")
       // val email = sharedPreference.getString("email", "defaultName")
        val email = intent.getStringExtra("em")

        btnCl.setOnClickListener {
            var i = (Intent(this@SetNewPasswordActivity,LoginActivity::class.java))
            i.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }

        Log.d("eemaill",email!!)

        btn_sv.setOnClickListener {
            val np = nps.text.toString().trim()
            val cp = cps.text.toString().trim()

            if(np.equals(cp)) {
                Log.d("hhhhhhh", np)
                val map: MutableMap<String, RequestBody> = HashMap()
                map["email"] = toPart(email!!) as RequestBody
                map["new_password"] = toPart(np)

                mAPIService.changePas(token!!, "Create_New_Password", map).enqueue(object :
                    Callback<ChangePasswordResponse> {
                    override fun onResponse(
                        call: Call<ChangePasswordResponse>,
                        response: retrofit2.Response<ChangePasswordResponse>
                    ) {
                        Toast.makeText(this@SetNewPasswordActivity, response.body()?.message, Toast.LENGTH_LONG).show()

                        var i = (Intent(this@SetNewPasswordActivity,LoginActivity::class.java))
                        i.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(i)
                    }
                    override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                        Toast.makeText(this@SetNewPasswordActivity, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
            else
            {
                cps.setError("Password does not match!")
            }

        }
    }
    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }
}