package com.example.stocardapp.utils

import android.content.Intent
import android.widget.EditText

object Utils {

    fun gettext(edittext:EditText): String {
        return edittext.text.toString().trim()?:""
    }
}