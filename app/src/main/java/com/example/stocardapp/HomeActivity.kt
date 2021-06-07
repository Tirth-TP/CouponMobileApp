package com.example.stocardapp

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import coil.api.load
import com.example.stocardapp.models.ForgotPsResponse
import com.example.stocardapp.models.ShareCardResponse
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import kotlin.system.exitProcess

class HomeActivity : AppCompatActivity() {
    var img:ImageView?=null
    var tvItem:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

   //    val un=  intent.getStringExtra("Username")

        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val un =  sharedPreference.getString("name", "defaultName")
        val pt =  sharedPreference.getString("Image", "defaultName")

        Log.d("path",pt.toString())

        var itemsLayout: NavigationView = findViewById(R.id.nav)
        val view = layoutInflater.inflate(R.layout.nav_header, null)
        tvItem = view.findViewById(R.id.uname)
        img = view.findViewById(R.id.nav_profilePic)

        var u: Uri = Uri.parse(pt)
        Log.d("path1",u.toString())
        img!!.load(u.toString())
        tvItem!!.setText(un)// Here you can access all views inside your child layout and assign them values
        itemsLayout.addView(view)

        intent.putExtra("Username",un)

        val tb = findViewById<androidx.appcompat.widget.Toolbar>(R.id.topAppBar)
        val dr = findViewById<DrawerLayout>(R.id.drawer)
        setSupportActionBar(tb)
        //creating hamburger icon
        var toggler = ActionBarDrawerToggle(this, dr, tb, 0, 0)
        dr.addDrawerListener(toggler)

        toggler.syncState()
        movetoFragment(HomeScreen())
        val navigation = findViewById<NavigationView>(R.id.nav)
        navigation.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    movetoFragment(
                       HomeScreen()
                    )
                }
                R.id.nav_about -> {
                    movetoFragment(
                            About   ()
                    )
                }
                R.id.nav_profile->
                {
                    movetoFragment(
                        MyProfile()
                    )
                }
                R.id.nav_fav->
                {
                    movetoFragment(Favorites())
                }
                R.id.nav_contact->
                {
                    movetoFragment(
                         contactUs()
                    )
                }

                R.id.nav_rate->
                {
                    movetoFragment(
                        RateUs()
                    )
                }
                R.id.nav_card->
                {
                    getCard()
                }

                R.id.nav_pin->
                {
                    movetoFragment(Pin())
                }

                R.id.nav_invite->
                {
                    inviteUser()
                }
//                R.id.nav_find->
//                {
//                   findStore()
//                }

                R.id.nav_logout->
                {
                    SharedPrefManager.getInstance(applicationContext).clear()
                    startActivity(Intent(this,LoginActivity::class.java))
                    true
                }
            }
            dr.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun findStore() {
        val uri = "http://maps.google.com/maps?daddr=" + 12f + "," + 2f + " (" + "Search Destination.." + ")"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            try {
                val unrestrictedIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                startActivity(unrestrictedIntent)
            } catch (innerEx: ActivityNotFoundException) {
                Toast.makeText(this, "Please install a map application", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun inviteUser() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.example.stocardapp")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Invite Via")
        startActivity(shareIntent)
    }

    private fun getCard() {
        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference =  getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val token = "Bearer " + sharedPreference.getString("token", "defaultName")
        var mAPIService: UserApi? = null
        mAPIService = ApiUtils.apiService
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.get_card, null)
        var cd = view.findViewById<TextInputEditText>(R.id.getCd)
        cd.setHint("Enter Code")
        var alertDialog: AlertDialog? =null

        with(builder)
        {
            val title = SpannableString("Get Card")
            title.setSpan(
                    ForegroundColorSpan(Color.parseColor("#342ea9")),
                    0,
                    title.length,
                    0
            )
            setTitle(title)
            setTheme(R.style.Theme_StocardApp)
            setPositiveButton("Add"){ dialog, which ->
                val map: MutableMap<String, RequestBody> = HashMap()
                map["share_code"] = toPart(cd.text.toString()) as RequestBody

            Log.d("tpp",token)
                mAPIService.shareCard(token!!, "AddShareCard", map).enqueue(object :
                        Callback<ShareCardResponse> {
                    override fun onResponse(
                            call: Call<ShareCardResponse>,
                            response: retrofit2.Response<ShareCardResponse>
                    ) {
                        Log.d("resshh1",response.toString())
                       // Log.d("resshh1",response.body()!!.message)
                                Toast.makeText(this@HomeActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                    }
                    override fun onFailure(call: Call<ShareCardResponse>, t: Throwable) {
                        Toast.makeText(this@HomeActivity, t.message, Toast.LENGTH_LONG).show()
                    }
                })
            }
            setNegativeButton("Cancel"){ dialog, which->
                dialog.dismiss()
            }
            setView(view)
            show()
        }
    }
    private fun movetoFragment(fragment: Fragment){
        val fragmrntTrans=supportFragmentManager.beginTransaction()
        fragmrntTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        fragmrntTrans.replace(R.id.fragmentContainer,fragment,"back")
        fragmrntTrans.addToBackStack("back")
        fragmrntTrans.commit()
    }
    fun toPart(data: String): RequestBody {
        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
    }

override fun onBackPressed() {
    val dr = findViewById<DrawerLayout>(R.id.drawer)
    if(dr.isDrawerOpen(GravityCompat.START)) {
        dr.closeDrawer(GravityCompat.START)
    }
    else{
        super.onBackPressed()
    }
    exitProcess(1)
}
    override fun onResume() {
        super.onResume()
        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreference = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        img!!.load(sharedPreference.getString("Image", "Def"))
        tvItem!!.setText(sharedPreference.getString("name", ""))// Here you can access all views inside your child layout and assign them values
        Log.d("shredImage",sharedPreference.getString("Image", "Def").toString())
    }
//    fun setImageHeader(){
//        val dr = findViewById<DrawerLayout>(R.id.drawer)
//
//        if(dr.isDrawerOpen(GravityCompat.START)) {
//            dr.closeDrawer(GravityCompat.START)
//        }
//        else{
//
//            super.onBackPressed()
//        }
//    }
}