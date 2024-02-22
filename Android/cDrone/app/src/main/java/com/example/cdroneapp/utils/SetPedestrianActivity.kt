package com.example.cdroneapp.utils

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cdroneapp.R

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
}