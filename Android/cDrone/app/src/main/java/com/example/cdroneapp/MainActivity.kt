package com.example.cdroneapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.value.common.LocationCoordinate2D
import dji.v5.et.create
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dji.v5.common.error.IDJIError
import dji.v5.common.register.DJISDKInitEvent
import dji.v5.manager.SDKManager
import dji.v5.manager.interfaces.SDKManagerCallback
import dji.v5.et.get
import com.example.cdroneapp.utils.VirtualStickVM
import dji.sdk.keyvalue.key.DJIKey
import dji.v5.common.callback.CommonCallbacks
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.v5.manager.KeyManager
import com.example.cdroneapp.utils.BasicAircraftControlVM





class MainActivity : AppCompatActivity() {
    var altitudeKey: DJIKey<Double> = FlightControllerKey.KeyAltitude.create()
    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var button1 :Button
    private lateinit var button2 :Button
    private lateinit var button3 :Button

    private lateinit var myApp : MyApplication

    private val basicAircraftControlVM = BasicAircraftControlVM()
    private val virtualStickVM = VirtualStickVM()
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
            //textView3.text = getHeight().toString()
            virtualStickVM.listenRCStick()





        }
        button3.setOnClickListener {
            //textView3.text = myApp.getStatusMessage()
            var ttt = getHeight()
            textView3.text = ttt.toString()




        }
    }
    fun enableVS() {
        virtualStickVM.enableVirtualStick(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                textView3.text =("enableVirtualStick success.")
            }

            override fun onFailure(error: IDJIError) {
                textView3.text =("enableVirtualStick error,$error")
            }
        })
    }
    fun getAircraftLocation() = FlightControllerKey.KeyAircraftLocation.create().get(LocationCoordinate2D(0.0, 0.0))

    private fun getHeight(): Double = (altitudeKey.get(0.0))

    private fun initTakeoff() {
        basicAircraftControlVM.startTakeOff(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {
                textView2.text = ("start takeOff onSuccess.")
            }

            override fun onFailure(error: IDJIError) {
                textView2.text =("start takeOff onFailure,$error")
            }
        })
    }








}