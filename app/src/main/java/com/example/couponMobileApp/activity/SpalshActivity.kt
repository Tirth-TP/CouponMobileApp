package com.example.couponMobileApp.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.couponMobileApp.LoginActivity
import com.example.couponMobileApp.R
import com.example.couponMobileApp.SharedPrefManager
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.MessageDigest
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class SpalshActivity : AppCompatActivity() {

    lateinit var topANim: Animation;
    lateinit var btANim: Animation;
    lateinit var img : ImageView;
    lateinit var  title : TextView;
    lateinit var desc:TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh)

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        hash()
        getCertificateSHA1Fingerprint()

        topANim = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        btANim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        img = findViewById(R.id.spImg)
        desc = findViewById(R.id.sptTxt)

        img.animation = topANim
        title.animation = btANim
        desc.animation = btANim
        Handler().postDelayed({
            if (SharedPrefManager.getInstance(this).isLoggedIn) {
                val i = (Intent(this, HomeActivity::class.java))
//                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
            }
            else{
                startActivity(Intent(this, LoginActivity::class.java))
            }
            Intent.FLAG_ACTIVITY_CLEAR_TOP
            Intent.FLAG_ACTIVITY_NEW_TASK
            finish()
        },4000)
    }
    @SuppressLint("PackageManagerGetSignatures")
    private fun hash() {
        val info: PackageInfo
        try {
            info =
                packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e("SHA key : ", md.toString())
                val something = String(Base64.encode(md.digest(), 0))
                Log.e("Hash key : ", something)
                println("Hash key : $something")
            }
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
    }

    private fun getCertificateSHA1Fingerprint() {
        val pm = this.packageManager
        val packageName = this.packageName
        val flags = PackageManager.GET_SIGNATURES
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = pm.getPackageInfo(packageName, flags)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val signatures = packageInfo!!.signatures
        val cert = signatures[0].toByteArray()
        val input: InputStream = ByteArrayInputStream(cert)
        var cf: CertificateFactory? = null
        try {
            cf = CertificateFactory.getInstance("X509")
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        var c: X509Certificate? = null
        try {
            c = cf!!.generateCertificate(input) as X509Certificate
        } catch (e: CertificateException) {
            e.printStackTrace()
        }
        val hexString = ""
        try {
            val md = MessageDigest.getInstance("SHA1")
            val publicKey = md.digest(c!!.encoded)
            Log.e("SHA : ", byte2HexFormatted(publicKey)!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun byte2HexFormatted(arr: ByteArray): String? {
        val str = StringBuilder(arr.size * 2)
        for (i in arr.indices) {
            var h = Integer.toHexString(arr[i].toInt())
            val l = h.length
            if (l == 1) h = "0$h"
            if (l > 2) h = h.substring(l - 2, l)
            str.append(h.toUpperCase())
            if (i < arr.size - 1) str.append(':')
        }
        return str.toString()
    }
}