package com.example.stocardapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.Constants.TAG
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody

class MyFirebaseMessegingService : FirebaseMessagingService() {

    lateinit var title:String
    lateinit var message:String
    var CHANNEL_ID = "CHANNEL"
    lateinit var manager:NotificationManager

    override fun onMessageReceived(remotemessage: RemoteMessage) {
        super.onMessageReceived(remotemessage)
        title = remotemessage.notification!!.title!!
        message = remotemessage.notification!!.body!!
        // message = Objects.requireNonNull(remotemessage.notification?.body)!!
        manager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        sendNotification()
    }

    private fun sendNotification() {
        var intent = Intent(applicationContext,HomeActivity::class.java)
        intent.putExtra("title",title)
        intent.putExtra("message",message)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            var channel = NotificationChannel(CHANNEL_ID,"pushnotification",NotificationManager.IMPORTANCE_HIGH)
           manager.createNotificationChannel(channel)
        }
        var builder = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setContentText(message)
        var pedingintent = PendingIntent.getActivity(applicationContext,0,intent,PendingIntent.FLAG_ONE_SHOT)
        builder.setContentIntent(pedingintent)
        manager.notify(0,builder.build())
    }
//    fun getManager():NotificationManager
//    {
//        var manager = this.getSystemService(Context.NOTIFICATION_SERVICE)
//        return (manager as NotificationManager)
//    }
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("Token","New Token")

        val p0 = FirebaseInstanceId.getInstance().token
        val SHARED_PREF_NAME = "my_shared_preff"
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE)
        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString("device_token",p0)
        editor.apply()
        editor.commit()

}
//    fun toPart(data: String): RequestBody {
//        return RequestBody.create("text/plain".toMediaTypeOrNull(), data)
//    }
}