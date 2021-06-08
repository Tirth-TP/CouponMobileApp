package com.example.stocardapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stocardapp.models.ChangePasswordResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [contactUs.newInstance] factory method to
 * create an instance of this fragment.
 */
class contactUs : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        (context as AppCompatActivity).supportActionBar!!.title = "Contact Us"

//        val txtTit = requireView().findViewById<TextView>(R.id.txtTitle)
//        txtTit.setText("Contact Us")
//        val ibk = requireView().findViewById(R.id.imgBack) as ImageView
//        ibk.setOnClickListener {
//            startActivity(Intent(context,HomeActivity::class.java))
//        }
        val btnCn = requireView().findViewById<Button>(R.id.btn_cnt)
        val tnm = requireView().findViewById<EditText>(R.id.txtCNm)
        val tem = requireView().findViewById<EditText>(R.id.txtCem)
        val tsb = requireView().findViewById<EditText>(R.id.txtSub)
        val tmg = requireView().findViewById<EditText>(R.id.txtMsg)

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        btnCn.setOnClickListener {
            val nm = tnm.text.toString()
            val em = tem.text.toString()
            val sb = tsb.text.toString()
            val mg = tmg.text.toString()

            val SHARED_PREF_NAME = "my_shared_preff"
            val sharedPreference =
                    this.activity?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            val token = "Bearer " + (sharedPreference?.getString("token", "defaultName"))
            val map: MutableMap<String, RequestBody> = HashMap()
            map["name"] = toPart(nm) as RequestBody
            map["email"] = toPart(em)
            map["subject"] = toPart(sb)
            map["description"] = toPart(mg)

            mAPIService.changePas(token!!, "ContactUs", map).enqueue(object :
                    Callback<ChangePasswordResponse> {
                override fun onResponse(
                        call: Call<ChangePasswordResponse>,
                        response: retrofit2.Response<ChangePasswordResponse>
                ) {
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                    val intent = Intent(this@contactUs.context, HomeActivity::class.java)
                    startActivity(intent)
                }
                override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }
            })
        }
    }
    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment contactUs.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                contactUs().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}