package com.example.couponMobileApp.fragment

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
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import coil.api.load
import com.example.couponMobileApp.ApiUtils
import com.example.couponMobileApp.R
import com.example.couponMobileApp.R.layout
import com.example.couponMobileApp.URIPathHelper
import com.example.couponMobileApp.UserApi
import com.example.couponMobileApp.activity.ChangePasswordActivity
import com.example.couponMobileApp.activity.HomeActivity
import com.example.couponMobileApp.models.UpdateResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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


class MyProfileFragment : Fragment() {

    var currentPath: String? = null
    val TAKE_PICTURE = 1
    val SELECT_PICTURE = 2
    var uri: Uri?=null
    var uimage:Uri?=null
    var u: Uri?=null
    var uri2: Uri?=null
    lateinit var file:File
    lateinit var requestFile:RequestBody
    private val REQUEST_PERMISSION = 0
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(layout.fragment_my_profile, container, false)
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (context as AppCompatActivity).supportActionBar!!.title = "My Profile"


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasWritePermission = requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val hasReadPermission = requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE),
                        REQUEST_PERMISSION)
            }
        }

        val phn = requireView().findViewById(R.id.getMb) as EditText
        val name = requireView().findViewById(R.id.getNm) as EditText
        val email = requireView().findViewById(R.id.getEm)as EditText
        val txtChange = requireView().findViewById(R.id.txtChangePs) as TextView
        val img_pro = requireView().findViewById(R.id.imgPro) as ImageView

        img_pro.setOnClickListener {

            var alertDialog: AlertDialog? =null
            val builder = AlertDialog.Builder(context)

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
        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreferences = context?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()

        val sharedPreference = this.activity?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token ="Bearer " + (sharedPreference?.getString("token", "defaultName"))
        val nm = sharedPreference?.getString("name", "defaultName")
        val em = sharedPreference?.getString("email", "defaultName")
        val ph = sharedPreference?.getString("phone", "defaultName")
        val pn = sharedPreference?.getString("pin", null)
        val pt =  sharedPreference?.getString("Image", "defaultName")

        name.setText(nm)
        email.setText(em)
        phn.setText(ph)
        Log.d("token", token!!)

        u = Uri.parse(pt)
        img_pro.load(u.toString())

        val editBtn = requireView().findViewById(R.id.btn_edit) as Button
        editBtn.setOnClickListener {


            if(uri == null) {
              uri2="content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F33/ORIGINAL/NONE/1858671968".toUri()
                Log.d("imageabcd", uri2.toString())
                file= File(URIPathHelper.getPath(requireContext(), uri2!!))

                Log.d("imageabcd", file.toString())

                requestFile = RequestBody.create(
                        requireContext().contentResolver.getType(uri2!!)!!.toMediaTypeOrNull(),
                        file
                )

            } else {
                file= File(URIPathHelper.getPath(requireContext(), uri!!))

                Log.d("imageabcd", file.toString())

                requestFile = RequestBody.create(
                        requireContext().contentResolver.getType(uri!!)!!.toMediaTypeOrNull(),
                        file
                )
                Log.d("Imageprofileelse","hi " + uri)
//                content://com.android.providers.media.documents/document/image%3A163122
            }
                Log.d("Imageprofileelse","hi " + uri.toString())
//                content://com.android.providers.media.documents/document/image%3A163122


            val n = name.text.toString().trim()
            val p = phn.text.toString().trim()
//            var imageurl = Uri.parse(uri.toString())
            name.setText(n)
            phn.setText(p)


            val map: MutableMap<String, RequestBody> = HashMap()
            map["name"] = toPart(n!!) as RequestBody
            //map["email"] = toPart(em!!)
            map["phone"] = toPart(p!!)
            Log.d("newurllll_2", "image:- " + uimage)
            Log.d("Imageprofileelsse","hi " + uri)


      val body = MultipartBody.Part.createFormData("user_img", file!!.name, requestFile)
            Log.d("contentresolver","" + requestFile)
            mAPIService.editProfile(token!!, body, "ChangeProfile", map).enqueue(object :
                    Callback<UpdateResponse> {
                override fun onResponse(
                        call: Call<UpdateResponse>,
                        response: Response<UpdateResponse>
                ) {

                        var nurl = response.body()?.data!!.Image
                        Log.d("newurl", nurl.toString())
                        editor?.putString("name", n)
                        editor?.putString("phone", p)
                        editor?.putString("Image", nurl.toString())
                        editor?.commit()

                        Log.d("sharedprefimg", sharedPreference?.getString("Image", "Def").toString())
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                        startActivity(Intent(context, HomeActivity::class.java))
                        getActivity()?.getFragmentManager()?.popBackStack()
                        Log.d("imageabc", file!!.name)

                }

                override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }

        txtChange.setOnClickListener {

            val intent = Intent(this@MyProfileFragment.context, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(context, HomeActivity::class.java))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)
    }

   fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    fun dispatchCamera() {
        val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(i.resolveActivity(requireContext().packageManager) != null)
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
                        requireContext(),
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
        val stoargeDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var image = File.createTempFile(imageName, ".jpg", stoargeDir)
        currentPath = image.absolutePath
        return image
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val stImg = requireView().findViewById<ImageView>(R.id.imgPro)
        if(requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK)
        {
            try {
                var file = File(currentPath)
                uri = Uri.fromFile(file)
                Log.d("Imaggggee", "image:- " + uri)
                stImg.imageTintMode = null
                stImg.setImageURI(uri)
            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }
        }

       else if(requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK)
        {
            try {
                uri = data?.data
                Log.d("Imaggggee", "image:- " + uri)
                stImg.imageTintMode = null
                stImg.setImageURI(uri)
            }
            catch (e: IOException)
            {
                Log.d("error", e.toString())
            }
        }
        else
        {
            val SHARED_PREF_NAME = "image_shared_preff"
            val sharedPreference = requireContext().getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val image = sharedPreference.getString("URI", "defaultName")
            uri = Uri.parse(image)
        }
    }


}