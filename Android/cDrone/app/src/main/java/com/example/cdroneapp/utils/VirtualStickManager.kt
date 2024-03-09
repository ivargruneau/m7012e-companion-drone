package com.example.cdroneapp.utils
import androidx.lifecycle.MutableLiveData
import dji.sdk.keyvalue.value.flightcontroller.*
import dji.v5.common.callback.CommonCallbacks
import dji.v5.manager.aircraft.virtualstick.VirtualStickManager
import dji.v5.manager.aircraft.virtualstick.VirtualStickState
import dji.v5.manager.aircraft.virtualstick.VirtualStickStateListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



class VirtualStickManager {


    val currentSpeedLevel = MutableLiveData(10.0)

    val currentVirtualStickStateInfo = MutableLiveData(VirtualStickStateInfo())
    private lateinit var vsListener: VirtualStickStateListener




    public fun init(){
        currentSpeedLevel.value = VirtualStickManager.getInstance().speedLevel
        vsListener = object :
            VirtualStickStateListener {

            override fun onVirtualStickStateUpdate(stickState: VirtualStickState) {
                currentVirtualStickStateInfo.postValue(currentVirtualStickStateInfo.value?.apply {
                    this.state = stickState
                    GlobalScope.launch {LogHandler.log("Updated stick")}
                })
            }

            override fun onChangeReasonUpdate(reason: FlightControlAuthorityChangeReason) {
                currentVirtualStickStateInfo.postValue(currentVirtualStickStateInfo.value?.apply {
                    this.reason = reason
                    GlobalScope.launch {LogHandler.log("onChangeReasonUpdate: " + reason)}

                })
            }
        }
        VirtualStickManager.getInstance().setVirtualStickStateListener(vsListener)
    }

    fun enableVirtualStick(callback: CommonCallbacks.CompletionCallback) {
        VirtualStickManager.getInstance().enableVirtualStick(callback)
    }

    fun disableVirtualStick(callback: CommonCallbacks.CompletionCallback) {
        VirtualStickManager.getInstance().disableVirtualStick(callback)
    }

    fun setSpeedLevel(speedLevel: Double) {
        VirtualStickManager.getInstance().speedLevel = speedLevel
        currentSpeedLevel.value = speedLevel
    }

    fun setLeftPosition(horizontal: Int, vertical: Int) {
        VirtualStickManager.getInstance().leftStick.horizontalPosition = horizontal
        VirtualStickManager.getInstance().leftStick.verticalPosition = vertical
    }

    fun setRightPosition(horizontal: Int, vertical: Int) {
        VirtualStickManager.getInstance().rightStick.horizontalPosition = horizontal
        VirtualStickManager.getInstance().rightStick.verticalPosition = vertical
    }




    data class VirtualStickStateInfo(
        var state: VirtualStickState = VirtualStickState(false, FlightControlAuthority.UNKNOWN, false),
        var reason: FlightControlAuthorityChangeReason = FlightControlAuthorityChangeReason.UNKNOWN
    )



    public fun removeVSListener(){
        VirtualStickManager.getInstance().removeVirtualStickStateListener(vsListener)
    }




}