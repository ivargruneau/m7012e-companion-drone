package com.example.cdroneapp.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cdroneapp.R
import androidx.lifecycle.MutableLiveData

import dji.sdk.keyvalue.key.CameraKey
import dji.sdk.keyvalue.key.KeyTools
import dji.sdk.keyvalue.key.KeyTools.createKey

import dji.sdk.keyvalue.value.camera.CameraMode
import dji.sdk.keyvalue.value.common.ComponentIndexType
import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import dji.v5.common.error.RxError
import dji.v5.common.utils.CallbackUtils
import dji.v5.common.utils.RxUtil


class SetPedestrianActivity : AppCompatActivity() {

    private lateinit var button_ok : Button
    private lateinit var button_cancel : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_pedestrian)

        button_ok = findViewById<Button>(R.id.button_ok)
        button_cancel = findViewById<Button>(R.id.button_cancel)
        button_ok.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("result", "Screen 2 ok")
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

        button_cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }
    fun takePhoto(callback: CommonCallbacks.CompletionCallback) {
        val ttt = RxUtil.setValue(createKey<CameraMode>(
            CameraKey.KeyCameraMode), CameraMode.PHOTO_NORMAL)
            .andThen(RxUtil.performActionWithOutResult(createKey(CameraKey.KeyStartShootPhoto)))
            .subscribe({ CallbackUtils.onSuccess(callback) }
            ) { throwable: Throwable ->
                CallbackUtils.onFailure(
                    callback,
                    (throwable as RxError).djiError
                )
            }
    }

}