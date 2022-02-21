package com.example.couponMobileApp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RateUs.newInstance] factory method to
 * create an instance of this fragment.
 */
class RateUs : Fragment() {
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
        return inflater.inflate(R.layout.fragment_rate_us, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (context as AppCompatActivity).supportActionBar!!.title = "Rate Us"
//        val txtTit = requireView().findViewById<TextView>(R.id.txtTitle)
//        txtTit.setText("Give Rating")
//        val ibk = requireView().findViewById(R.id.imgBack) as ImageView
//        ibk.setOnClickListener {
//            startActivity(Intent(context,HomeActivity::class.java))
//        }
        val btnRt = requireView().findViewById<Button>(R.id.btn_rate)
        btnRt.setOnClickListener {
            var playstoreuri1: Uri = Uri.parse("market://details?id=com.example.stocardapp")
            Toast.makeText(context, "Thank You For Rating", Toast.LENGTH_LONG).show()
//            val intent = Intent(this@RateUs.context, HomeActivity::class.java)
//            startActivity(intent)
            var playstoreIntent1: Intent = Intent(Intent.ACTION_VIEW, playstoreuri1)
            startActivity(playstoreIntent1)
            //it genrate exception when devices do not have playstore
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RateUs.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RateUs().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}