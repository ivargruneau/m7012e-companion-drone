package com.example.cdroneapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cdroneapp.utils.MediaVM
import dji.sdk.keyvalue.key.GimbalKey
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.media.MediaFile
import com.example.cdroneapp.utils.GimbalHandler
import com.example.cdroneapp.utils.LogHandler
import com.example.cdroneapp.utils.PhotoFetcher

class MainActivity : AppCompatActivity() {

    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView
    private lateinit var logView : TextView

    private lateinit var button1 :Button
    private lateinit var button2 :Button
    private lateinit var button3 :Button
    private lateinit var button4 :Button

    private lateinit var myApp : MyApplication
    private lateinit var mediaVM : MediaVM
    private lateinit var mediaList : List<MediaFile>
    private lateinit var gimbalHandler: GimbalHandler

    private lateinit var photoFetcher: PhotoFetcher
    var indexCounter = 0
    //val myApp = application as MyApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1 = findViewById<Button>(R.id.button1)
        textView1 = findViewById<TextView>(R.id.textView1)
        button2 = findViewById<Button>(R.id.button2)
        textView2 = findViewById<TextView>(R.id.textView2)
        button3 = findViewById<Button>(R.id.button3)
        textView3 = findViewById<TextView>(R.id.textView3)
        button4 = findViewById<Button>(R.id.button4)
        textView4 = findViewById<TextView>(R.id.textView4)
        logView = findViewById<TextView>(R.id.logTextView)
        mediaVM = MediaVM()
        mediaVM.init()
        photoFetcher = PhotoFetcher()
        photoFetcher.init(1000,mediaVM )
        button1.text = "Get Status"
        button2.text = "Photo"
        button3.text = "Start fetcher"
        button4.text = "Update Log"



        myApp = application as MyApplication
        gimbalHandler = GimbalHandler()
        gimbalHandler.init()

        button1.setOnClickListener {

            textView1.text = myApp.getStatusMessage()
            //mediaVM.pullMediaFileListFromCamera(-1, -1)



        }


        button2.setOnClickListener {
            //mediaVM.capturePhoto()
            //increment(textView2)
            mediaVM.capturePhoto()



        }
        button3.setOnClickListener {
            photoFetcher.start()

        }
        button4.setOnClickListener {
            updateLogView(this, logView)
            gimbalHandler.decreasePitch()

        }



    }

    fun updateLogView(activity: Activity, logView: TextView) {
        val logs = LogHandler.getLogs().joinToString("\n")
        activity.runOnUiThread {
            logView.text = logs
        }
    }















}