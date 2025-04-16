package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    var timerBinder : TimerService.TimerBinder? = null

    lateinit var timerTextView : TextView

    val timerHandler = Handler(Looper.getMainLooper()){
        timerTextView.text = it.what.toString()
        true
    }

    val serviceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            timerBinder = (p1 as TimerService.TimerBinder).apply {
                setHandler(timerHandler)
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            timerBinder = null
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.textView)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )

        findViewById<Button>(R.id.startButton).setOnClickListener {
            startOrPauseTimer()
        }

        findViewById<Button>(R.id.stopButton).setOnClickListener {
            stopTimer()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_start -> {startOrPauseTimer()}
            R.id.action_stop -> {stopTimer()}

            else -> return false
        }
        return true
    }

    private fun startOrPauseTimer() {
        timerBinder?.run {
            if (!isRunning && !paused) {
                start(30)
            }
            else {
                pause()
            }
        }
    }

    private fun stopTimer() {
        timerBinder?.stop()
    }
}