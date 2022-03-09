package com.example.couponMobileApp.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import coil.api.load
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.couponMobileApp.*
import com.example.couponMobileApp.models.Response
import kotlinx.android.synthetic.main.activity_google_sign_in.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import java.io.*
import kotlin.collections.HashMap


class GoogleSignInActivity : AppCompatActivity() {

    var dtoken: String = ""
    var pid: String = ""

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_sign_in)

        window.setStatusBarColor(ContextCompat.getColor(applicationContext, R.color.dark_blue))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Sign up")

        val pnm = intent.getStringExtra("personName")
        val pem = intent.getStringExtra("perEmail")
        pid = intent.getStringExtra("personId") ?: ""
        val pimg = intent.getStringExtra("personPhoto")

        Log.d("uhhjh", pimg.toString())

        txtgNm.setText(pnm)
        txtgEm.setText(pem)
        imggPro.load(pimg)
        imggPro.imageTintMode = null

        val SHARED_PREF_NAME2 = "my_shared_preff"
        val sharedPreference1 = getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE)
        dtoken = sharedPreference1.getString("device_token", "defaultToken") ?: ""
        Log.d("detok", dtoken!!)

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService


        btnGreg.setOnClickListener {

            val img = intent.getStringExtra("personPhoto")

            val rootdir =
                File(Environment.getExternalStorageDirectory().toString() + "/" + "stocard/")
            if (!rootdir.exists()) {
                rootdir.mkdirs()
            }
            var profileImagePath =
                rootdir.absolutePath + File.separator.toString() + "" + System.currentTimeMillis() + ".jpg"

            Glide.with(this)
                .asBitmap()
                .load(img)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                            bit: Bitmap,
                            transition: Transition<in Bitmap>?
                    ) {
                        val out = FileOutputStream(profileImagePath)
                        bit.compress(Bitmap.CompressFormat.JPEG, 90, out)
                        out.flush()
                        out.close()
                        if (profileImagePath.length > 0) {
                            this@GoogleSignInActivity.runOnUiThread {
                                signupApi(profileImagePath);
                            }
                        }

                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
        }
    }

    private fun signupApi(profileImagePath: String) {

        val file = File(profileImagePath)
        val requestFile = file.asRequestBody(
                contentResolver.getType(Uri.parse(profileImagePath))?.toMediaTypeOrNull()
        )
        Log.d("urllllla", file.name)
        val body = MultipartBody.Part.createFormData("user_img", file.name, requestFile)
        val map: MutableMap<String, RequestBody> = HashMap()
        map["name"] = toPart(txtgNm.text.toString().trim()) as RequestBody
        map["email"] = toPart(txtgEm.text.toString().trim())
        map["phone"] = toPart(txtgMb.text.toString().trim())
        map["pin"] = toPart(txtsgPin.text.toString().trim())
        map["device_token"] = toPart(dtoken ?: "")
        map["providerID"] = toPart(pid.toString())

        RetrofitClient.instance.createUser("", body, "SocialRegister", map).enqueue(object :
                retrofit2.Callback<Response> {
            override fun onResponse(
                    call: Call<Response>,
                    response: retrofit2.Response<Response>
            ) {
                Log.d("siiignn", response.body()?.success.toString())
                if (response.body()?.success == true) {
                    Toast.makeText(
                            this@GoogleSignInActivity,
                            response.body()?.message,
                            Toast.LENGTH_LONG
                    ).show()
                    SharedPrefManager.getInstance(applicationContext)
                        .saveUser(response.body()?.data!!)
                    val i = (Intent(applicationContext, HomeActivity::class.java))
                    i.putExtra("Username", response.body()?.data!!.name)
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(
                            this@GoogleSignInActivity,
                            "Something went wrong!",
                            Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Toast.makeText(this@GoogleSignInActivity, t.message, Toast.LENGTH_LONG).show()
                Log.d("errormessage", t.message!!)
            }
        })

    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    fun getBitmapFromURL(url: String): Uri? {
//        val mydir = File(Environment.getExternalStorageDirectory().toString() + "/stocard")
        val rootdir = File(Environment.getExternalStorageDirectory().toString() + "/" + "stocard/")
        var profileImagePath =
            rootdir.absolutePath + File.separator.toString() + "" + System.currentTimeMillis() + ".jpg"

//        val mydir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath)
        Log.d("mydir", "" + rootdir)
        if (!rootdir.exists()) {
            rootdir.mkdirs()
        }


        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })


        /*  val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
          val downloadUri = Uri.parse(url[0])
          val request = DownloadManager.Request(downloadUri)

          val dateFormat = SimpleDateFormat("mmddyyyyhhmmss")
          val date: String = dateFormat.format(Date())

          request.setAllowedNetworkTypes(
              DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE
          )
              .setAllowedOverRoaming(false)
              .setTitle("Downloading")
  //            .setDestinationInExternalPublicDir("stocard", "$date.jpg")
              .setDestinationInExternalPublicDir("/stocard", "$date.jpg")
  //            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "$date.jpg")

          manager.enqueue(request)
          Log.d("Final_LLLLL", "" + mydir.absolutePath + File.separator.toString() + date + ".jpg")
          var p = mydir.absolutePath + File.separator.toString() + date + ".jpg"
  //        val cw = ContextWrapper(applicationContext)
  //        val directory = cw.getDir("/stocard", MODE_PRIVATE)
  //        val mypath = File(directory, "imagename.jpg")
          var file = File(p)
          iuri = Uri.fromFile(file)
          return iuri*/
        return null
    }
}
