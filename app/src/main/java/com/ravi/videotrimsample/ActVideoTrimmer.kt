package com.ravi.videotrimsample

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.arthenica.mobileffmpeg.FFmpeg
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.gson.Gson
import com.ravi.videotrimsample.crystalrangeseekbar.LocaleHelper
import com.ravi.videotrimsample.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.ravi.videotrimsample.crystalrangeseekbar.widgets.CrystalSeekbar
import java.io.File
import java.util.*
import java.util.concurrent.Executors

class ActVideoTrimmer : LocalizationActivity() {
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
    private var lastMinValue: Long = 0
    private var lastMaxValue: Long = 0
    private var menuDone: MenuItem? = null
    private var seekbarController: CrystalSeekbar? = null
    private var isValidVideo = true
    private var isVideoEnded = false
    private var seekHandler: Handler? = null
    private var bundle: Bundle? = null
    private var progressBar: ProgressBar? = null
    private var trimVideoOptions: TrimVideoOptions? = null
    private var currentDuration: Long = 0
    private var lastClickedTime: Long = 0
    private var updateSeekbar: Runnable = object : Runnable {
        override fun run() {
            try {
                videoPlayer?.let { player->
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
    private var compressOption: CompressOption? = null
    private var outputPath: String? = null
    private var local: String? = null
    private var fixedGap: Long = 0
    private var minGap: Long = 0
    private var minFromGap: Long = 0
    private var maxToGap: Long = 0
    private var hidePlayerSeek = false
    private var isAccurateCut = false
    private var showFileLocationAlert = false
    private var progressView: CustomProgressView? = null
    private var fileName: String? = null
    private var btnDone: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_video_trimmer)
        btnDone = findViewById(R.id.btnDone)
        btnDone?.setOnClickListener { doneClicked() }
        bundle = intent.extras
        val gson = Gson()
        val videoOption = bundle!!.getString(TrimVideo.TRIM_VIDEO_OPTION)
        trimVideoOptions = gson.fromJson(videoOption, TrimVideoOptions::class.java)
        progressView = CustomProgressView(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"))
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        playerView = findViewById(R.id.player_view_lib)
        imagePlayPause = findViewById(R.id.image_play_pause)
        seekbar = findViewById(R.id.range_seek_bar)
        txtStartDuration = findViewById(R.id.txt_start_duration)
        txtEndDuration = findViewById(R.id.txt_end_duration)
        seekbarController = findViewById(R.id.seekbar_controller)
        ivClose = findViewById(R.id.ivClose)
        progressBar = findViewById(R.id.progress_circular)
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
        seekHandler = Handler()
        initPlayer()
        if (checkStoragePermission()) setDataInView()
    }

    private fun setUpToolBar(actionBar: ActionBar?, title: String?) {
        try {
            actionBar!!.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.title = title ?: "Edirt Video"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * SettingUp exoplayer
     */
    private fun initPlayer() {
        try {
            videoPlayer = SimpleExoPlayer.Builder(this).build()
            playerView!!.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            playerView!!.player = videoPlayer
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.CONTENT_TYPE_MOVIE)
                    .build()
                videoPlayer!!.setAudioAttributes(audioAttributes, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDataInView() {
        try {
            val fileUriRunnable = Runnable {
                uri = Uri.parse(bundle!!.getString(TrimVideo.TRIM_VIDEO_URI))
                //              String path = FileUtils.getPath(ActVideoTrimmer.this, uri);
                val path = FileUtils.getRealPath(this@ActVideoTrimmer, uri)
                uri = Uri.parse(path)
                runOnUiThread {
                    Log.v("VideoUri:: ", uri.toString())
                    progressBar?.visibility = View.GONE
                    totalDuration = TrimmerUtils.getDuration(this@ActVideoTrimmer, uri)
                    imagePlayPause!!.setOnClickListener { v: View? -> onVideoClicked() }
                    Objects.requireNonNull(playerView!!.videoSurfaceView)?.setOnClickListener { v: View? -> onVideoClicked() }
                    initTrimData()
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

    private fun initTrimData() {
        try {
            assert(trimVideoOptions != null)
          //  trimType = TrimmerUtils.getTrimType(trimVideoOptions!!.trimType)
            fileName = trimVideoOptions!!.fileName
            hidePlayerSeek = trimVideoOptions!!.hideSeekBar
            isAccurateCut = trimVideoOptions!!.accurateCut
            local = trimVideoOptions!!.local
            compressOption = trimVideoOptions!!.compressOption
            showFileLocationAlert = trimVideoOptions!!.showFileLocationAlert
            fixedGap = trimVideoOptions!!.fixedDuration
            fixedGap = if (fixedGap != 0L) fixedGap else totalDuration
            minGap = trimVideoOptions!!.minDuration
            minGap = if (minGap != 0L) minGap else totalDuration

                minFromGap = trimVideoOptions!!.minToMax[0]
                maxToGap = trimVideoOptions!!.minToMax[1]
                minFromGap = if (minFromGap != 0L) minFromGap else totalDuration
                maxToGap = if (maxToGap != 0L) maxToGap else totalDuration

        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        setLanguage(Locale(if (local != null) local else "en"))
    }

    private fun onVideoClicked() {
        try {
            if (isVideoEnded) {
                seekTo(lastMinValue)
                videoPlayer!!.playWhenReady = true
                return
            }
            if (currentDuration - lastMaxValue > 0) seekTo(lastMinValue)
            videoPlayer!!.playWhenReady = !videoPlayer!!.playWhenReady
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
            videoPlayer!!.addMediaSource(mediaSource)
            videoPlayer!!.prepare()
            videoPlayer!!.playWhenReady = true
            videoPlayer!!.addListener(object : Player.Listener {
                override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                    imagePlayPause!!.visibility = if (playWhenReady) View.GONE else View.VISIBLE
                }

                override fun onPlaybackStateChanged(state: Int) {
                    when (state) {
                        Player.STATE_ENDED -> {
                            //LogMessage.v("onPlayerStateChanged: Video ended.");
                            imagePlayPause!!.visibility = View.VISIBLE
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
                    .load(bundle!!.getString(TrimVideo.TRIM_VIDEO_URI))
                    .apply(options)
                    .transition(DrawableTransitionOptions.withCrossFade(300))
                    .into(img)
                if (sec < totalDuration) sec++
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setUpSeekBar() {
        seekbar?.visibility = View.VISIBLE
        txtStartDuration?.visibility = View.VISIBLE
        txtEndDuration?.visibility = View.VISIBLE
        seekbarController?.setMaxValue(totalDuration.toFloat())?.apply()
        seekbar?.setMaxValue(totalDuration.toFloat())?.apply()
        seekbar?.setMaxStartValue(totalDuration.toFloat())?.apply()

        seekbar?.setMaxStartValue(maxToGap.toFloat())
        seekbar?.setGap(minFromGap.toFloat())?.apply()
        lastMaxValue = maxToGap
     /*
        lastMaxValue = if (trimType == 1) {
            seekbar!!.setFixGap(fixedGap.toFloat()).apply()
            totalDuration
        } else if (trimType == 2) {
            seekbar!!.setMaxStartValue(minGap.toFloat())
            seekbar!!.setGap(minGap.toFloat()).apply()
            totalDuration
        } else if (trimType == 3) {
            seekbar!!.setMaxStartValue(maxToGap.toFloat())
            seekbar!!.setGap(minFromGap.toFloat()).apply()
            maxToGap
        } else {
            seekbar!!.setGap(2f).apply()
            totalDuration
        }
        */

        if (hidePlayerSeek) seekbarController!!.visibility = View.GONE
        seekbar!!.setOnRangeSeekbarFinalValueListener { minValue: Number?, maxValue: Number? ->
            if (!hidePlayerSeek) seekbarController!!.visibility = View.VISIBLE
        }
        seekbar!!.setOnRangeSeekbarChangeListener { minValue: Number, maxValue: Number ->
            val minVal = minValue as Long
            val maxVal = maxValue as Long
            if (lastMinValue != minVal) {
                seekTo(minValue)
                if (!hidePlayerSeek) seekbarController!!.visibility = View.INVISIBLE
            }
            lastMinValue = minVal
            lastMaxValue = maxVal
            txtStartDuration!!.text = TrimmerUtils.formatSeconds(minVal)
            txtEndDuration!!.text = TrimmerUtils.formatSeconds(maxVal)
           // if (trimType == 3) setDoneColor(minVal, maxVal)
        }
        seekbarController!!.setOnSeekbarFinalValueListener { value: Number ->
            val value1 = value as Long
            if (value1 in (lastMinValue + 1) until lastMaxValue) {
                seekTo(value1)
                return@setOnSeekbarFinalValueListener
            }
            if (value1 > lastMaxValue) seekbarController?.setMinStartValue(
                lastMaxValue.toFloat()
            )?.apply() else if (value1 < lastMinValue) {
                seekbarController?.setMinStartValue(lastMinValue.toFloat())?.apply()
                if (videoPlayer!!.playWhenReady) seekTo(lastMinValue)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PER_REQ_CODE) {
            if (isPermissionOk(*grantResults)) setDataInView() else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        videoPlayer!!.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (videoPlayer != null) videoPlayer!!.release()
        if (progressView != null && progressView!!.isShowing) progressView!!.dismiss()
        deleteFile("temp_file")
        stopRepeatingTask()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_done, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menuDone = menu.findItem(R.id.action_done)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_done) {
            //prevent multiple clicks
            if (SystemClock.elapsedRealtime() - lastClickedTime < 800) return true
            lastClickedTime = SystemClock.elapsedRealtime()
            trimVideo()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun doneClicked() {
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
            //            LogMessage.v("outputPath::" + outputPath + new File(outputPath).exists());
//            LogMessage.v("sourcePath::" + uri);
            videoPlayer!!.playWhenReady = false
            showProcessingDialog()
            val complexCommand: Array<String?>
            complexCommand = if (compressOption != null) compressionCmd else if (isAccurateCut) {
                //no changes in video quality
                //faster trimming command and given duration will be accurate
                accurateCmd
            } else {
                //no changes in video quality
                //fastest trimming command however, result duration
                //will be low accurate(2-3 secs)
                arrayOf(
                    "-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                    "-i", uri.toString(),
                    "-t",
                    TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue),
                    "-async", "1", "-strict", "-2", "-c", "copy", outputPath
                )
            }
            execFFmpegBinary(complexCommand, true)
        } else Toast.makeText(
            this,
            "Video should be smaller than" + " " + TrimmerUtils.getLimitedTimeFormatted(maxToGap),
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
                    fName + fileDateTime + "." + TrimmerUtils.getFileExtension(this, uri)
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
            var w = if (TrimmerUtils.clearNull(width).isEmpty()) 0 else width!!.toInt()
            var h = height!!.toInt()
            val rotation = TrimmerUtils.getVideoRotation(this, uri)
            if (rotation == 90 || rotation == 270) {
                val temp = w
                w = h
                h = temp
            }
            //Default compression option
            return if (compressOption!!.width != 0 || compressOption!!.height != 0 || compressOption!!.bitRate != "0k") {
                arrayOf(
                    "-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                    "-i", uri.toString(), "-s", compressOption!!.width.toString() + "x" +
                            compressOption!!.height,
                    "-r", compressOption!!.frameRate.toString(),
                    "-vcodec", "mpeg4", "-b:v",
                    compressOption!!.bitRate, "-b:a", "48000", "-ac", "2", "-ar",
                    "22050", "-t",
                    TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue), outputPath
                )
            } else if (w >= 800) {
                w = w / 2
                h = height.toInt() / 2
                arrayOf(
                    "-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                    "-i", uri.toString(),
                    "-s", w.toString() + "x" + h, "-r", "30",
                    "-vcodec", "mpeg4", "-b:v",
                    "1M", "-b:a", "48000", "-ac", "2", "-ar", "22050",
                    "-t",
                    TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue), outputPath
                )
            } else {
                arrayOf(
                    "-ss", TrimmerUtils.formatCSeconds(lastMinValue),
                    "-i", uri.toString(), "-s", w.toString() + "x" + h, "-r",
                    "30", "-vcodec", "mpeg4", "-b:v",
                    "400K", "-b:a", "48000", "-ac", "2", "-ar", "22050",
                    "-t",
                    TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue), outputPath
                )
            }
        }

    private fun execFFmpegBinary(command: Array<String?>, retry: Boolean) {
        try {
            Thread {
                val result = FFmpeg.execute(command)
                if (result == 0) {
                    dialog!!.dismiss()
                    if (showFileLocationAlert) showLocationAlert() else {
                        val intent = Intent()
                        intent.putExtra(TrimVideo.TRIMMED_VIDEO_PATH, outputPath)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                } else if (result == 255) {
                    // LogMessage.v("Command cancelled");
                    if (dialog!!.isShowing) dialog!!.dismiss()
                } else {
                    // Failed case:
                    // line 489 command fails on some devices in
                    // that case retrying with accurateCmt as alternative command
                    if (retry && !isAccurateCut && compressOption == null) {
                        val newFile = File(outputPath)
                        if (newFile.exists()) newFile.delete()
                        execFFmpegBinary(accurateCmd, false)
                    } else {
                        if (dialog!!.isShowing) dialog!!.dismiss()
                        runOnUiThread {
                            Toast.makeText(
                                this@ActVideoTrimmer,
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
        val openFileLocationDialog = AlertDialog.Builder(this@ActVideoTrimmer).create()
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
            intent.putExtra(TrimVideo.TRIMMED_VIDEO_PATH, outputPath)
            setResult(RESULT_OK, intent)
            finish()
        }
        openFileLocationDialog.show()
    }

    private val accurateCmd: Array<String?>
        private get() = arrayOf(
            "-ss", TrimmerUtils.formatCSeconds(lastMinValue), "-i", uri.toString(), "-t",
            TrimmerUtils.formatCSeconds(lastMaxValue - lastMinValue),
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
            PER_REQ_CODE
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

    companion object {
        private const val PER_REQ_CODE = 115
    }
}