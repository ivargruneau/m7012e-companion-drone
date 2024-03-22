package com.example.cdroneapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import com.example.cdroneapp.utils.GimbalHandler
import com.example.cdroneapp.utils.LogHandler
import com.example.cdroneapp.utils.MovementHandler
import com.example.cdroneapp.utils.PhotoCapturer
import com.example.cdroneapp.utils.PhotoFetcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    private val logTextView by lazy { findViewById<TextView>(R.id.logTextView) }
    private val uiScope = CoroutineScope(Dispatchers.Main)
    private lateinit var movementHandler: MovementHandler

    private lateinit var forwardButton : Button
    private lateinit var backwardButton : Button
    private lateinit var yawLeftButton : Button
    private lateinit var yawRightButton : Button

    private lateinit var liftButton : Button
    private lateinit var landButton : Button

    private lateinit var upButton : Button
    private lateinit var hoverButton : Button
    private lateinit var downButton : Button

    private lateinit var startButton: Button
    private lateinit var panicButton : Button

    private lateinit var enableVSButton : Button
    private lateinit var disableVSButton : Button

    private lateinit var takePhotoButton: Button
    private lateinit var startFetchButton : Button
    private lateinit var stopCapButton: Button

    private lateinit var pitchUpButton: Button
    private lateinit var pitchDownButton : Button

    private lateinit var myApp : MyApplication

    private lateinit var gimbalHandler: GimbalHandler

    private lateinit var photoFetcher: PhotoFetcher
    private lateinit var photoCapturer: PhotoCapturer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        observeLogs()





        movementHandler = MovementHandler()
        photoFetcher = PhotoFetcher()
        photoFetcher.init(1000, movementHandler)


        photoCapturer = PhotoCapturer()
        photoCapturer.init(1000)
        movementHandler.init(photoCapturer)
        myApp = application as MyApplication
        gimbalHandler = GimbalHandler()
        gimbalHandler.init()

        setupButtons()
        setButtonListeners()

    }


    override fun onDestroy() {
        super.onDestroy()
        uiScope.cancel() // Cancel the coroutine scope when the activity is destroyed
    }

    private fun setupButtons(){
        forwardButton = findViewById<Button>(R.id.forward_button)
        backwardButton = findViewById<Button>(R.id.backward_button)
        yawLeftButton = findViewById<Button>(R.id.yawLeft_button)
        yawRightButton = findViewById<Button>(R.id.yawRight_button)


        liftButton = findViewById<Button>(R.id.lift_button)
        landButton = findViewById<Button>(R.id.land_button)

        upButton = findViewById<Button>(R.id.up_button)
        hoverButton = findViewById<Button>(R.id.hover_button)
        downButton = findViewById<Button>(R.id.down_button)

        panicButton = findViewById<Button>(R.id.panic_button)
        startButton = findViewById<Button>(R.id.start_button)

        enableVSButton = findViewById<Button>(R.id.enableVS_button)
        disableVSButton = findViewById<Button>(R.id.disableVS_button)

        takePhotoButton = findViewById<Button>(R.id.takePhoto_button)
        startFetchButton = findViewById<Button>(R.id.startFetch_button)
        stopCapButton = findViewById<Button>(R.id.stopCap_button)

        pitchUpButton = findViewById<Button>(R.id.pitchUp_button)
        pitchDownButton = findViewById<Button>(R.id.pitchDown_button)
    }
    private fun setButtonListeners(){
        forwardButton.setOnClickListener{movementHandler.moveForward()}
        backwardButton.setOnClickListener{movementHandler.moveBackward()}
        yawLeftButton.setOnClickListener{movementHandler.yawLeft()}
        yawRightButton.setOnClickListener{movementHandler.yawRight()}


        liftButton.setOnClickListener{movementHandler.initTakeOff()}
        landButton.setOnClickListener{movementHandler.initLanding()}

        upButton.setOnClickListener{movementHandler.moveUp()}
        hoverButton.setOnClickListener{movementHandler.stopMovement()}
        downButton.setOnClickListener{movementHandler.moveDown()}


        panicButton.setOnClickListener{movementHandler.stopMovement()}
        startButton.setOnClickListener{
            photoFetcher.start()
            movementHandler.startMovementHandler()
        }

        enableVSButton.setOnClickListener{movementHandler.startUpVirtualStick()}
        disableVSButton.setOnClickListener{movementHandler.shutDownVirtualStick()}

        takePhotoButton.setOnClickListener{photoCapturer.capturePhoto()}
        startFetchButton.setOnClickListener{photoFetcher.start()}
        stopCapButton.setOnClickListener{
            photoFetcher.stop()
            photoCapturer.stop()
        }

        pitchUpButton.setOnClickListener{gimbalHandler.changePitch(5.0)}
        pitchDownButton.setOnClickListener{gimbalHandler.changePitch(-5.0)}
    }
    private fun observeLogs() {
        uiScope.launch {
            LogHandler.logMessages.collect { messages ->
                logTextView.text = messages.joinToString("\n")
            }
        }
    }

















}