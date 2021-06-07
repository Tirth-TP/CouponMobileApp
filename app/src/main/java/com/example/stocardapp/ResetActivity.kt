package com.example.stocardapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.stocardapp.models.ChangePasswordResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class ResetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        val np = findViewById(R.id.npi1) as EditText
        val cp = findViewById(R.id.cpi1) as EditText
        val yesBtn = findViewById(R.id.okBtn1) as Button
        val noBtn = findViewById(R.id.cnBtn1) as Button

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference =  getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultName")

        yesBtn.setOnClickListener {
            var n =np.text.toString()
            var c = cp.text.toString()

            if (n.equals(c)) {
                val map: MutableMap<String, RequestBody> = HashMap()
                map["new_pin"] = toPart(np.text.toString()) as RequestBody
                mAPIService.changePas(token!!, "Create_New_Pin", map).enqueue(object :
                    Callback<ChangePasswordResponse> {
                    override fun onResponse(
                        call: Call<ChangePasswordResponse>,
                        response: retrofit2.Response<ChangePasswordResponse>
                    ) {
                        Log.d("hhhh","jj")
                        Toast.makeText(this@ResetActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@ResetActivity,HomeActivity::class.java))
                    }
                    override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                        Toast.makeText(this@ResetActivity, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
            else{
                cp.setError("Password does not match!")
            }
        }
    }
    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }
//        private fun movetoFragment(fragment: Fragment){
//        val fragmrntTrans=supportFragmentManager.beginTransaction()
//        fragmrntTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//        fragmrntTrans.replace(R.id.fragmentContainer,fragment,"back")
//        fragmrntTrans.addToBackStack("back")
//        fragmrntTrans.commit()
//    }
}
