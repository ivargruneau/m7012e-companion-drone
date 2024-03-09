package com.example.cdroneapp

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import dji.sdk.keyvalue.key.GimbalKey
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.media.MediaFile
import com.example.cdroneapp.utils.GimbalHandler
import com.example.cdroneapp.utils.LogHandler
import com.example.cdroneapp.utils.PhotoCapturer
import com.example.cdroneapp.utils.PhotoFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.example.cdroneapp.utils.MovementHandler
import com.example.cdroneapp.utils.BasicAircraftControlVM
import dji.sdk.keyvalue.value.common.EmptyMsg

class MainActivity : AppCompatActivity() {

    private lateinit var basicAircraftControlVM : BasicAircraftControlVM
    private val logTextView by lazy { findViewById<TextView>(R.id.logTextView) }
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private lateinit var logView : TextView
    private lateinit var movementHandler: MovementHandler

    private lateinit var forwardButton : Button
    private lateinit var backwardButton : Button
    private lateinit var yawRightButton : Button
    private lateinit var yawLeftButton : Button

    private lateinit var liftButton : Button
    private lateinit var landButton : Button

    private lateinit var upButton : Button
    private lateinit var hoverButton : Button
    private lateinit var downButton : Button

    private lateinit var startButton: Button
    private lateinit var panicButton : Button


    private lateinit var myApp : MyApplication

    private lateinit var gimbalHandler: GimbalHandler

    private lateinit var photoFetcher: PhotoFetcher
    private lateinit var photoCapturer: PhotoCapturer

    //val myApp = application as MyApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        observeLogs()

        forwardButton = findViewById<Button>(R.id.forward_button)
        backwardButton = findViewById<Button>(R.id.backward_button)
        yawRightButton = findViewById<Button>(R.id.yawRight_button)
        yawLeftButton = findViewById<Button>(R.id.yawLeft_button)

        liftButton = findViewById<Button>(R.id.lift_button)
        landButton = findViewById<Button>(R.id.land_button)

        upButton = findViewById<Button>(R.id.up_button)
        hoverButton = findViewById<Button>(R.id.hover_button)
        downButton = findViewById<Button>(R.id.down_button)

        panicButton = findViewById<Button>(R.id.panic_button)
        startButton = findViewById<Button>(R.id.start_button)

        logView  = findViewById<TextView>(R.id.logTextView)

        //photoFetcher = PhotoFetcher()
        //photoFetcher.init(1000)

        //photoCapturer = PhotoCapturer()
        //photoCapturer.init(1000)

        myApp = application as MyApplication
        //gimbalHandler = GimbalHandler()
        //gimbalHandler.init()
        movementHandler = MovementHandler()
        movementHandler.init()
        basicAircraftControlVM = BasicAircraftControlVM()

        startButton.setOnClickListener {
            movementHandler.start()
            //photoFetcher.start()
            //photoCapturer.start()



        }

        liftButton.setOnClickListener{
            //movementHandler.start_takeoff()
            takeOff()

        }
        landButton.setOnClickListener{
            //land()
            movementHandler.test_land()
        }
        yawLeftButton.setOnClickListener{
            movementHandler.yaw_test_start()
        }
        hoverButton.setOnClickListener{
            movementHandler.yaw_test_start()
        }

        panicButton.setOnClickListener{
            //movementHandler.disableVS()
            movementHandler.clear()
        }



        //button1.setOnClickListener {

            //textView1.text = myApp.getStatusMessage()
            //mediaVM.pullMediaFileListFromCamera(-1, -1)



        //}



    }


    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel() // Cancel the coroutine scope when the activity is destroyed
    }


    private fun observeLogs() {
        uiScope.launch {
            LogHandler.logMessages.collect { messages ->
                logView.text = messages.joinToString("\n")
            }
        }
    }
    private fun takeOff(){
        basicAircraftControlVM.startTakeOff(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {

            }

            override fun onFailure(error: IDJIError) {

            }
        })
    }

    private fun land(){
        basicAircraftControlVM.startLanding(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {

            }

            override fun onFailure(error: IDJIError) {

            }
        })
    }



















}