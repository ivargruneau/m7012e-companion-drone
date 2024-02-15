package com.example.cdroneapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView


import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dji.v5.common.error.IDJIError
import dji.v5.common.register.DJISDKInitEvent
import dji.v5.manager.SDKManager
import dji.v5.manager.interfaces.SDKManagerCallback


class MainActivity : AppCompatActivity() {
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView

    private lateinit var button1 :Button
    private lateinit var button2 :Button
    private lateinit var button3 :Button
    private lateinit var myApp : MyApplication
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


        myApp = application as MyApplication
        button1.setOnClickListener {
            textView1.text = "Status msg: " + myApp.getStatusMessage()


        }

        button2.setOnClickListener {
            //increment(textView2)


        }
        button3.setOnClickListener {
            //textView3.text = myApp.getStatusMessage()


        }
    }








}