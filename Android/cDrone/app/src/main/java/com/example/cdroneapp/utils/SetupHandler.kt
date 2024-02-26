package com.example.cdroneapp.utils
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cdroneapp.R
import java.util.*
import com.example.cdroneapp.MyApplication
class SetupHandler : AppCompatActivity() {
    val timer = Timer()
    var connectedToDrone = false
    private lateinit var myApp : MyApplication


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_pedestrian)
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {

            }
        }, 0L, 1000L)

        val returnIntent = Intent()
        returnIntent.putExtra("result", "Setup Complete")
        setResult(Activity.RESULT_OK, returnIntent)


    }
    // Schedule the task to run every 10 seconds

}