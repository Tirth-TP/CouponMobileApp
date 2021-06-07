package com.example.stocardapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import coil.api.load
import com.example.stocardapp.R.layout
import com.example.stocardapp.models.UpdateResponse
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType
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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfile.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyProfile : Fragment() {
    // TODO: Rename and change types of parameters
    var currentPath: String? = null
    val TAKE_PICTURE = 1
    val SELECT_PICTURE = 2
    var uri: Uri?=null
    var u: Uri?=null
    private val REQUEST_PERMISSION = 0
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private lateinit var customAlertDialogView : View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(layout.fragment_my_profile, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar!!.title = "My Profile"

//        val txtTit = requireView().findViewById(R.id.txtTitle) as TextView
//        txtTit.setText("My profile")

//        val ibk = requireView().findViewById(R.id.imgBack) as ImageView
//        ibk.setOnClickListener {
//            startActivity(Intent(context, HomeActivity::class.java))
//        }


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

            val n = name.text.toString().trim()
            val p = phn.text.toString().trim()

            var imageurl = Uri.parse(uri.toString())

            name.setText(n)
            phn.setText(p)


            val map: MutableMap<String, RequestBody> = HashMap()
            map["name"] = toPart(n!!) as RequestBody
            //map["email"] = toPart(em!!)
            map["phone"] = toPart(p!!)


            val file: File = File(URIPathHelper.getPath(requireContext(), uri!!))
            Log.d("imageabcd", file.toString())

            val requestFile = RequestBody.create(
                requireContext().contentResolver.getType(uri!!)!!.toMediaTypeOrNull(),
                    file
            )
            val body = MultipartBody.Part.createFormData("user_img", file.name, requestFile)
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
                   // editor?.putString("pin", i)
                    editor?.putString("Image", nurl.toString())
                    editor?.commit()

                    Log.d("sharedprefimg", sharedPreference?.getString("Image", "Def").toString())
                    //img_pro.load(u.toString())
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
//                    val fr = fragmentManager!!.beginTransaction()
//                    fr.replace(R.id.container, HomeScreen())
                    startActivity(Intent(context,HomeActivity::class.java))
                    getActivity()?.getFragmentManager()?.popBackStack()
//                    getActivity()?.getSupportFragmentManager()?.beginTransaction()?.remove(this@MyProfile)?.commit()
                    Log.d("imageabc", file.name)
                }

                override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }

        txtChange.setOnClickListener {
            //Toast.makeText(context, "Pro", Toast.LENGTH_LONG).show()
           /* materialAlertDialogBuilder = MaterialAlertDialogBuilder(context?.applicationContext!!)
            customAlertDialogView = LayoutInflater.from(context)
                    .inflate(R.layout.change_ps, null, false)
            launchCustomAlertDialog()*/
            val intent = Intent(this@MyProfile.context, ChangePasswordActivity::class.java)
            startActivity(intent)

        }
    }
  /*  private fun launchCustomAlertDialog() {
        val ops = requireView().findViewById(R.id.chold) as EditText
        val nps = requireView().findViewById(R.id.chNew) as EditText
        val cps = requireView().findViewById(R.id.chCon) as EditText
        materialAlertDialogBuilder.setView(customAlertDialogView)
                .setTitle("Change Password")
               // .setMessage("Enter your basic details")
                .setPositiveButton("Save"){ dialog, _ ->
                    val op = ops.text.toString()
                    val np = nps.text.toString()
                    val cp = cps.text.toString()
                    Log.d("data",op+np+cp)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
    }*/
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

//    override fun onResume() {
//        super.onResume()
//        val img_pro = requireView().findViewById(R.id.imgPro) as ImageView
//        img_pro.load(u.toString())
//    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val stImg = requireView().findViewById<ImageView>(R.id.imgPro)
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyProfile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                MyProfile().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}