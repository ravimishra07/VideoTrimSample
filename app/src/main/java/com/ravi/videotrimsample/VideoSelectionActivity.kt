package com.ravi.videotrimsample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class VideoSelectionActivity : AppCompatActivity()  {
    lateinit var selectVideo: TextView
    private var videoView: VideoView? = null
    private var mediaController: MediaController? = null
    companion object{
        private val TAG = "VideoSelectionActivity"

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_selection)
        selectVideo = findViewById(R.id.tvSelect)
        mediaController = MediaController(this)
        videoView = findViewById(R.id.video_view)


        selectVideo.setOnClickListener{
            if (checkCamStoragePer())
                openVideo()
        }

    }
    fun openVideo() {
        try {
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            takeOrSelectVideoResultLauncher.launch(Intent.createChooser(intent, "Select Video"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun checkPermission(vararg permissions: String): Boolean {
        var allPermitted = false
        for (permission in permissions) {
            allPermitted = (ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED)
            if (!allPermitted) break
        }
        if (allPermitted) return true
        ActivityCompat.requestPermissions(
            this, permissions,
            220
        )
        return false
    }

    private fun checkCamStoragePer(): Boolean {
        return checkPermission(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }

    var takeOrSelectVideoResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK &&
            result.data != null
        ) {
            val data = result.data
            //check video duration if needed
            /*        if (TrimmerUtils.getDuration(this,data.getData())<=30){
                           Toast.makeText(this,"Video should be larger than 30 sec",Toast.LENGTH_SHORT).show();
                           return;
                       }*/if (data!!.data != null) {
                Log.v(TAG , data.data.toString())
                openTrimActivity(data.data.toString())
            } else {
                Toast.makeText(this, "video uri is null", Toast.LENGTH_SHORT).show()
            }
        } else Log.v(TAG,"takeVideoResultLauncher data is null")
    }
    var videoTrimResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK &&
            result.data != null
        ) {
            val uri =
                Uri.parse(TrimVideo.getTrimmedVideoPath(result.data))
            Log.d(TAG, "Trimmed path:: $uri")
            videoView?.setMediaController(mediaController)
            videoView?.setVideoURI(uri)
            videoView?.requestFocus()
            videoView?.start()
            videoView?.setOnPreparedListener { mediaPlayer: MediaPlayer? ->
                mediaController?.setAnchorView(
                    videoView
                )
            }
            val filepath = uri.toString()
            val file = File(filepath)
            val length = file.length()
             Log.d(TAG, "Video size:: " + length / 1024)
        } else {
            //   LogMessage.v("videoTrimResultLauncher data is null")
        }
    }

    private fun openTrimActivity(data: String) {
        TrimVideo.activity(data)
            .setCompressOption(CompressOption()) //pass empty constructor for default compress option
            .start(this, videoTrimResultLauncher)

    }

}