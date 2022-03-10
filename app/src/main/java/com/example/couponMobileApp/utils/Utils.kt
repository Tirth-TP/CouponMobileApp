package com.example.couponMobileApp.utils

import android.app.Activity
import android.content.Context
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity

object Utils {

    fun gettext(edittext: EditText): String {
        return edittext.text.toString().trim() ?: ""
    }

    //for hide keyboard for fragment
    fun hideKeyboard(activity: FragmentActivity) {
        // Check if no view has focus:
        val view = activity.currentFocus
        if (view != null) {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
    //for hide keyboard for activity
    fun hideKeyboard(activity: AppCompatActivity) {
        // Check if no view has focus:
        val view = activity.currentFocus
        if (view != null) {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

}