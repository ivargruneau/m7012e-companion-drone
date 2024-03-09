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


class MovementHandler {
    private lateinit var virtualStickManager : VirtualStickManager
    private var handler: Handler? = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var vsEnabled = false
    private var started = false
    private val desierdAltitude = 1.0
    private lateinit var photoCapturer : PhotoCapturer

    public fun init(photoCapturer: PhotoCapturer) {
        this.photoCapturer = photoCapturer
    }
    public fun startMH(){

        if (!started) {
            started = true
            initTakeOff()
            altitudeChecker()
        }
    }
    public fun startUpVirtualStick() {

        virtualStickManager = VirtualStickManager()
        virtualStickManager.init()
        enableVS()

    }

    public fun shutDownVirtualStick(){
        removeListener()
        disableVS()
    }

    private fun enableVS(){
        virtualStickManager.enableVirtualStick(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                vsEnabled = true
            }

            override fun onFailure(error: IDJIError) {

            }
        })
    }

    public fun handleDirectionsResponse(detected : Boolean, distance : Double, horizontalAngle : Double, verticalAngle : Double){
        if (detected) {
            if (horizontalAngle==-1.0){
                virtualStickManager.setLeftPosition(30,0)
                val currentTimeMillis = System.currentTimeMillis()
                while (1000>currentTimeMillis){

                }
                virtualStickManager.setLeftPosition(0,0)
            }
            else if (horizontalAngle==1.0) {
                virtualStickManager.setLeftPosition(-30, 0)
                val currentTimeMillis = System.currentTimeMillis()
                while (1000>currentTimeMillis){

                }
                virtualStickManager.setLeftPosition(0,0)
            }
            else{
            }

            if(distance!=0.0){
                virtualStickManager.setRightPosition(0,30)
                val currentTimeMillis = System.currentTimeMillis()
                while (distance*1000>currentTimeMillis){

                }
                virtualStickManager.setRightPosition(0,30)

            }

        }



    }

    fun stopAll(){
        virtualStickManager.setLeftPosition(0,0)
        //virtualStickVM.setRightPosition(0,0)
    }

    //Handles forward and backward movement
    private fun move(){

    }

    //Handles yaw
    private fun yaw() {

    }

    //Handles the cameras pitch
    private fun pitch(){

    }

    fun moveForward() {
        virtualStickManager.setRightPosition(0,45)

    }


    fun moveBackward () {
        virtualStickManager.setRightPosition(0,-30)
    }


    fun moveStop() {
        //Stops all movement (forward and backward movement)
        virtualStickManager.setRightPosition(0,0)
    }


    fun yawLeft () {
        virtualStickManager.setLeftPosition(-30,0)

    }


    fun yawRight() {
        virtualStickManager.setLeftPosition(30,0)
    }


    fun yawStop() {
        //Stops all yaw (left and right yaw movement)
        virtualStickManager.setLeftPosition(0,0)
    }





    public fun disableVS(){

        virtualStickManager.disableVirtualStick(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                vsEnabled = false
                GlobalScope.launch {
                    LogHandler.log("disableVirtualStick done")
                }

            }

            override fun onFailure(error: IDJIError) {
                GlobalScope.launch {
                    LogHandler.log("disableVirtualStick error: " + error)
                }
            }
        })
    }
    public fun initTakeOff() {
        startTakeOff(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {
                GlobalScope.launch {LogHandler.log("takeOff onSuccess")}
            }

            override fun onFailure(error: IDJIError) {
                GlobalScope.launch {LogHandler.log("takeOff onFailure, error: " + error)}
            }
        })
    }

    public fun initLanding() {
        startLanding(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {
                GlobalScope.launch {LogHandler.log("land onSuccess")}
            }

            override fun onFailure(error: IDJIError) {
                GlobalScope.launch {LogHandler.log("land onFailure, error: " + error)}
            }
        })
    }
    public fun yaw_test_start(){


        virtualStickManager.setLeftPosition(30,0)

    }

    public fun yaw_test_stop(){


        virtualStickManager.setLeftPosition(0,0)

    }
    public fun clear(){

        //virtualStickVM.clear()
    }

    public fun removeListener(){
        virtualStickManager.removeVSListener()
        //virtualStickVM.clear()
    }


    private fun startTakeOff(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyStartTakeoff.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })

    }

    private fun startLanding(callback: CommonCallbacks.CompletionCallbackWithParam<EmptyMsg>) {
        FlightControllerKey.KeyStartAutoLanding.create().action({
            callback.onSuccess(it)
        }, { e: IDJIError ->
            callback.onFailure(e)
        })

    }

    public fun initSearchMode() {

    }
    private fun performLandingConfirmationAction(){
        DJISDKModel.getInstance().performActionWithOutResult(KeyTools.createKey(FlightControllerKey.KeyConfirmLanding))

    }

    fun scheduleFreeze() {
        // remove existing callbacks
        runnable?.let { handler?.removeCallbacks(it) }

        runnable = Runnable { freezeMovement() }

        // Schedule the task to freeze movement
        runnable?.let { handler?.postDelayed(it, 2000) }

    }

    fun freezeMovement() {

        runnable?.let { handler?.removeCallbacks(it) }


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
            GlobalScope.launch { LogHandler.log("Altetude checker, if")}
            runnable = Runnable { altitudeChecker() }

            // Schedule the task to freeze movement
            runnable?.let { handler?.postDelayed(it, 2000) }
        }
        else {
            GlobalScope.launch { LogHandler.log("Altetude checker. else")}
            beginSearchMode()
        }




    }
    public fun beginSearchMode(){
        startUpVirtualStick()
        photoCapturer.start()
        virtualStickManager.setLeftPosition(30,0)
        GlobalScope.launch { LogHandler.log("search mode comp")}
    }




}