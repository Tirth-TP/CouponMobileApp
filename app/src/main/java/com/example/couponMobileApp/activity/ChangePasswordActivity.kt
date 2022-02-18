package com.example.couponMobileApp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatDelegate
import com.example.couponMobileApp.ApiUtils
import com.example.couponMobileApp.fragment.MyProfileFragment
import com.example.couponMobileApp.R
import com.example.couponMobileApp.UserApi
import com.example.couponMobileApp.models.ChangePasswordResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.header_black)

        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Change Password")
        var imgBack = findViewById<ImageView>(R.id.imgBack)
        imgBack.setOnClickListener {
            var i = Intent(this, MyProfileFragment::class.java)
            i.putExtra("filter", "all")
            finish()
        }

        val ops = findViewById(R.id.chold) as EditText
        val nps = findViewById(R.id.chNew) as EditText
        val cps = findViewById(R.id.chCon) as EditText
        val btn_sv = findViewById(R.id.btn_yes) as Button
        //val btnCl = findViewById(R.id.btn_cl) as Button

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultName")

//        btnCl.setOnClickListener {
//            var i = (Intent(this@ChangePasswordActivity,HomeActivity::class.java))
//            i.flags= Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            startActivity(i)
//        }

        btn_sv.setOnClickListener {
            val op = ops.text.toString().trim()
            val np = nps.text.toString().trim()
            val cp = cps.text.toString().trim()

            if (np.equals(cp)) {
                Log.d("hhhhhhh", np)

                val map: MutableMap<String, RequestBody> = HashMap()
                map["old_password"] = toPart(op) as RequestBody
                map["new_password"] = toPart(np)
//            map["confirm_password"] = toPart(cp)
                Log.d("token", token!!)

                mAPIService.changePas(token!!, "ChangePassword", map).enqueue(object :
                        Callback<ChangePasswordResponse> {
                    override fun onResponse(
                            call: Call<ChangePasswordResponse>,
                            response: retrofit2.Response<ChangePasswordResponse>
                    ) {
                        if (response.body()?.success == true) {
                            Toast.makeText(this@ChangePasswordActivity, response.body()?.message, Toast.LENGTH_LONG).show()

                            var i = (Intent(this@ChangePasswordActivity, HomeActivity::class.java))
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                        } else {
                            Toast.makeText(this@ChangePasswordActivity, "Something went wrong!", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                        Toast.makeText(this@ChangePasswordActivity, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                cps.setError("Password does not match!")
            }
        }
    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }
}