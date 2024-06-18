package com.example.gebung.ui.customdialog

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.gebung.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var auth: FirebaseAuth

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("AlarmReceiver", "Alarm muncul pada pukul 7 pagi")
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        auth = Firebase.auth
        val firebaseUser = auth.currentUser

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "Gebung Reminder"
        val builder = NotificationCompat.Builder(context,channelId)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle("What is your today's transactions, " + firebaseUser?.displayName + "?")
            .setContentText("Don't forget to fill in your daily expenses for today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(1, builder.build())
    }
}