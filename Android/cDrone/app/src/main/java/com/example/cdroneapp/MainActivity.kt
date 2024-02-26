package com.example.cdroneapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.app.Activity
import android.content.Intent

import dji.v5.common.callback.CommonCallbacks
import dji.v5.common.error.IDJIError
import com.example.cdroneapp.utils.MediaVM
import com.example.cdroneapp.utils.SetupHandler
import dji.v5.manager.datacenter.media.MediaFile


class MainActivity : AppCompatActivity() {

    private lateinit var textView1: TextView
    private lateinit var textView2: TextView
    private lateinit var textView3: TextView
    private lateinit var textView4: TextView

    private lateinit var button1 :Button
    private lateinit var button2 :Button
    private lateinit var button3 :Button
    private lateinit var button4 :Button

    private lateinit var myApp : MyApplication
    private lateinit var mediaVM : MediaVM
    private lateinit var mediaList : List<MediaFile>
    var indexCounter = 0
    //val myApp = application as MyApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1 = findViewById<Button>(R.id.button1)
        button1.text = "Start Setup"
        textView1 = findViewById<TextView>(R.id.textView1)
        button2 = findViewById<Button>(R.id.button2)
        textView2 = findViewById<TextView>(R.id.textView2)
        button3 = findViewById<Button>(R.id.button3)
        textView3 = findViewById<TextView>(R.id.textView3)
        button4 = findViewById<Button>(R.id.button4)
        textView4 = findViewById<TextView>(R.id.textView4)
        mediaVM = MediaVM()
        mediaVM.init()



        myApp = application as MyApplication
        button1.setOnClickListener {
            val intent = Intent(this, SetupHandler::class.java)
            startActivityForResult(intent, SETUP_ACTIVITY_REQUEST_CODE)
            // Assuming you renamed SecondActivity to SetPedestrianActivity, this should be SetPedestrianActivity::class.java



        }


        button2.setOnClickListener {
            //increment(textView2)
            beginPhoto()

        }
        button3.setOnClickListener {
            mediaVM.pullMediaFileListFromCamera(-1, -1)
            mediaList = mediaVM.getMediaFileList()
            textView3.text = mediaList.size.toString()

        }
        button4.setOnClickListener {

            if (mediaList.size != 0) {
                var tempList : List<MediaFile>
                tempList =  List<MediaFile>(1) {mediaList[0]}
                mediaVM.downloadMediaFile(tempList)
                textView4.text = "started downloading"
            }
            /*
            if (mediaList.size != 0) {
                if (mediaList.size> indexCounter) {
                    val target = mediaList[indexCounter]

                    textView4.text = "Date: " + target.co_j + ". mediafileType: " + target.co_g
                }
                indexCounter = indexCounter + 1
            }

             */

        }



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SETUP_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val result = data?.getStringExtra("result")
                //newMessage = result ?: ""
                // Use newMessage as needed
            }
        }
    }
    companion object {
        private const val SETUP_ACTIVITY_REQUEST_CODE = 0 // Declaration of the request code
    }
    fun beginPhoto(){
        mediaVM.takePhoto(object : CommonCallbacks.CompletionCallback {
            override fun onSuccess() {
                textView2.text ="Photo success"
            }

            override fun onFailure(error: IDJIError) {
                textView2.text ="Photo Failed"

            }
        })
    }










}