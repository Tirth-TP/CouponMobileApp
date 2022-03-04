package com.example.couponMobileApp.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.couponMobileApp.LoginActivity
import com.example.couponMobileApp.R
import com.example.couponMobileApp.RetrofitClient
import com.example.couponMobileApp.URIPathHelper
import com.example.couponMobileApp.models.Response
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_maps.*
import kotlinx.android.synthetic.main.activity_signup.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class SignupActivity : AppCompatActivity()/* Gota.OnRequestPermissionsBack*/ {

    var currentPath: String? = null
    val TAKE_PICTURE = 1
    val SELECT_PICTURE = 2
    var uri: Uri?=null
    private val REQUEST_PERMISSION = 0
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)


        window.setStatusBarColor(ContextCompat.getColor(applicationContext, R.color.dark_blue))
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        val cpass = findViewById<EditText>(R.id.txtcPass)
        val phn = findViewById<EditText>(R.id.txtMb)
        val name = findViewById<TextInputEditText>(R.id.txtNm)
        val pin = findViewById<EditText>(R.id.txtsPin)
        val value:Int = phn.length()
        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Sign Up")
        val btnsin = findViewById<Button>(R.id.btn_reg)
        val txtLg = findViewById<TextView>(R.id.txtLogin)
        val st = findViewById<ImageView>(R.id.imgSPro)
        val txtsEm = findViewById<EditText>(R.id.txtsEm)
        val txtsPass = findViewById<EditText>(R.id.txtsPass)

        //validation
        typeListener(txtsEm)
        typeListener(txtsPass)



        cpass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (cpass.length() < 8 || txtsPass.length() > 16) {
                    cpass.setError("Invalid Password")
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if (!cpass.text.toString().equals(txtsPass.text.toString())) {
                    cpass.setError("Password does not match!")
                }
            }

        })

        phn.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (phn.length() < 10) {
                    phn.error = "Invalid Phone Number"
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        st.setOnClickListener {

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                Gota.Builder(this)
//                    .withPermissions(
//                        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    )
//                    .requestId(1)
//                    .setListener(this)
//                    .check()
//            }else{
//                Gota.Builder(this)
//                    .withPermissions(
//                        Manifest.permission.READ_EXTERNAL_STORAGE,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE
//                    )
//                    .requestId(1)
//                    .setListener(this)
//                    .check()
//            }
            var alertDialog: AlertDialog? = null
            val builder = AlertDialog.Builder(this@SignupActivity)


            val inflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.imagepick, null)
            builder.setView(dialogView)

            var cm = dialogView.findViewById<ImageView>(R.id.btn_cam)
            var ga = dialogView.findViewById<ImageView>(R.id.btn_gal)
            var cl = dialogView.findViewById<Button>(R.id.btn_cancel)

            cm.setOnClickListener {
                cl.performClick()
                dispatchCamera()
            }

            ga.setOnClickListener {
                cl.performClick()
                dispatchGallery()
            }

            cl.setOnClickListener {
                if (alertDialog != null) {
                    alertDialog!!.dismiss()
                }
            }
            alertDialog = builder.create()
            alertDialog.window?.setGravity(Gravity.BOTTOM)
            alertDialog.show()

        }

        btnsin.setOnClickListener {
         //   Toast.makeText(this,"Correct Password",Toast.LENGTH_LONG).show()
          val unm = name.text.toString().trim()
         val uem = txtsEm.text.toString().trim()
          val ups = txtsPass.text.toString().trim()
            val cps = cpass.text.toString().trim()
          val uph = phn.text.toString().trim()
           val upin = pin.text.toString().trim()

            if(unm.isEmpty())
            {
                name.error = "Name is required"
                name.requestFocus()
                return@setOnClickListener
            }
            if(uem.isEmpty())
            {
                txtsEm.error = "Email is required"
                txtsEm.requestFocus()
                return@setOnClickListener
            }
            if(ups.isEmpty())
            {
                txtsPass.error = "Password is required"
                txtsPass.requestFocus()
                return@setOnClickListener
            }
            else
            {
                val l = uph.length
                if(l <=8 || l >= 16)
                {
                    phn.error = "Invalid Phone Number"
                    phn.requestFocus()
                    return@setOnClickListener
                }
            }
            if(cps.isEmpty())
            {
                cpass.error = "Password is required"
                cpass.requestFocus()
                return@setOnClickListener
            }
            else{

                if(!cps.equals(ups))
                {
                    cpass.error = "Password does not match"
                    cpass.requestFocus()
                    return@setOnClickListener
                }
            }
            if(uph.isEmpty())
            {
                phn.error = "Phone number is required"
                phn.requestFocus()
                return@setOnClickListener
            }
            else
            {
                val l = uph.length
                if(l < 10 || l >= 15)
                {
                    phn.error = "Invalid Phone Number"
                    phn.requestFocus()
                    return@setOnClickListener
                }
            }
            if(upin.isEmpty())
            {
                pin.error = "pin is required"
                pin.requestFocus()
                return@setOnClickListener
            }
            else
            {
                val l = upin.length
                if(l != 4)
                {
                    pin.error = "Invalid pin"
                    pin.requestFocus()
                    return@setOnClickListener
                }
            }


            val SHARED_PREF_NAME = "my_shared_preff"
            val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val token = "Bearer " + sharedPreference.getString("token", "defaultName")
            val file: File = File(URIPathHelper.getPath(this@SignupActivity, uri!!))
            val deviceId = Settings.Secure.getString(
                this.contentResolver,
                Settings.Secure.ANDROID_ID
            )

            val SHARED_PREF_NAME2 = "my_shared_preff"
            val sharedPreference1 = getSharedPreferences(SHARED_PREF_NAME2, Context.MODE_PRIVATE)
            val dtoken = sharedPreference1.getString("device_token", "defaultToken")
            Log.d("detok", dtoken!!)

            Log.d("newurlllll", uri!!.toString())
            val requestFile = RequestBody.create(
                contentResolver.getType(uri!!)!!.toMediaTypeOrNull(),
                file
            )
            val body = MultipartBody.Part.createFormData("user_img", file.name, requestFile)
            val map: MutableMap<String, RequestBody> = HashMap()
            map["name"] = toPart(unm) as RequestBody
            map["email"] = toPart(uem)
            map["password"] = toPart(ups)
            map["phone"] = toPart(uph)
            map["pin"] = toPart(upin)
            map["device_token"] = toPart(dtoken)

          RetrofitClient.instance.createUser("", body, "register", map).enqueue(object :
              Callback<Response> {
              override fun onResponse(
                  call: Call<Response>,
                  response: retrofit2.Response<Response>
              ) {
                  Log.d("siiignn", response.body()?.success.toString())
                  if (response.body()?.success == true) {
                      Toast.makeText(
                          this@SignupActivity,
                          response.body()?.message,
                          Toast.LENGTH_LONG
                      ).show()
                      val i = (Intent(applicationContext, LoginActivity::class.java))
                      i.putExtra("Username", response.body()?.data!!.name)
                      i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                      startActivity(i)
                      finish()
                  } else {
                      Toast.makeText(
                          this@SignupActivity,
                          "Something went wrong!",
                          Toast.LENGTH_LONG
                      ).show()
                  }
              }

              override fun onFailure(call: Call<Response>, t: Throwable) {
                  Toast.makeText(this@SignupActivity, t.message, Toast.LENGTH_LONG).show()
              }
          })
        }
        txtLg.setOnClickListener {
         //   Toast.makeText(this@SignupActivity, "success", Toast.LENGTH_LONG).show()
            val i = (Intent(applicationContext, LoginActivity::class.java))
            startActivity(i)
            finish()
        }
    }

    private fun typeListener(editext: EditText) {
        editext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editext == txtsEm) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(editext.text.toString())
                            .matches()
                    ) {
                        txtInpEmail.error=""
                    } else {
                        txtInpEmail.error="Invalid email";
//                        editext.setError("Invalid Email")
                    }
                } else if (editText == txtsPass) {
                    if (editext.length() < 8 || editext.length() > 16) {
//                        editext.setError("Invalid Password")
                        txtInpEmail.error="Invalid Email";

                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }
    fun dispatchCamera() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(i.resolveActivity(packageManager) != null)
        {
            var photoFile: File? = null
            try {
                photoFile = createImage()
            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }
            if(photoFile != null)
            {
                var photoUrl = FileProvider.getUriForFile(
                    this,
                    "com.example.couponMobileApp.fileprovider",
                    photoFile
                )
                i.putExtra(MediaStore.EXTRA_OUTPUT, photoUrl)
                startActivityForResult(i, TAKE_PICTURE)
            }
        }
    }

    fun dispatchGallery()
    {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_PICTURE)
    }
    fun createImage(): File
    {
        val timeStamp = SimpleDateFormat("yyyyMMddd_HHmmss").format(Date())
        val imageName = "JPEG_"+timeStamp+"_"
        val stoargeDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image = File.createTempFile(imageName, ".jpg", stoargeDir)
        currentPath = image.absolutePath
        return image
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val stImg = findViewById<ImageView>(R.id.imgSPro)
        if(requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK)
        {
            try {
                var file = File(currentPath)
                uri = Uri.fromFile(file)
                stImg.imageTintMode = null
                stImg.setImageURI(uri)

            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }
        }

        if(requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK)
        {
            try {
                uri = data?.data
                stImg.imageTintMode = null
                stImg.setImageURI(uri)
            }
            catch (e: IOException)
            {
                Log.d("error", e.toString())
            }
        }
        val SHARED_PREF_NAME = "image_shared_preff"
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(
            SHARED_PREF_NAME,
            Context.MODE_PRIVATE
        )
        val editor: SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString("URI", uri.toString())
        editor.apply()
        editor.commit()
    }

//    override fun onRequestBack(requestId: Int, gotaResponse: GotaResponse) {
//        if(requestId==1 && gotaResponse.isAllGranted) {
//            var alertDialog: AlertDialog? = null
//            val builder = AlertDialog.Builder(this@SignupActivity)
//
//
//            val inflater = this.layoutInflater
//            val dialogView: View = inflater.inflate(R.layout.imagepick, null)
//            builder.setView(dialogView)
//
//            var cm = dialogView.findViewById<ImageView>(R.id.btn_cam)
//            var ga = dialogView.findViewById<ImageView>(R.id.btn_gal)
//            var cl = dialogView.findViewById<Button>(R.id.btn_cancel)
//
//            cm.setOnClickListener {
//                cl.performClick()
//                dispatchCamera()
//            }
//
//            ga.setOnClickListener {
//                cl.performClick()
//                dispatchGallery()
//            }
//
//            cl.setOnClickListener {
//                if (alertDialog != null) {
//                    alertDialog!!.dismiss()
//                }
//            }
//            alertDialog = builder.create()
//            alertDialog.window?.setGravity(Gravity.BOTTOM)
//            alertDialog.show()
//            TODO("Not yet implemented")
//        }
//    }
}