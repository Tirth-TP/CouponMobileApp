package com.example.stocardapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.stocardapp.models.CardResponse
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_add_card2.*
import kotlinx.android.synthetic.main.imagepickqr.*
import kotlinx.android.synthetic.main.imagepickqr.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class AddCardActivity : AppCompatActivity() {

    var cal = Calendar.getInstance()
    var currentPath: String? = null
    val TAKE_PICTURE = 1
    val SELECT_PICTURE = 2
    var uri: Uri? = null
    private val REQUEST_PERMISSION = 0
    var datePicker: DatePickerDialog? = null
    val calendar = Calendar.getInstance()

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card2)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        val day = calendar[Calendar.DAY_OF_MONTH]
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]

        datePicker = DatePickerDialog(this);
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
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_PHONE_STATE
                    ),
                    REQUEST_PERMISSION
                )
            }
        }

        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Add Coupon")


        val cname = findViewById<EditText>(R.id.cardNm)
        val crwd = findViewById<EditText>(R.id.cardRwd)
        val cdtl = findViewById<EditText>(R.id.cardDtl)
        val cnum = findViewById<EditText>(R.id.cardNum)
        val addCard = findViewById<Button>(R.id.btn_card)
        val cdt = findViewById<TextInputEditText>(R.id.cardDate)
        val txtinpt = findViewById<LinearLayout>(R.id.lindat)
        val icrd = findViewById<ImageView>(R.id.imgCr)

        crwd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (crwd.text.toString().toInt() > 100) {
                    crwd.setError("Percentage should not be grater than 100")
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })




        cdt.setOnClickListener {
            datePicker = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val ans = "" + dayOfMonth + "/" + month + "/" + year
                    cdt.setText(ans)

                },
                year,
                month,
                day
            )

            datePicker!!.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker!!.show();
        }

        val bmp: Bitmap
        val byteArray = intent.getByteArrayExtra("imagebitmap")
        if (byteArray == null) {
            icrd.setImageDrawable(resources.getDrawable(R.drawable.coupan))
        } else {
            Log.d("bytearray", "" + byteArray)
            bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
            icrd.setImageBitmap(bmp)
        }
        icrd.setOnClickListener {
            var alertDialog: AlertDialog? =null
            val builder = AlertDialog.Builder(this@AddCardActivity)
            var sid = intent.getIntExtra("storeId", 0)
            val inflater = this.layoutInflater
            val dialogView: View = inflater.inflate(R.layout.imagepickqr, null)
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
            dialogView.btn_scan.setOnClickListener{
                Log.d("STORE_IDCARD","" + sid)
                val i = (Intent(applicationContext, QrScannerActivity::class.java))
                i.putExtra("s_id", sid)
                startActivity(i)
                finish()
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


        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultName")

        addCard.setOnClickListener {
            var sid = intent.getIntExtra("storeId", 0)
            Log.d("STORE_ID",""+sid)
            val bmp: Bitmap
            val byteArray = intent.getByteArrayExtra("imagebitmap")
            if (byteArray == null) {
                icrd.setImageDrawable(resources.getDrawable(R.drawable.addst))
            } else {
                Log.d("bytearray", "" + byteArray)
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
//                icrd.setImageBitmap(bmp)
                val bytes = ByteArrayOutputStream()
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path: String =
                    Images.Media.insertImage(this.getContentResolver(), bmp, "Title", null)
                uri = Uri.parse(path)
//                icrd.setImageURI(uri)
            }

            Log.d("loggeduri", "" + uri)
            //var sid = intent.getIntExtra("storeId", 0)

            val cn = cname.text.toString().trim()
            val cnm = cnum.text.toString().trim()
            val cd = cdtl.text.toString().trim()
            val crd = crwd.text.toString().trim()

            val map: MutableMap<String, RequestBody> = HashMap()
            map["cardname"] = toPart(cn) as RequestBody
            map["carddetail"] = toPart(cd)
            map["rewardpercen"] = toPart(crd)
            map["cardno"] = toPart(cnm)
            map["expdate"] = toPart(cdt.text.toString())
            map["st_id"] = toPart(sid.toString())

            val file: File = File(URIPathHelper.getPath(this@AddCardActivity, uri!!))

            val requestFile = RequestBody.create(
                contentResolver.getType(uri!!)!!.toMediaTypeOrNull(),
                file
            )
            Log.d("loggedurifile", "" + file.name)
            val body = MultipartBody.Part.createFormData("card_img", file.name, requestFile)
            //  map["user_id"] = token?.let { it1 -> toPart(it1) }!!
            Log.d("token", token!!)
            mAPIService.addCard(token!!, "StoreCard", body, map).enqueue(object :
                Callback<CardResponse> {
                override fun onResponse(
                    call: Call<CardResponse>,
                    response: retrofit2.Response<CardResponse>
                ) {
                    if (response.body()?.success == true) {
                        Toast.makeText(
                            applicationContext,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        val i = (Intent(applicationContext, CardListActivity::class.java))
                        // i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        //   i.putExtra("s_id",sid)
                        startActivity(i)
                    } else {
                        Toast.makeText(
                            this@AddCardActivity,
                            "Something Went Wrong!" + response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

                override fun onFailure(call: Call<CardResponse>, t: Throwable) {
                    Toast.makeText(this@AddCardActivity, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }

        cdt.setFocusable(false);
        cdt!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                var mdate: DatePickerDialog = DatePickerDialog(
                    this@AddCardActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                mdate.datePicker.minDate = System.currentTimeMillis() - 1000
                mdate.show()
            }
        })
    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    private fun updateDateInView() {
        var cdt = findViewById<EditText>(R.id.cardDate)
        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        cdt.setText(sdf.format(cal.getTime()))
    }

    fun dispatchCamera() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (i.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoFile != null) {
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

    fun dispatchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_PICTURE)
    }

    fun createImage(): File {
        val timeStamp = SimpleDateFormat("yyyyMMddd_HHmmss").format(Date())
        val imageName = "JPEG_" + timeStamp + "_"
        val stoargeDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image = File.createTempFile(imageName, ".jpg", stoargeDir)
        currentPath = image.absolutePath
        return image
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val icrd = findViewById<ImageView>(R.id.imgCr)
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            try {
                var file = File(currentPath)
                uri = Uri.fromFile(file)
                icrd.imageTintMode = null
                icrd.setImageURI(uri)
                Log.d("uri", uri.toString())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            try {
                uri = data?.data
                icrd.imageTintMode = null
                icrd.setImageURI(uri)

                Log.d("uri", uri.toString())
            } catch (e: IOException) {
                Log.d("error", e.toString())
            }
        }
    }
}