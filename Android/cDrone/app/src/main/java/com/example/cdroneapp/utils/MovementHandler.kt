package com.example.cdroneapp.utils
import android.os.Handler
import android.os.Looper
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.sdk.keyvalue.key.DJIKey
import dji.v5.et.get
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.KeyTools
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.v5.et.action
import dji.v5.et.create
import dji.v5.ux.core.base.DJISDKModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class MovementHandler {
    private lateinit var virtualStickManager : VirtualStickManager
    private var handler: Handler? = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var vsEnabled = false
    private var started = false
    private val desierdAltitude = 1.0
    private lateinit var photoCapturer : PhotoCapturer
    private var searchModeEnabled = false
    private val searchModeSpeed = 10
    private val buttonMoveSpeed = 10.0
    private val buttonYawSpeed = 30.0
    private val moveTime : Long = 2000
    public fun init(photoCapturer: PhotoCapturer) {
        this.photoCapturer = photoCapturer
    }
    public fun startMovementHandler(){

        if (!started) {
            started = true
            initTakeOff()
            altitudeChecker()
        }
    }
    public fun startUpVirtualStick() {
        if (!vsEnabled){
            virtualStickManager = VirtualStickManager()
            virtualStickManager.init()
            enableVS()
        }


    }

    public fun shutDownVirtualStick(){
        if (vsEnabled){
            removeListener()
            disableVS()
        }

    }

    private fun enableVS(){
        virtualStickManager.enableVirtualStick(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                vsEnabled = true
                GlobalScope.launch {LogHandler.log("enableVirtualStick complete")}
            }

            override fun onFailure(error: IDJIError) {
                GlobalScope.launch { LogHandler.log("enableVirtualStick error: " + error)}
            }
        })
    }

    private fun disableVS(){

        virtualStickManager.disableVirtualStick(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                vsEnabled = false
                GlobalScope.launch {LogHandler.log("disableVirtualStick complete")}

            }

            override fun onFailure(error: IDJIError) {
                GlobalScope.launch { LogHandler.log("disableVirtualStick error: " + error)}
            }
        })
    }

    public fun handleDirectionsResponse(detected : Boolean, distance : Double, horizontalAngle : Double, verticalAngle : Double){

        if (detected) {
            val rightStickVertical: Double = distance* 12.62
            val leftStickHorizontal : Double = horizontalAngle* 48.04

            performMovement(leftStickHorizontal, 0.0, 0.0, rightStickVertical )


        }
        else {
            photoCapturer.capturePhoto()
        }





    }

    fun stopAll(){
        if (vsEnabled) {
            virtualStickManager.setLeftPosition(0,0)
            virtualStickManager.setRightPosition(0,0)
        }
    }


    fun moveForward() {
        //virtualStickManager.setRightPosition(0,45)
        if (vsEnabled) {
            performMovement(0.0, 0.0, 0.0, buttonMoveSpeed)
        }



    }


    fun moveBackward () {
        //virtualStickManager.setRightPosition(0,-30)
        if (vsEnabled) {
            performMovement(0.0, 0.0, 0.0, -buttonMoveSpeed)
        }


    }





    fun yawLeft () {
        //virtualStickManager.setLeftPosition(-30,0)
        if (vsEnabled) {
            performMovement(-buttonYawSpeed, 0.0, 0.0, 0.0)
        }



    }


    fun yawRight() {
        //virtualStickManager.setLeftPosition(30,0)
        if (vsEnabled) {
            performMovement(buttonYawSpeed, 0.0, 0.0, 0.0)
        }


    }









    public fun initTakeOff() {
        startTakeOff(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {
                //GlobalScope.launch {LogHandler.log("takeOff onSuccess")}
            }

            override fun onFailure(error: IDJIError) {
                //GlobalScope.launch {LogHandler.log("takeOff onFailure, error: " + error)}
            }
        })
    }
    private fun startTakeOff(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyStartTakeoff.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })

    }

    public fun initLanding() {
        startLanding(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {
                //GlobalScope.launch {LogHandler.log("land onSuccess")}
            }

            override fun onFailure(error: IDJIError) {
                //GlobalScope.launch {LogHandler.log("land onFailure, error: " + error)}
            }
        })
    }
    private fun startLanding(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyStartAutoLanding.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })

    }





    private fun removeListener(){
        virtualStickManager.removeVSListener()
        //virtualStickVM.clear()
    }






    public fun performLandingConfirmationAction(){
        DJISDKModel.getInstance().performActionWithOutResult(KeyTools.createKey(FlightControllerKey.KeyConfirmLanding))

    }

    private fun performMovement(leftStickHorizontal : Double, leftStickVertical : Double, rightStickHorizontal : Double, rightStickVertical: Double ) {
        // remove existing callbacks
        runnable?.let { handler?.removeCallbacks(it) }
        if (!vsEnabled) {
            GlobalScope.launch {LogHandler.log("Virtualstick not can not execute fun performMovement ")}
        }
        else {
            var lSH : Int = leftStickHorizontal.roundToInt()
            var lSV : Int = leftStickVertical.roundToInt()
            var rSH : Int = rightStickHorizontal.roundToInt()
            var rSV : Int = rightStickVertical.roundToInt()



            virtualStickManager.setLeftPosition(lSH, lSV)
            virtualStickManager.setRightPosition(rSH,rSV)
        }



        // Schedule the task to freeze movement
        runnable = Runnable { stopMovement() }
        runnable?.let { handler?.postDelayed(it, moveTime) }

    }

    fun stopMovement() {
        runnable?.let { handler?.removeCallbacks(it) }
        if (vsEnabled) {
            virtualStickManager.setLeftPosition(0,0)
            virtualStickManager.setRightPosition(0,0)
            photoCapturer.capturePhoto()
        }




    }


    fun getHeight(): Double{

        var altitudeKey: DJIKey<Double> = FlightControllerKey.KeyAltitude.create()
        var height = (altitudeKey.get(0.0))
        return height


    }
    fun getMotorStatus() : Boolean{
        var motorStatus: DJIKey<Boolean> = FlightControllerKey.KeyAreMotorsOn.create()
        var status = motorStatus.get(false)
        return status


    }


    fun altitudeChecker() {
        // remove existing callbacks

        runnable?.let { handler?.removeCallbacks(it) }
        var height = getHeight()
        if (height <= desierdAltitude) {
            GlobalScope.launch { LogHandler.log("Altitude checker: altitude not reached")}
            runnable = Runnable { altitudeChecker() }

            // Schedule the task to freeze movement
            runnable?.let { handler?.postDelayed(it, 2000) }
        }
        else {
            GlobalScope.launch { LogHandler.log("Altitude checker: altitude reached")}
            beginSearchMode()
        }




    }
    private fun beginSearchMode(){
        searchModeEnabled = true
        startUpVirtualStick()
        //photoCapturer.start()

        virtualStickManager.setLeftPosition(searchModeSpeed,0)
        GlobalScope.launch { LogHandler.log("Search mode engaged")}
        photoCapturer.capturePhoto()
    }




}