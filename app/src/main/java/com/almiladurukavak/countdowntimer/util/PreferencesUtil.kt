package com.almiladurukavak.countdowntimer.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.preference.PreferenceManager
import com.almiladurukavak.countdowntimer.MainActivity

class PreferencesUtil {

    companion object{
        private const val TIMER_LENGTH_ID = "com.almiladurukavak.countdowntimer.timer_length"

        fun getCountDownTimerLength(context: Context): Int{
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getInt(TIMER_LENGTH_ID, 1)
        }

        private const val PREVIOUS_COUNTDOWN_TIMER_LENGTH_SECOND_ID="com.almiladurukavak.countdowntimer.previous_countdowntimer_length"

        fun getPreviousCountDownTimerLengthSeconds(context:Context): Long {

          val preferences =PreferenceManager.getDefaultSharedPreferences(context)
          return preferences.getLong (PREVIOUS_COUNTDOWN_TIMER_LENGTH_SECOND_ID,0)

        }

        fun setPreviousCountDownTimerLengthSeconds(seconds: Long,context:Context) {

            val editor =PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_COUNTDOWN_TIMER_LENGTH_SECOND_ID,seconds)
            editor.apply()
        }

        private const val COUNTDOWNTIMER_STATE_ID="com.almiladurukavak.countdowntimer.countdowntimer_state"
        fun getCountDownTimerState(context: Context):MainActivity.CountDownTimerState{
            val preferences= PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal=preferences.getInt(COUNTDOWNTIMER_STATE_ID,0)
            return MainActivity.CountDownTimerState.values()[ordinal]
        }
        fun setCountDownTimerState(state: MainActivity.CountDownTimerState,context: Context){
            val editor =PreferenceManager.getDefaultSharedPreferences(context).edit()
            var ordinal=state.ordinal
            editor.putInt(COUNTDOWNTIMER_STATE_ID,ordinal)
            editor.apply()

        }

        private const val SECONDS_REMAINING_ID="com.almiladurukavak.countdowntimer.seconds_remaining"

        fun getSecondsRemaining(context:Context): Long {

            val preferences =PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong (SECONDS_REMAINING_ID,0)

        }

        fun setSecondsRemaining(seconds: Long,context:Context) {

            val editor =PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(SECONDS_REMAINING_ID,seconds)
            editor.apply()
        }
        private const val ALARM_SET_TIME_ID= "com.almiladurukavak.countdowntimer.backgrounded_time"

        fun getAlarmSetTime(context: Context):Long{

            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(ALARM_SET_TIME_ID,0)

        }
        fun setAlarmSetTime(time: Long, context: Context){

            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(ALARM_SET_TIME_ID,time)
            editor.apply()


        }



    }
}