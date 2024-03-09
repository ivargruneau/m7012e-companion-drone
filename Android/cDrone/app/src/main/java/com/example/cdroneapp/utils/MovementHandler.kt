package com.example.cdroneapp.utils
import java.time.LocalDateTime
import com.example.cdroneapp.utils.VirtualStickVM
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.manager.datacenter.MediaDataCenter
import dji.v5.manager.datacenter.media.MediaFileListState
import com.example.cdroneapp.utils.BasicAircraftControlVM
import dji.sdk.keyvalue.value.common.EmptyMsg
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.abs

class MovementHandler {
    private lateinit var virtualStickVM : VirtualStickVM
    private lateinit var basicAircraftControlVM : BasicAircraftControlVM
    public fun init() {

        virtualStickVM = VirtualStickVM()
        virtualStickVM.init()
        //basicAircraftControlVM = BasicAircraftControlVM()
    }
    public fun start() {
        //virtualStickVM.listenRCStick()
        enableVS()
    }
    private fun enableVS(){
        virtualStickVM.enableVirtualStick(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {

            }

            override fun onFailure(error: IDJIError) {

            }
        })
    }

    public fun handleDirections(distance : Double, horizontalAngle : Double, verticalAngle : Double){
        if (horizontalAngle==-1.0){
            virtualStickVM.setLeftPosition(30,0)
            val currentTimeMillis = System.currentTimeMillis()
            while (1000>currentTimeMillis){

            }
            virtualStickVM.setLeftPosition(0,0)
        }
        else if (horizontalAngle==1.0) {
            virtualStickVM.setLeftPosition(-30, 0)
            val currentTimeMillis = System.currentTimeMillis()
            while (1000>currentTimeMillis){

            }
            virtualStickVM.setLeftPosition(0,0)
        }
        else{
        }

        if(distance!=0.0){
            virtualStickVM.setRightPosition(0,30)
            val currentTimeMillis = System.currentTimeMillis()
            while (distance*1000>currentTimeMillis){

            }
            virtualStickVM.setRightPosition(0,30)

        }



    }

    fun stopAll(){
        virtualStickVM.setLeftPosition(0,0)
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
        virtualStickVM.setRightPosition(0,45)

    }


    fun moveBackward () {
        virtualStickVM.setRightPosition(0,-30)
    }


    fun moveStop() {
        //Stops all movement (forward and backward movement)
        virtualStickVM.setRightPosition(0,0)
    }


    fun yawLeft () {
        virtualStickVM.setLeftPosition(-30,0)

    }


    fun yawRight() {
        virtualStickVM.setLeftPosition(30,0)
    }


    fun yawStop() {
        //Stops all yaw (left and right yaw movement)
        virtualStickVM.setLeftPosition(0,0)
    }

    fun initTakeoff() {
        basicAircraftControlVM.startTakeOff(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {

            }

            override fun onFailure(error: IDJIError) {

            }
        })


    }

    fun initLanding() {
        basicAircraftControlVM.startLanding(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {

            }

            override fun onFailure(error: IDJIError) {

            }
        })

    }
    public fun start_yaw(){
        virtualStickVM.start_yaw(30)
    }
    public fun stop_yaw(){
        virtualStickVM.stop_yaw()
    }

    public fun disableVS(){

        virtualStickVM.disableVirtualStick(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {

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
    fun start_takeoff() {
        basicAircraftControlVM.startTakeOff(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {
                GlobalScope.launch {
                    LogHandler.log("start_takeoff done")
                }
            }

            override fun onFailure(error: IDJIError) {
                GlobalScope.launch {
                    LogHandler.log("start_takeoff error: " + error)
                }
            }
        })
    }

    fun start_landing() {
        basicAircraftControlVM.startLanding(object :
            CommonCallbacks.CompletionCallbackWithParam<EmptyMsg> {
            override fun onSuccess(t: EmptyMsg?) {
                GlobalScope.launch {
                    LogHandler.log("start_landing done")
                }
            }

            override fun onFailure(error: IDJIError) {
                GlobalScope.launch {
                    LogHandler.log("start_landing error: " + error)
                }
            }
        })
    }
    public fun yaw_test_start(){


        virtualStickVM.setLeftPosition(30,0)

    }

    public fun yaw_test_stop(){


        virtualStickVM.setLeftPosition(0,0)

    }
    public fun clear(){

        //virtualStickVM.clear()
    }

    public fun removeListener(){
        virtualStickVM.removeVSListener()
        //virtualStickVM.clear()
    }



}