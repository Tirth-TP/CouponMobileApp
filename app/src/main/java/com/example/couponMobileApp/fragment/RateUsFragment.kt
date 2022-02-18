package com.example.couponMobileApp.fragment

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
import com.example.couponMobileApp.R


class RateUsFragment : Fragment() {


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

        val btnRt = requireView().findViewById<Button>(R.id.btn_rate)
        btnRt.setOnClickListener {
            var playstoreuri1: Uri = Uri.parse("market://details?id=com.example.stocardapp")
            Toast.makeText(context, "Thank You For Rating", Toast.LENGTH_LONG).show()

            var playstoreIntent1: Intent = Intent(Intent.ACTION_VIEW, playstoreuri1)
            startActivity(playstoreIntent1)
            //it genrate exception when devices do not have playstore
        }
    }

}