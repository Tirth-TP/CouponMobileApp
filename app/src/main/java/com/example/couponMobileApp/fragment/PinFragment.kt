package com.example.couponMobileApp.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.couponMobileApp.ApiUtils
import com.example.couponMobileApp.R
import com.example.couponMobileApp.UserApi
import com.example.couponMobileApp.activity.HomeActivity
import com.example.couponMobileApp.activity.ResetActivity
import com.example.couponMobileApp.models.ChangePasswordResponse
import com.example.couponMobileApp.models.ForgotPsResponse
import kotlinx.android.synthetic.main.fragment_pin_authentication.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class PinFragment : Fragment() {

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


        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService

        val top = requireView().findViewById(R.id.opin) as EditText
        val tnp = requireView().findViewById(R.id.npin) as EditText
        val tcp = requireView().findViewById(R.id.cpin) as EditText
        val txtChange = requireView().findViewById(R.id.txtForPin) as TextView
        val btn = requireView().findViewById(R.id.btnFrg) as Button

        val SHARED_PREF_NAME = "my_shared_preff"

        val sharedPreference =
            this.activity?.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreference?.getString("token", "defaultName"))
        val map: MutableMap<String, RequestBody> = HashMap()


        btn.setOnClickListener {

            var op = top.text.toString()
            var np = tnp.text.toString()
            var cp = tcp.text.toString()

            if (np.equals(cp)) {

                map["old_pin"] = toPart(op) as RequestBody
                map["new_pin"] = toPart(np)

                mAPIService.changePas(token!!, "Change_Pin", map).enqueue(object :
                    Callback<ChangePasswordResponse> {
                    override fun onResponse(
                        call: Call<ChangePasswordResponse>,
                        response: retrofit2.Response<ChangePasswordResponse>
                    ) {
                        Log.d("Pin_RESPONSE",response.body()?.success.toString())
                        if (response.body()?.success == true) {
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG).show()
                            startActivity(Intent(requireActivity(), HomeActivity::class.java))
                        }
                        else{
                            Toast.makeText(context,"Something went wrong!",Toast.LENGTH_LONG).show()
                        }
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

                    val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(context)

                    val inflater: LayoutInflater = this@PinFragment.layoutInflater
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
                        mAPIService.changePas(token!!, "OTP_Verify_Pin", map).enqueue(object :
                            Callback<ChangePasswordResponse> {
                            override fun onResponse(
                                call: Call<ChangePasswordResponse>,
                                response: retrofit2.Response<ChangePasswordResponse>
                            ) {
                                alertDialog.dismiss()
                                if (response.body()?.success == true) {
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

}