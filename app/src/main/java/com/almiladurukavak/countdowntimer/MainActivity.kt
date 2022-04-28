package com.almiladurukavak.countdowntimer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer

import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.almiladurukavak.countdowntimer.util.PreferencesUtil
import com.google.android.material.floatingactionbutton.FloatingActionButton
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.util.*


class MainActivity : AppCompatActivity() {


    companion object {

        fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, CountDownTimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PreferencesUtil.setAlarmSetTime(nowSeconds, context)
            return wakeUpTime

        }

        fun removeAlarm(context: Context) {

            val intent = Intent(context, CountDownTimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PreferencesUtil.setAlarmSetTime(0, context)
        }

        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / 1000
    }


    enum class CountDownTimerState {

        Stopped, Paused, Running

    }

    private lateinit var countDownTimer: CountDownTimer
    private var countDownTimerLengthSeconds: Long = 0
    private var countDownTimerState = CountDownTimerState.Stopped
    private var secondsRemaining: Long = 0

    private var countDownTv: TextView? = null
    private var progress_CountDown: MaterialProgressBar? = null

    private var fab_pause: FloatingActionButton? = null
    private var fab_play: FloatingActionButton? = null
    private var fab_stop: FloatingActionButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        countDownTv = findViewById(R.id.countDownTextView) as TextView
        progress_CountDown = findViewById(R.id.progressCountDown) as MaterialProgressBar

        fab_pause = findViewById(R.id.fab_pause) as FloatingActionButton
        fab_play = findViewById(R.id.fab_play) as FloatingActionButton
        fab_stop = findViewById(R.id.fab_stop) as FloatingActionButton


        fab_play?.setOnClickListener { v ->

            startCountDownTimer()
            countDownTimerState = CountDownTimerState.Running
            updateFabButtons()

        }
        fab_pause?.setOnClickListener { v ->

            countDownTimer.cancel()
            countDownTimerState = CountDownTimerState.Paused
            updateFabButtons()

        }
        fab_stop?.setOnClickListener { v ->

            countDownTimer.cancel()
            onTimerFinished()

        }


    }

    override fun onResume() {
        super.onResume()
        initCountDownTimer()
        removeAlarm(this)

}

    private fun initCountDownTimer() {

        countDownTimerState = PreferencesUtil.getCountDownTimerState(this)

        if (countDownTimerState == CountDownTimerState.Stopped)
            setNewCountDownTimerLength()
        else
            setPreviousCountDownTimerLength()

        secondsRemaining = if (countDownTimerState == CountDownTimerState.Running || countDownTimerState == CountDownTimerState.Paused)
                PreferencesUtil.getSecondsRemaining(this)
        else
                countDownTimerLengthSeconds

        val alarmSetTime = PreferencesUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0)

            secondsRemaining -= nowSeconds - alarmSetTime

        if (secondsRemaining <= 0)

            onTimerFinished()
        else if (countDownTimerState == CountDownTimerState.Running)
            startCountDownTimer()


        updateFabButtons()
        updateCountDownUI()


    }


    private fun setNewCountDownTimerLength() {
        val lengthInMinutes = PreferencesUtil.getCountDownTimerLength(this)
        countDownTimerLengthSeconds = (lengthInMinutes * 5L)
        progress_CountDown?.max = countDownTimerLengthSeconds.toInt()


    }

    private fun setPreviousCountDownTimerLength() {
        countDownTimerLengthSeconds = PreferencesUtil.getPreviousCountDownTimerLengthSeconds(this)

        progress_CountDown?.max = countDownTimerLengthSeconds.toInt()

    }

    private fun updateCountDownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        countDownTv?.text = ":${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"

        progress_CountDown?.progress = (countDownTimerLengthSeconds - secondsRemaining).toInt()


    }

    private fun updateFabButtons() {
        when (countDownTimerState) {

            CountDownTimerState.Running -> {

                fab_play?.isEnabled = false
                fab_pause?.isEnabled = true
                fab_stop?.isEnabled = true
            }
            CountDownTimerState.Stopped -> {

                fab_play?.isEnabled = true
                fab_pause?.isEnabled = false
                fab_stop?.isEnabled = false
            }
            CountDownTimerState.Paused -> {

                fab_play?.isEnabled = true
                fab_pause?.isEnabled = false
                fab_stop?.isEnabled = true
            }

        }

    }


    private fun onTimerFinished() {

        countDownTimerState = CountDownTimerState.Stopped
        setNewCountDownTimerLength()

        progress_CountDown?.progress = 0
        PreferencesUtil.setSecondsRemaining(countDownTimerLengthSeconds, this)
        secondsRemaining = countDownTimerLengthSeconds

        updateFabButtons()
        updateCountDownUI()


    }

    private fun startCountDownTimer() {
        countDownTimerState = CountDownTimerState.Running

        countDownTimer = object : CountDownTimer(secondsRemaining * 1000, 1000) {

            override fun onFinish() = onTimerFinished()
            override fun onTick(millisUntilFinished: Long) {

                secondsRemaining = millisUntilFinished / 1000
                updateCountDownUI()

            }
        }.start()


    }


    override fun onPause() {
        super.onPause()

        if (countDownTimerState == CountDownTimerState.Running) {
            countDownTimer.cancel()

            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)

        } else if (countDownTimerState == CountDownTimerState.Paused) {


        }

        PreferencesUtil.setPreviousCountDownTimerLengthSeconds(countDownTimerLengthSeconds, this)
        PreferencesUtil.setSecondsRemaining(secondsRemaining, this)
        PreferencesUtil.setCountDownTimerState(countDownTimerState, this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


}