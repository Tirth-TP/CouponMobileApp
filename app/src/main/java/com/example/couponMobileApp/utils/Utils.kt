package com.example.couponMobileApp.utils

import android.widget.EditText

object Utils {

    fun gettext(edittext:EditText): String {
        return edittext.text.toString().trim()?:""
    }
}