package com.ravi.videotrimsample.activity

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.*
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arthenica.mobileffmpeg.FFmpeg
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.ravi.videotrimsample.R
import com.ravi.videotrimsample.customseekar.CrystalRangeSeekbar
import com.ravi.videotrimsample.customseekar.CrystalSeekbar
import com.ravi.videotrimsample.interfaces.OnRangeSeekbarChangeListener
import com.ravi.videotrimsample.interfaces.OnRangeSeekbarFinalValueListener
import com.ravi.videotrimsample.interfaces.OnSeekbarFinalValueListener
import com.ravi.videotrimsample.util.Constants
import com.ravi.videotrimsample.util.FileUtils
import com.ravi.videotrimsample.util.Utils
import kotlinx.coroutines.selects.select
import java.io.File
import java.util.*
import java.util.concurrent.Executors

class VideoTrimActivity : AppCompatActivity() {
    companion object {
        const val MAX_DURATION = 5L
    }

    private var playerView: StyledPlayerView? = null
    private var videoPlayer: ExoPlayer? = null
    private var imagePlayPause: ImageView? = null
    private var ivClose: ImageView? = null
    private lateinit var imageViews: Array<ImageView>
    private var totalDuration: Long = 0
    private var dialog: Dialog? = null
    private var uri: Uri? = null
    private var txtStartDuration: TextView? = null
    private var txtEndDuration: TextView? = null
    private var seekbar: CrystalRangeSeekbar? = null
    private var seekbarController: CrystalSeekbar? = null
    private var lastMinValue: Long = 0
    private var lastMaxValue: Long = 0
    private var menuDone: MenuItem? = null
    private var isValidVideo = true
    private var isVideoEnded = false
    private var seekHandler: Handler? = null
    private var bundle: Bundle? = null
    private var progressBar: ProgressBar? = null
    private var currentDuration: Long = 0
    private var lastClickedTime: Long = 0
    private var updateSeekbar: Runnable = object : Runnable {
        override fun run() {
            try {
                videoPlayer?.let { player ->
                    currentDuration = player.currentPosition / 1000
                    if (!player.playWhenReady) return
                    if (currentDuration <= lastMaxValue) seekbarController?.setMinStartValue(
                        currentDuration.toFloat()
                    )?.apply() else player.playWhenReady = false
                }
            } finally {
                seekHandler?.postDelayed(this, 1000)
            }
        }
    }
    private var outputPath: String? = null
    private var minFromGap: Long = 0
    private var maxToGap: Long = 60
    private var hidePlayerSeek = false
    private var isAccurateCut = false
    private var showFileLocationAlert = false

    //private var progressView: ProgressBar? = null
    private var fileName: String? = null
    private var btnDone: TextView? = null
    private var isInitialRangeSet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_video_trimmer)
        bundle = intent.extras
        initViews()
    }

    private fun initViews() {
        playerView = findViewById(R.id.player_view_lib)
        imagePlayPause = findViewById(R.id.image_play_pause)
        seekbar = findViewById(R.id.range_seek_bar)
        txtStartDuration = findViewById(R.id.txt_start_duration)
        txtEndDuration = findViewById(R.id.txt_end_duration)
        seekbarController = findViewById(R.id.seekbar_controller)
        ivClose = findViewById(R.id.ivClose)
        progressBar = findViewById(R.id.progress_circular)
        btnDone = findViewById(R.id.btnDone)
        btnDone?.setOnClickListener { doneClicked() }
        ivClose?.setOnClickListener{
            finish()
        }
        val imageOne = findViewById<ImageView>(R.id.image_one)
        val imageTwo = findViewById<ImageView>(R.id.image_two)
        val imageThree = findViewById<ImageView>(R.id.image_three)
        val imageFour = findViewById<ImageView>(R.id.image_four)
        val imageFive = findViewById<ImageView>(R.id.image_five)
        val imageSix = findViewById<ImageView>(R.id.image_six)
        val imageSeven = findViewById<ImageView>(R.id.image_seven)
        val imageEight = findViewById<ImageView>(R.id.image_eight)
        imageViews = arrayOf(
            imageOne, imageTwo, imageThree,
            imageFour, imageFive, imageSix, imageSeven, imageEight
        )
        seekHandler = Handler(Looper.getMainLooper())
        initPlayer()
        // progressView = ProgressBar(this)
        if (checkStoragePermission()) setDataInView()
    }

    /**
     * SettingUp exoplayer
     */
    private fun initPlayer() {
        try {
            videoPlayer = ExoPlayer.Builder(this).build()
            playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            playerView?.player = videoPlayer
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build()
            videoPlayer?.setAudioAttributes(audioAttributes, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDataInView() {
        try {
            val fileUriRunnable = Runnable {
                uri = Uri.parse(bundle!!.getString(Constants.TRIM_VIDEO_URI))
                val path = FileUtils.getRealPath(
                    this@VideoTrimActivity,
                    Uri.parse(bundle!!.getString(Constants.TRIM_VIDEO_URI))
                )
                uri = Uri.parse(path)
                runOnUiThread {
                    Log.v("VideoUri:: ", uri.toString())
                    progressBar?.visibility = View.GONE
                    totalDuration = Utils.getDuration(this@VideoTrimActivity, uri)
                    imagePlayPause?.setOnClickListener { _: View? -> onVideoClicked() }
                    playerView?.videoSurfaceView?.setOnClickListener {
                        onVideoClicked()
                    }
                    buildMediaSource(uri)
                    loadThumbnails()
                    setUpSeekBar()
                }
            }
            Executors.newSingleThreadExecutor().execute(fileUriRunnable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onVideoClicked() {
        try {
            if (isVideoEnded) {
                seekTo(lastMinValue)
                videoPlayer?.playWhenReady = true
                return
            }
            if (currentDuration - lastMaxValue > 0) seekTo(lastMinValue)
            videoPlayer?.playWhenReady = !videoPlayer?.playWhenReady!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun seekTo(sec: Long) {
        if (videoPlayer != null) videoPlayer!!.seekTo(sec * 1000)
    }

    private fun buildMediaSource(mUri: Uri?) {
        try {
            val dataSourceFactory: DataSource.Factory =
                DefaultDataSourceFactory(this, getString(R.string.app_name))
            val mediaSource: MediaSource =
                ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                    MediaItem.fromUri(
                        mUri!!
                    )
                )
            videoPlayer?.apply {
                addMediaSource(mediaSource)
                prepare()
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                        imagePlayPause?.visibility = if (playWhenReady) View.GONE else View.VISIBLE
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        when (state) {
                            Player.STATE_ENDED -> {
                                imagePlayPause?.visibility = View.VISIBLE
                                isVideoEnded = true
                            }
                            Player.STATE_READY -> {
                                isVideoEnded = false
                                startProgress()
                            }
                            Player.STATE_BUFFERING -> {
                            }
                            Player.STATE_IDLE -> {
                            }
                            else -> {
                            }
                        }
                    }
                })
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /*
     *  loading thumbnails
     * */
    private fun loadThumbnails() {
        try {
            val diff = totalDuration / 8
            var sec = 1
            for (img in imageViews) {
                val interval = diff * sec * 1000000
                val options = RequestOptions().frame(interval)
                Glide.with(this)
                    .load(bundle!!.getString(Constants.TRIM_VIDEO_URI))
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(img)
                if (sec < totalDuration) {
                    sec++
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpSeekBar() {
        seekbar?.visibility = View.VISIBLE
        txtStartDuration?.visibility = View.VISIBLE
        txtEndDuration?.visibility = View.VISIBLE
        lastMaxValue = maxToGap
        seekbarController?.setMaxValue(totalDuration.toFloat())?.apply()
        seekbar?.apply {
            seekbarController?.setMaxValue(totalDuration.toFloat())?.apply()
             setMaxValue(totalDuration.toFloat())?.apply()
             setMaxStartValue(12F)?.apply()
            if (totalDuration.toFloat() > MAX_DURATION) {
                seekbar?.setMaxStartValue(MAX_DURATION.toFloat())?.apply()
                val mGap = MAX_DURATION*100 / totalDuration.toFloat()
                setFixGap(mGap)
            } else {
                setMaxStartValue(totalDuration.toFloat())?.apply()
            }
        }


        if (hidePlayerSeek) seekbarController?.visibility = View.GONE

        seekbar?.setOnRangeSeekbarFinalValueListener(object : OnRangeSeekbarFinalValueListener {
            override fun finalValue(minValue: Number?, maxValue: Number?) {
                if (!hidePlayerSeek) {
                    seekbarController?.visibility = View.VISIBLE
                }
            }
        })
        seekbar?.setOnRangeSeekbarChangeListener(object : OnRangeSeekbarChangeListener {
            override fun valueChanged(minValue: Number?, maxValue: Number?) {

                setRange(minValue as Long, maxValue as Long)

//                if (isInitialRangeSet) {
//                    setRange(minValue as Long, maxValue as Long)
//                }else{
//                    isInitialRangeSet = true
//                }
//                else {
//                    lastMinValue = minValue?.toLong() ?:0L
//
//                    if (totalDuration.toFloat() > MAX_DURATION) {
//                        seekTo(MAX_DURATION)
//                        lastMaxValue = MAX_DURATION//maxValue?.toLong() ?:0L
//                    } else {
//                        seekTo(totalDuration)
//                        lastMaxValue = totalDuration
//                    }
//                    txtStartDuration?.text = Utils.formatSeconds(minValue?.toLong() ?: 0L)
//                    txtEndDuration?.text = Utils.formatSeconds(totalDuration)
//                }
            }
        })
        seekbarController?.setOnSeekbarFinalValueListener(object : OnSeekbarFinalValueListener {
            override fun finalValue(value: Number?) {
                val value1 = value as Long
                if (value1 in (lastMinValue + 1) until lastMaxValue) {
                    seekTo(value1)
                    return
                }
                if (value1 > lastMaxValue) seekbarController?.setMinStartValue(
                    lastMaxValue.toFloat()
                )?.apply() else if (value1 < lastMinValue) {
                    seekbarController?.setMinStartValue(lastMinValue.toFloat())?.apply()
                    if (videoPlayer?.playWhenReady == true) seekTo(lastMinValue)
                }
            }
        })
    }

    private fun setRange(minVal: Long, maxVal: Long) {
        //  if (lastMinValue != minVal) {
        seekTo(minVal)
        if (!hidePlayerSeek) seekbarController?.visibility = View.INVISIBLE
        // }
        lastMinValue = minVal
        lastMaxValue = maxVal
        txtStartDuration?.text = Utils.formatSeconds(minVal)
        txtEndDuration?.text = Utils.formatSeconds(maxVal)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 115) {
            if (isPermissionOk(*grantResults)) setDataInView() else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        videoPlayer?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (videoPlayer != null) videoPlayer!!.release()
        // if (progressView != null && progressView?.isVisible) progressView!!.dismiss()
        deleteFile("temp_file")
        stopRepeatingTask()
    }

    private fun doneClicked() {
        //prevent multiple clicks
        if (SystemClock.elapsedRealtime() - lastClickedTime < 800) {
            return
        }
        lastClickedTime = SystemClock.elapsedRealtime()
        trimVideo()
    }

    private fun trimVideo() {
        if (isValidVideo) {
            //not exceed given maxDuration if has given
            outputPath = getFileName()

            videoPlayer!!.playWhenReady = false
            showProcessingDialog()
            val complexCommand: Array<String?> = arrayOf(
                "-ss", Utils.formatCSeconds(lastMinValue),
                "-i", uri.toString(),
                "-t",
                Utils.formatCSeconds(lastMaxValue - lastMinValue),
                "-async", "1", "-strict", "-2", "-c", "copy", outputPath
            )

            execFFmpegBinary(complexCommand, true)
        } else Toast.makeText(
            this,
            "Video should be smaller than" + " " + Utils.getLimitedTimeFormatted(maxToGap),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getFileName(): String {
        val path = getExternalFilesDir("TrimmedVideo")!!.path
        val calender = Calendar.getInstance()
        val fileDateTime = calender[Calendar.YEAR].toString() + "_" +
                calender[Calendar.MONTH] + "_" +
                calender[Calendar.DAY_OF_MONTH] + "_" +
                calender[Calendar.HOUR_OF_DAY] + "_" +
                calender[Calendar.MINUTE] + "_" +
                calender[Calendar.SECOND]
        var fName = "trimmed_video_"
        if (fileName != null && !fileName!!.isEmpty()) fName = fileName as String
        val newFile = File(
            path + File.separator +
                    fName + fileDateTime + "." + uri?.let { Utils.getFileExtension(this, it) }
        )
        return newFile.toString()
    }

    //Default compression option
    private val compressionCmd: Array<String?>
        private get() {
            val metaRetriever = MediaMetadataRetriever()
            metaRetriever.setDataSource(uri.toString())
            val height =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
            val width =
                metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
            var w = if (Utils.clearNull(width).isEmpty()) 0 else width!!.toInt()
            var h = height!!.toInt()
            val rotation = Utils.getVideoRotation(this, uri)
            if (rotation == 90 || rotation == 270) {
                val temp = w
                w = h
                h = temp
            }
            //Default compression option
            return arrayOf(
                "-ss", Utils.formatCSeconds(lastMinValue),
                "-i", uri.toString(), "-s", w.toString() + "x" + h, "-r",
                "30", "-vcodec", "mpeg4", "-b:v",
                "400K", "-b:a", "48000", "-ac", "2", "-ar", "22050",
                "-t",
                Utils.formatCSeconds(lastMaxValue - lastMinValue), outputPath
            )
        }

    private fun execFFmpegBinary(command: Array<String?>, retry: Boolean) {
        try {
            Thread {
                val result = FFmpeg.execute(command)
                if (result == 0) {
                    dialog!!.dismiss()
                    if (showFileLocationAlert) showLocationAlert() else {
                        val intent = Intent()
                        intent.putExtra(Constants.TRIMMED_VIDEO_PATH, outputPath)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                } else if (result == 255) {
                    if (dialog!!.isShowing) dialog!!.dismiss()
                } else {

                    if (retry && !isAccurateCut) {
                        val newFile = File(outputPath)
                        if (newFile.exists()) newFile.delete()
                        execFFmpegBinary(accurateCmd, false)
                    } else {
                        if (dialog!!.isShowing) dialog!!.dismiss()
                        runOnUiThread {
                            Toast.makeText(
                                this@VideoTrimActivity,
                                "Failed to trim",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showLocationAlert() {
        // dialog to ask user to open file location in file manager or not
        val openFileLocationDialog = AlertDialog.Builder(this@VideoTrimActivity).create()
        openFileLocationDialog.setTitle("location alert")
        openFileLocationDialog.setCancelable(true)

        // when user click yes
        openFileLocationDialog.setButton(
            DialogInterface.BUTTON_POSITIVE,
            getString(R.string.yes)
        ) { dialogInterface: DialogInterface?, i: Int ->
            // open file location
            val chooser = Intent(Intent.ACTION_GET_CONTENT)
            val uriFile = Uri.parse(outputPath)
            chooser.addCategory(Intent.CATEGORY_OPENABLE)
            chooser.setDataAndType(uriFile, "*/*")
            startActivity(chooser)
        }

        // when user click no and finish current activity
        openFileLocationDialog.setButton(
            DialogInterface.BUTTON_NEGATIVE,
            getString(R.string.no)
        ) { dialogInterface: DialogInterface?, i: Int -> openFileLocationDialog.dismiss() }

        // when user click no and finish current activity
        openFileLocationDialog.setOnDismissListener { dialogInterface: DialogInterface? ->
            val intent = Intent()
            intent.putExtra(Constants.TRIMMED_VIDEO_PATH, outputPath)
            setResult(RESULT_OK, intent)
            finish()
        }
        openFileLocationDialog.show()
    }

    private val accurateCmd: Array<String?>
        private get() = arrayOf(
            "-ss", Utils.formatCSeconds(lastMinValue), "-i", uri.toString(), "-t",
            Utils.formatCSeconds(lastMaxValue - lastMinValue),
            "-async", "1", outputPath
        )

    private fun showProcessingDialog() {
        try {
            dialog = Dialog(this)
            dialog!!.setCancelable(false)
            dialog!!.setContentView(R.layout.alert_convert)
            val txtCancel = dialog!!.findViewById<TextView>(R.id.txt_cancel)
            dialog!!.setCancelable(false)
            dialog!!.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            txtCancel.setOnClickListener { v: View? ->
                dialog!!.dismiss()
                FFmpeg.cancel()
            }
            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            checkPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )
        } else checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
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
            115
        )
        return false
    }

    private fun isPermissionOk(vararg results: Int): Boolean {
        var isAllGranted = true
        for (result in results) {
            if (PackageManager.PERMISSION_GRANTED != result) {
                isAllGranted = false
                break
            }
        }
        return isAllGranted
    }

    fun startProgress() {
        updateSeekbar.run()
    }

    fun stopRepeatingTask() {
        seekHandler!!.removeCallbacks(updateSeekbar)
    }


}