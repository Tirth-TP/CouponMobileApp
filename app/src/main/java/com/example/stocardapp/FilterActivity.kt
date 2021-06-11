package com.example.stocardapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.stocardapp.models.Filter
import com.example.stocardapp.models.FilterResponse
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.activity_filter.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FilterActivity : AppCompatActivity() {

    var lst = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.header_black)

        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Apply Filters")
        val imgback = findViewById<ImageView>(R.id.imgBack)
        imgback.setOnClickListener {
            var i = Intent(this,HomeActivity::class.java)
            i.putExtra("filter","all")
            finish()
        }

        var stList = ArrayList<Filter>()

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultToken")

        val SHARED_PREF_NAME1 = "my_shared_preff"
        val sharedPreferences: SharedPreferences = getSharedPreferences(SHARED_PREF_NAME1,Context.MODE_PRIVATE)
        var s = sharedPreferences.getString("filterid", "defaultName")

        val map: MutableMap<String, RequestBody> = HashMap()
        map[""] = toPart("") as RequestBody

        mAPIService!!.getCategory(token!!, "FetchAll", map).enqueue(object :
            Callback<FilterResponse> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(
                call: Call<FilterResponse>,
                response: Response<FilterResponse>
            ) {
                var dt = response.body()?.data
                if (dt != null) {
                    for (d in dt) {
                        var v = d
                        stList.add(v)
                    }
//                    val layoutManager = FlexboxLayoutManager(applicationContext)
//                    layoutManager.flexDirection = FlexDirection.COLUMN
//                    layoutManager.justifyContent = JustifyContent.FLEX_END
//                    filterRv.setLayoutManager(layoutManager)
                    var adapter = FilterAdapter(this@FilterActivity, stList)
                    filterRv.adapter = adapter
                }
            }

            override fun onFailure(call: Call<FilterResponse>, t: Throwable) {
                Toast.makeText(this@FilterActivity, t.message, Toast.LENGTH_LONG).show()
                Log.d("ttttt", t.message.toString())
            }
        })

        btnFil.setOnClickListener {
            Log.d("ffffiiidd",s!!)
            var i = Intent(this,HomeActivity::class.java)
            i.putExtra("filter","all")
            i.putExtra("isfilter","filtered")
            startActivity(i)
        }
    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }
}