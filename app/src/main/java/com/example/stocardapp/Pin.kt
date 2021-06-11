package com.example.stocardapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.stocardapp.models.ChangePasswordResponse
import com.example.stocardapp.models.ForgotPsResponse
import kotlinx.android.synthetic.main.fragment_pin_authentication.view.*
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
 * Use the [Pin.newInstance] factory method to
 * create an instance of this fragment.
 */
class Pin : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pin, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (context as AppCompatActivity).supportActionBar!!.title = "Change Pin"

//        val txtTit = requireView().findViewById(R.id.txtTitle) as TextView
//        txtTit.setText("Change PIN")

        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val top = requireView().findViewById(R.id.opin) as EditText
        val tnp = requireView().findViewById(R.id.npin) as EditText
        val tcp = requireView().findViewById(R.id.cpin) as EditText
        val txtChange = requireView().findViewById(R.id.txtForPin) as TextView
        val btn = requireView().findViewById(R.id.btnFrg) as Button

        val SHARED_PREF_NAME = "my_shared_preff"
        //val sharedPreferences = context?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val sharedPreference =
            this.activity?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreference?.getString("token", "defaultName"))
        val map: MutableMap<String, RequestBody> = HashMap()


        btn.setOnClickListener {

            var op = top.text.toString()
            var np = tnp.text.toString()
            var cp = tcp.text.toString()

            if (np.equals(cp)) {
                //Log.d("pinnnnn","yesss")
                map["old_pin"] = toPart(op) as RequestBody
                map["new_pin"] = toPart(np)

                mAPIService.changePas(token!!, "Change_Pin", map).enqueue(object :
                    Callback<ChangePasswordResponse> {
                    override fun onResponse(
                        call: Call<ChangePasswordResponse>,
                        response: retrofit2.Response<ChangePasswordResponse>
                    ) {
                        Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                        Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                //Log.d("pinnnnn","upss")
                tcp.setError("PIN does not match!")
            }
        }

        txtChange.setOnClickListener {
            map[""] = toPart("") as RequestBody

            mAPIService.forgotPs(token!!, "Forgot_Pin", map).enqueue(object :
                Callback<ForgotPsResponse> {
                override fun onResponse(
                    call: Call<ForgotPsResponse>,
                    response: retrofit2.Response<ForgotPsResponse>
                ) {
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                    Log.d("HELLLLLLLLLLLLLLLLLL", response.body()?.message.toString())

//                    var dialog = Dialog(requireContext())
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                    dialog.setCancelable(false)
//                    dialog.setContentView(R.layout.otpdlg)
//                    val otp = dialog.findViewById(R.id.votp) as EditText
//                    val yesBtn = dialog.findViewById(R.id.cotp) as Button
                    val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)

                    val inflater: LayoutInflater = this@Pin.layoutInflater
                    val dialogView: View = inflater.inflate(
                        R.layout.fragment_pin_authentication,
                        null
                    )


                    dialogBuilder.setView(dialogView)

                    val alertDialog: AlertDialog = dialogBuilder.create()

                    alertDialog.show()

                    dialogView.pin_code_et.setOnPinEnteredListener {
                        val otp = it.toString()
                        map["pin_otp"] = toPart(otp) as RequestBody
                        //start
                        mAPIService.changePas(token!!, "OTP_Verify", map).enqueue(object :
                            Callback<ChangePasswordResponse> {
                            override fun onResponse(
                                call: Call<ChangePasswordResponse>,
                                response: retrofit2.Response<ChangePasswordResponse>
                            ) {
                                alertDialog.dismiss()
                                if (response.body()?.status == true) {
                                    var i = (Intent(context, ResetActivity::class.java))

                                    i.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(i)
                                }
                                else{
                                    Toast.makeText(context,"Wrong OTP Try Again",Toast.LENGTH_SHORT).show()
                                }

                            }

                            override fun onFailure(
                                call: Call<ChangePasswordResponse>,
                                t: Throwable
                            ) {
                                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                            }
                        })
                        //end
                        // dialog.dismiss()
                    }
                    // dialog.show()
                }

                override fun onFailure(call: Call<ForgotPsResponse>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }
            })
            //  Log.d("htytyt",flag.toString())

//            if(flag==true)
//            {
//                Toast.makeText(context,"htytyt",Toast.LENGTH_LONG).show()
//                Log.d("htytyt","hhheeeee")
//                var dialog1 = Dialog(requireContext())
//                dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
//                dialog1.setCancelable(false)
//                dialog1.setContentView(R.layout.resetpin)
//                val np = dialog1.findViewById(R.id.npi) as EditText
//                val cp = dialog1.findViewById(R.id.cpi) as EditText
//                val yesBtn = dialog1.findViewById(R.id.okBtn) as Button
//                val noBtn = dialog1.findViewById(R.id.cnBtn) as Button
//
//                yesBtn.setOnClickListener {
//                    if(np.equals(cp))
//                    {
//                        map["new_pin"] = toPart(np.text.toString()) as RequestBody
//
//                        mAPIService.changePas(token!!, "Create_New_Password", map).enqueue(object :
//                                Callback<ChangePasswordResponse> {
//                            override fun onResponse(
//                                    call: Call<ChangePasswordResponse>,
//                                    response: retrofit2.Response<ChangePasswordResponse>
//                            ) {
//                                Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
//                                dialog1.dismiss()
//                            }
//                            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
//                                Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
//                            }
//                        })
//                        dialog1.dismiss()
//                    }
//                }
//                dialog1.show()
//            }

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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Pin.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Pin().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}