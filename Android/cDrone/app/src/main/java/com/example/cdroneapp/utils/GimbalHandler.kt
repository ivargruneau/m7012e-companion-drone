package com.example.cdroneapp.utils


import dji.sdk.keyvalue.key.DJIKey
import dji.sdk.keyvalue.key.FlightControllerKey
import dji.sdk.keyvalue.key.GimbalKey
import dji.sdk.keyvalue.key.KeyTools
import dji.sdk.keyvalue.key.KeyTools.createKey
import dji.sdk.keyvalue.key.co_v.KeyGimbalMode
import dji.sdk.keyvalue.value.common.EmptyMsg
import dji.sdk.keyvalue.value.gimbal.GimbalMode
import dji.sdk.keyvalue.value.gimbal.PostureFineTuneAxis
import dji.v5.common.callback.CommonCallbacks.CompletionCallbackWithParam
import dji.v5.common.error.IDJIError
import dji.v5.et.create
import dji.v5.manager.KeyManager
import dji.v5.manager.SDKManager
import dji.v5.manager.datacenter.media.*
import dji.v5.ux.core.base.DJISDKModel
import dji.v5.ux.core.communication.ObservableInMemoryKeyedStore
import dji.v5.ux.gimbal.GimbalFineTuneWidgetModel
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.utils.CallbackUtils
import io.reactivex.rxjava3.core.Completable

class GimbalHandler {
    private lateinit var djiSdkModel : DJISDKModel
    private lateinit var uxKeyManager : ObservableInMemoryKeyedStore
    private lateinit var gimbalFineTuneWidgetModel: GimbalFineTuneWidgetModel
    var currentAxis: PostureFineTuneAxis = PostureFineTuneAxis.PITCH_AXIS

    fun init(){
        djiSdkModel = DJISDKModel.getInstance()
        uxKeyManager = ObservableInMemoryKeyedStore.getInstance()
        gimbalFineTuneWidgetModel = GimbalFineTuneWidgetModel(djiSdkModel, uxKeyManager)
    }

    public fun changePitch(pitchVal : Double){
        gimbalFineTuneWidgetModel.fineTunePosture(currentAxis, pitchVal).subscribe()
    }


}