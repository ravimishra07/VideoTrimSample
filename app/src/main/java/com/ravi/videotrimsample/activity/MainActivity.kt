package com.ravi.videotrimsample.activity

import android.os.Bundle
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.ravi.videotrimsample.R

class MainActivity : AppCompatActivity() {
    lateinit var selectVideo: TextView
    private val videoView: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        selectVideo = findViewById(R.id.tvSelect)

    }
}