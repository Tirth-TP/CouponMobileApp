package com.example.stocardapp

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.stocardapp.models.CardDetail
import com.example.stocardapp.models.Store
import com.example.stocardapp.models.User

class SharedPrefManager private constructor(private val mCtx:Context){

    val isLoggedIn: Boolean
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString("email",null)!=null
        }

    val user: User
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
            return User(
                    sharedPreferences.getInt("id", 0),
                    sharedPreferences.getString("token", null).toString(),
                    sharedPreferences.getString("name", null).toString(),
                    sharedPreferences.getString("email", null).toString(),
                    sharedPreferences.getString("pin", null).toString(),
                    sharedPreferences.getString("phone", null).toString(),
                    sharedPreferences.getString("Image", null).toString(),
                            sharedPreferences.getString("device_id", null).toString()

            )
        }

    fun saveUser(user: User) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        Log.d("TOOOKen",user.token)
        editor.putString("token", user.token)
        editor.putString("name", user.name)
        editor.putString("email", user.email)
        editor.putString("pin", user.pin)
        editor.putString("phone", user.phone)
        editor.putString("Image",user.Image)
        editor.putString("device_id",user.device_token)
        editor.apply()
    }

    fun saveStore(sid: Int, stname: String, stlocation: String, stcontact: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("sid",sid)
        editor.putString("stname",stname)
        editor.putString("stlocation",stlocation)
<<<<<<< HEAD
        editor.putString("stcontact",stcontact)
=======
        editor.putString("contact",stcontact)
>>>>>>> 5479617693d742621dd7a811e6aaedadc550cac6
        editor.apply()
    }

    fun clear() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("token")
        editor.remove("email")
        editor.remove("pin")
        editor.remove("phone")
        editor.remove("Image")
        editor.apply()
    }

    companion object {
        private val SHARED_PREF_NAME = "my_shared_preff"
        private var mInstance: SharedPrefManager? = null
        @Synchronized
        fun getInstance(mCtx: Context): SharedPrefManager {
            if (mInstance == null) {
                mInstance = SharedPrefManager(mCtx)
            }
            return mInstance as SharedPrefManager
        }
    }
}