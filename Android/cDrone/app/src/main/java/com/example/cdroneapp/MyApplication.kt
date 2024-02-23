package com.example.cdroneapp

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.cdroneapp.utils.FPVDemoApplication
import dji.v5.common.callback.CommonCallbacks.CompletionCallback
import dji.v5.common.error.IDJIError
import dji.v5.common.register.DJISDKInitEvent
import dji.v5.manager.SDKManager
import dji.v5.manager.interfaces.SDKManagerCallback


class MyApplication : Application() {
    private var statusString ="Not initialized"
    private val TAG = this::class.simpleName
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        // 在调用install前，请勿调用任何MSDK相关
        com.secneo.sdk.Helper.install(this)
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化MSDK，建议初始化逻辑放在Application中，当然也可以根据自己的需要放在任意地方。
        SDKManager.getInstance().init(this,object:SDKManagerCallback{
            override fun onInitProcess(event: DJISDKInitEvent?, totalProcess: Int) {
                Log.i(TAG, "onInitProcess: ")
                if (event == DJISDKInitEvent.INITIALIZE_COMPLETE) {
                    SDKManager.getInstance().registerApp()
                }
                else {
                    statusString = "INITIALIZE failed: "
                    Log.i(TAG, "INITIALIZE failed: ")
                }
            }
            override fun onRegisterSuccess() {
                statusString = "onRegisterSuccess: "
                Log.i(TAG, "onRegisterSuccess: ")
            }
            override fun onRegisterFailure(error: IDJIError?) {
                statusString = "onRegisterFailure: "
                Log.i(TAG, "onRegisterFailure: ")
            }
            override fun onProductConnect(productId: Int) {
                statusString = "onProductConnect: "
                Log.i(TAG, "onProductConnect: ")

            }
            override fun onProductDisconnect(productId: Int) {
                statusString = "onProductDisconnect: "
                Log.i(TAG, "onProductDisconnect: ")
            }
            override fun onProductChanged(productId: Int)
            {
                statusString = "onProductChanged: "
                Log.i(TAG, "onProductChanged: ")
            }
            override fun onDatabaseDownloadProgress(current: Long, total: Long) {
                statusString = "onDatabaseDownloadProgress: ${current/total}"
                Log.i(TAG, "onDatabaseDownloadProgress: ${current/total}")
            }
        })
    }
    fun getStatusMessage() : String {
        return statusString

    }





}