package com.example.cuti_ta.Helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.cuti_ta.LoginActivity
import com.example.cuti_ta.R
import com.example.cuti_ta.pegawai.IzinDetailPegawaiActivity
import com.example.cuti_ta.pimpinan.ArsipDetailPimpinanActivity

class NotificationHelper(private val context: Context) {

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun createNotificationChannel(channelId: String, channelName: String, importance: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCutiNotif(nama: String, pesan: String) {
        val channelId = "channel_cuti"
        val channelName = "Channel Cuti"
        val importance = NotificationManager.IMPORTANCE_HIGH

        createNotificationChannel(channelId, channelName, importance)

        val intent = Intent(context, LoginActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(nama)
            .setContentText(pesan)
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

    fun notifTelat(nama:String, pesan: String, id: String) {
        val channelId = "channel_telat"
        val channelName = "Channel Telat"
        val importance = NotificationManager.IMPORTANCE_HIGH

        createNotificationChannel(channelId, channelName, importance)

        val intent = Intent(context, ArsipDetailPimpinanActivity::class.java)

        intent.putExtra("idCuti", id)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(nama)
            .setContentText(pesan)
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }

    fun notifTelatKembali(nama:String, pesan: String, id: String) {
        val channelId = "channel_telat"
        val channelName = "Channel Telat"
        val importance = NotificationManager.IMPORTANCE_HIGH

        createNotificationChannel(channelId, channelName, importance)

        val intent = Intent(context, IzinDetailPegawaiActivity::class.java)

        intent.putExtra("idIzin", id)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(nama)
            .setContentText(pesan)
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(3, notification)
    }
}
