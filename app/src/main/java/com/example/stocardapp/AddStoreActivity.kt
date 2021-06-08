package com.example.stocardapp

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.stocardapp.models.FilterResponse
import com.example.stocardapp.models.StoreResponse
import com.example.stocardapp.models.StoreSuggestionResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class AddStoreActivity : AppCompatActivity() {

    var currentPath: String? = null
    val TAKE_PICTURE = 1
    val SELECT_PICTURE = 2
    var uri:Uri?=null
    private val REQUEST_PERMISSION = 0

    var mAPIService: UserApi? = null
    val map: MutableMap<String, RequestBody> = HashMap()

    val temp: ArrayList<String?> = ArrayList()
    val c: ArrayList<String?> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_store)

        supportActionBar?.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.header_black)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasWritePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val hasReadPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            val permissions: MutableList<String> = ArrayList()
            if (hasWritePermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
//              preferencesUtility.setString("storage", "true");
            }
            if (hasReadPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
//              preferencesUtility.setString("storage", "true");
            }
            if (!permissions.isEmpty()) {
//              requestPermissions(permissions.toArray(new String[permissions.size()]), REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE),
                        REQUEST_PERMISSION)
            }
        }

        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Add Store")

        mAPIService = ApiUtils.apiService

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultName")

        //Store category
        initUI()

        val snm = findViewById<AutoCompleteTextView>(R.id.storeNm)
        val sloc = findViewById<EditText>(R.id.storeLoc)
        val spn = findViewById<EditText>(R.id.storePn)
        val addBtn = findViewById<Button>(R.id.btn_store)
        val stImg = findViewById<ImageView>(R.id.storeImg)
        val ibk = findViewById<ImageView>(R.id.imgBack)
        val stCt = findViewById<AutoCompleteTextView>(R.id.spCategory)

        spn.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(spn.length()>11)
                {
                    spn.setError("Invalid Phone Number")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        ibk.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        stImg.setOnClickListener {
            var alertDialog: AlertDialog? =null
            val builder = AlertDialog.Builder(this@AddStoreActivity)
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
                if(alertDialog!=null) {
                    alertDialog!!.dismiss()
                }
            }
            alertDialog = builder.create()
            alertDialog.window?.setGravity(Gravity.BOTTOM)
            alertDialog.show()
        }

        map[""] = toPart("") as RequestBody
        val listdata = ArrayList<String>()
        val imglist = ArrayList<String>()

        mAPIService!!.storeSuggestion(token!!, "StoreSuggest", map).enqueue(object :
                Callback<StoreSuggestionResponse> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(
                    call: Call<StoreSuggestionResponse>,
                    response: Response<StoreSuggestionResponse>
            ) {
                //  val data = JSONArray()
                //   val jsonObject = JSONObject()
                // val list: List<JSONObject> = data.stream().map { o -> o as JSONObject }.collect(Collectors.toList())
                val jArray = response.body()?.data!!
                for (i in jArray) {
                    listdata.add(i.stname)
                }
                var adapter = ArrayAdapter(this@AddStoreActivity, android.R.layout.simple_list_item_1, listdata)
                snm.threshold = 0
                snm.setAdapter(adapter)

                for (i in jArray) {
//                    while(snm.text.toString()==i.stname)
//                    {
                    imglist.add(i.store_img)
                    //}
                }
            }

            override fun onFailure(call: Call<StoreSuggestionResponse>, t: Throwable) {
                Toast.makeText(this@AddStoreActivity, t.message, Toast.LENGTH_LONG).show()
                Log.d("ttttt", t.message.toString())
            }
        })

        addBtn.setOnClickListener {

            val sct = stCt.text.toString().trim()
            var fi = ""


            for (j in c.indices)
            {
//                Log.d("fffiiff", c[j]!!)
            if(c[j] == sct)
            {
//                Log.d("fffiiff", temp[j]!!)
                fi = temp[j]!!
                break
            }
            }
//            Log.d("fffiiff",fi)

            val sn = snm.text.toString().trim()
                val sl = sloc.text.toString().trim()
                val sp = spn.text.toString().trim()

                val map: MutableMap<String, RequestBody> = HashMap()
                map["stname"] = toPart(sn) as RequestBody
                map["stlocation"] = toPart(sl)
                map["stcontact"] = toPart(sp)
                map["category_id"] = toPart(fi)

                val file: File = File(URIPathHelper.getPath(this@AddStoreActivity, uri!!))
//                val requestFile: RequestBody = RequestBody.create(
//                    MediaType.parse("image/jpeg"),
//                    file
//                )
                val requestFile = RequestBody.create(
                        contentResolver.getType(uri!!)!!.toMediaTypeOrNull(),
                        file
                )
                val body = MultipartBody.Part.createFormData("store_img", file.name, requestFile)
                   // Log.d("body", createImage().absolutePath)
                mAPIService!!.addStore(token!!, "AddStore", body, map).enqueue(object :
                        Callback<StoreResponse> {
                    override fun onResponse(
                            call: Call<StoreResponse>,
                            response: retrofit2.Response<StoreResponse>
                    ) {
                        if (response.body()?.success == true) {
                            Toast.makeText(applicationContext, response.body()?.message, Toast.LENGTH_LONG).show()
                            val i = (Intent(applicationContext, HomeActivity::class.java))
                            startActivity(i)
                        } else {
                            Toast.makeText(applicationContext, "failure", Toast.LENGTH_LONG).show()
                        }
                        // i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        val un=  i.getStringExtra("Username")
//                        Toast.makeText(applicationContext,un,Toast.LENGTH_LONG).show()
//                        var itemsLayout: NavigationView = findViewById(R.id.nav)
//                        val view = layoutInflater.inflate(R.layout.nav_header, null)
//                        val tvItem: TextView = view.findViewById(R.id.uname)
//                        tvItem.setText(un)// Here you can access all views inside your child layout and assign them values
//                        itemsLayout.addView(view)
                    }

                    override fun onFailure(call: Call<StoreResponse>, t: Throwable) {
                        Toast.makeText(this@AddStoreActivity, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
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
                     "com.example.stocardapp.fileprovider",
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
    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val stImg = findViewById<ImageView>(R.id.storeImg)
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
    }

    private fun initUI() {
        //UI reference of textView
        val customerAutoTV = findViewById<AutoCompleteTextView>(R.id.spCategory)

        // create list of customer
        val customerList = getCustomerList()

        //Create adapter
        val adapter: ArrayAdapter<String?> = ArrayAdapter<String?>(this@AddStoreActivity, android.R.layout.simple_spinner_item, customerList)

        //Set adapter
        customerAutoTV.setAdapter(adapter)

    }

    private fun getCustomerList(): ArrayList<String?> {
        val customers: ArrayList<String?> = ArrayList()

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultName")

        map[""] = toPart("") as RequestBody
        mAPIService!!.getCategory(token!!, "FetchAll", map).enqueue(object :
            Callback<FilterResponse> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(
                call: Call<FilterResponse>,
                response: Response<FilterResponse>
            ) {
                val jArray = response.body()?.data!!
                for (i in jArray) {
                    customers.add(i.name)
                    temp.add(i.id.toString())
                }
                c.addAll(customers)
            }

            override fun onFailure(call: Call<FilterResponse>, t: Throwable) {
                Toast.makeText(this@AddStoreActivity, t.message, Toast.LENGTH_LONG).show()
                Log.d("ttttt", t.message.toString())
            }
        })
        return customers
    }
}
