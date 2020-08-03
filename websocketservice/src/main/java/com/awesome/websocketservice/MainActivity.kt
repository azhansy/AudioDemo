package com.awesome.websocketservice

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.net.InetAddress
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() , View.OnClickListener{

    private var mTestServer : TestServer? = null
    private lateinit var text_view:TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_view = findViewById(R.id.text_view)
    }

    override fun onClick(v: View) {
        when(v.id){
            R.id.btn_start ->{
                startSocket()
            }
            R.id.btn_close ->{
                stopSocket()
            }
        }
    }


    private fun startSocket(){
        stopSocket()
        mTestServer = TestServer(8888)
        mTestServer!!.start()
        text_view.text = "已开启"

    }

    private fun stopSocket() {
        mTestServer?.stop()
        mTestServer = null
        text_view.text = "已关闭"

    }

}
