package com.almiladurukavak.countdowntimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.almiladurukavak.countdowntimer.util.NotiUtil
import com.almiladurukavak.countdowntimer.util.PreferencesUtil

class CountDownTimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {


        NotiUtil.showTimerExpired(context)
        PreferencesUtil.setCountDownTimerState(MainActivity.CountDownTimerState.Stopped, context)
        PreferencesUtil.setAlarmSetTime(0, context)
    }
}