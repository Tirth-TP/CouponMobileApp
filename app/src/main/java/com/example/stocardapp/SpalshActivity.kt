package com.example.stocardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class SpalshActivity : AppCompatActivity() {

    lateinit var topANim: Animation;
    lateinit var btANim: Animation;
    lateinit var img : ImageView;
    lateinit var  title : TextView;
    lateinit var desc:TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spalsh)
        topANim = AnimationUtils.loadAnimation(this,R.anim.top_animation)
        btANim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation)

        img = findViewById(R.id.spImg);
        title = findViewById(R.id.spTxt)
        desc = findViewById(R.id.sptTxt);

        img.animation = topANim;
        title.animation = btANim;
        desc.animation = btANim;
        Handler().postDelayed({
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        },4000)

    }
}