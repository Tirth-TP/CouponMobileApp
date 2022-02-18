package com.example.couponMobileApp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.couponMobileApp.R
import com.example.couponMobileApp.activity.HomeActivity


class AboutFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (context as AppCompatActivity).supportActionBar!!.title = "About Us"
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                startActivity(Intent(context, HomeActivity::class.java))
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

    }
}