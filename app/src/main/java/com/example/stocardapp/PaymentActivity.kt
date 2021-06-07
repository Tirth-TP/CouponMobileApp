package com.example.stocardapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.stocardapp.models.CardDetail
import com.example.stocardapp.models.ChangePasswordResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class PaymentActivity : AppCompatActivity() {

    var GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user"
    var GOOGLE_PAY_REQUEST_CODE = 123
    var mAPIService: UserApi? = null
    var token: String? = null
    val UPI_PAYMENT = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val txtTit = findViewById<TextView>(R.id.txtTitle)
        txtTit.setText("Make Payment")
        val ibk = findViewById<ImageView>(R.id.imgBack)
        ibk.setOnClickListener {
            startActivity(Intent(this,CardListActivity::class.java))
        }
        val unm = findViewById<EditText>(R.id.txtPname)
        val upi = findViewById<EditText>(R.id.txtUpi)
        val amt = findViewById<EditText>(R.id.txtAmt)
        val note = findViewById<EditText>(R.id.txtNote)
        val paymnt = findViewById<Button>(R.id.btn_prcd)

        mAPIService = ApiUtils.apiService

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        token = "Bearer " + sharedPreference.getString("token", "defaultName")

//        var am = amount.toDouble()
        paymnt.setOnClickListener {
            var nm = unm.text.toString().trim()
            var upi_id = upi.text.toString().trim()
            var amount = amt.text.toString().trim()
            var nt = note.text.toString().trim()

            val uri: Uri = Uri.parse("upi://pay").buildUpon()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", upi_id) // virtual ID
                .appendQueryParameter("pn", nm) // nam
         //   .appendQueryParameter("mc", "") // optional
//                .appendQueryParameter("tid", "20190603022401")
            .appendQueryParameter("tr", "25584584") // optional
                .appendQueryParameter("tn", nt) // any note about payment
                .appendQueryParameter("am", amount) // amount
                .appendQueryParameter("cu", "INR") // currency
//            .appendQueryParameter("url", "your-transaction-url") // optional
                .build()

            val upiPayIntent = Intent(Intent.ACTION_VIEW)
            upiPayIntent.data = uri
           // upiPayIntent.setPackage(GOOGLE_PAY_PACKAGE_NAME)
      //  startActivityForResult(upiPayIntent, GOOGLE_PAY_REQUEST_CODE)
            val chooser = Intent.createChooser(upiPayIntent, "Pay with")
            // check if intent resolves
            // check if intent resolves
            if (null != chooser.resolveActivity(packageManager)) {
                startActivityForResult(chooser, UPI_PAYMENT)

            } else {
                Toast.makeText(
                    this@PaymentActivity,
                    "No UPI app found, please install one to continue",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("main ", "response $resultCode")
        when (requestCode) {
            UPI_PAYMENT -> if (Activity.RESULT_OK == resultCode || resultCode == 11) {
                if (data != null) {
                    val trxt = data.getStringExtra("response")
                    Log.d("UPI", "onActivityResult: $trxt")
                    val dataList = ArrayList<String>()
                    if (trxt != null) {
                        dataList.add(trxt)
                    }
                    upiPaymentDataOperation(dataList)
                } else {
                    Log.d("UPI", "onActivityResult: " + "Return data is null")
                    val dataList = ArrayList<String>()
                    dataList.add("nothing")
                    upiPaymentDataOperation(dataList)
                }
            } else {
                Log.d("UPI", "onActivityResult: " + "Return data is null") //when user simply back without payment
                val dataList = ArrayList<String>()
                dataList.add("nothing")
                upiPaymentDataOperation(dataList)
            }
        }
    }

    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

    private fun upiPaymentDataOperation(data: ArrayList<String>) {
        if (isConnectionAvailable(this@PaymentActivity)) {
            var str: String? = data[0]
            Log.d("UPIPAY", "upiPaymentDataOperation: " + str!!)
            var paymentCancel = ""
            if (str == null) str = "discard"
            var status = ""
            var approvalRefNo = ""
            val response = str.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in response.indices) {
                val equalStr = response[i].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (equalStr.size >= 2) {
                    if (equalStr[0].toLowerCase() == "Status".toLowerCase()) {
                        status = equalStr[1].toLowerCase()
                    } else if (equalStr[0].toLowerCase() == "ApprovalRefNo".toLowerCase() || equalStr[0].toLowerCase() == "txnRef".toLowerCase()) {
                        approvalRefNo = equalStr[1]
                    }
                } else {
                    paymentCancel = "Payment cancelled by user."
                }
            }

            if (status == "success") {
                //Code to handle successful transaction here.
                Toast.makeText(this@PaymentActivity, "Transaction successful.", Toast.LENGTH_SHORT).show()
                Log.d("UPI", "responseStr: $approvalRefNo")
                var cid = intent.getIntExtra("cardId", 0)
                val map: MutableMap<String, RequestBody> = HashMap()
                map["card_id"] = toPart(cid.toString()) as RequestBody
                mAPIService!!.changePas(token!!, "Card_Disable", map).enqueue(object :
                        Callback<ChangePasswordResponse> {
                    override fun onResponse(
                            call: Call<ChangePasswordResponse>,
                            response: retrofit2.Response<ChangePasswordResponse>
                    ) {
                        Toast.makeText(this@PaymentActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@PaymentActivity,HomeActivity::class.java))
                    }
                    override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                        Toast.makeText(this@PaymentActivity, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            } else if ("Payment cancelled by user." == paymentCancel) {
                Toast.makeText(this@PaymentActivity, "Payment cancelled by user.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@PaymentActivity, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@PaymentActivity, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun isConnectionAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val netInfo = connectivityManager.activeNetworkInfo
                if (netInfo != null && netInfo.isConnected
                        && netInfo.isConnectedOrConnecting
                        && netInfo.isAvailable) {
                    return true
                }
            }
            return false
        }
    }
}