package com.almiladurukavak.countdowntimer.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaActionSound
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.Settings

import android.view.autofill.AutofillId
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.startActivity
import com.almiladurukavak.countdowntimer.CountDownTimerExpiredReceiver
import com.almiladurukavak.countdowntimer.MainActivity
import com.almiladurukavak.countdowntimer.R

class NotiUtil {



    companion object{

        private const val CHANNEL_ID_COUNTDOWNTIMER="countdowntimer"
        private const val CHANNEL_NAME_COUNTDOWNTIMER="CountDownTimer"
        private const val TIMER_ID=0



        private fun getBasicNotificationBuilder(context: Context,channelId: String,playSound: Boolean):NotificationCompat.Builder{


            val notificationSound: Uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.mixkit)
            val nBuilder= NotificationCompat.Builder(context,channelId)
                .setSmallIcon(R.drawable.ic_baseline_access_time_24)
                .setAutoCancel(true)
                .setDefaults(0)
                .setSilent(false)
                .setSound(notificationSound)

            if (playSound)nBuilder.setSound(notificationSound)
            return nBuilder

        }

        @RequiresApi(Build.VERSION_CODES.S)
        private fun <T> getPendingIntentWithStack(context: Context, javaClass: Class<T>):PendingIntent
        {
            val resultIntent= Intent(context,javaClass)
            resultIntent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0,PendingIntent.FLAG_MUTABLE)

        }

        @TargetApi(26)
        private fun NotificationManager.createNotificationChannel(channelId: String,
        channelName: String,playSound: Boolean){
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){

                val channelImportance=if (playSound)NotificationManager.IMPORTANCE_HIGH
                else NotificationManager.IMPORTANCE_HIGH
                val nChannel= NotificationChannel(channelId,channelName,channelImportance)
                nChannel.enableLights(true)
                nChannel.lightColor= Color.BLACK
                this.createNotificationChannel(nChannel)

            }


        }

        @RequiresApi(Build.VERSION_CODES.S)
        fun showTimerExpired(context: Context){
            val startIntent =Intent(context,CountDownTimerExpiredReceiver::class.java)
            val startPendingIntent=PendingIntent.getBroadcast(context,0,startIntent,PendingIntent.FLAG_MUTABLE)
            val notificationSound: Uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.mixkit)

            val nBuilder= getBasicNotificationBuilder(context, CHANNEL_ID_COUNTDOWNTIMER,true)
            nBuilder.setContentTitle("Time !")
                .setContentText("Great Effort")
                .setSilent(false)
                .setSound(notificationSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(getPendingIntentWithStack(context,MainActivity::class.java))


            val nManager= context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


            nManager.createNotificationChannel(CHANNEL_ID_COUNTDOWNTIMER,
                CHANNEL_NAME_COUNTDOWNTIMER,true)

            nManager.notify(TIMER_ID,nBuilder.build())
        }




    }

}

