package com.ravi.videotrimsample.util

import android.provider.MediaStore
import android.annotation.SuppressLint
import android.os.Build
import android.provider.DocumentsContract
import android.os.Environment
import android.text.TextUtils
import android.content.ContentUris
import android.provider.OpenableColumns
import com.ravi.videotrimsample.util.LocaleHelper
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.annotation.TargetApi
import androidx.core.content.ContextCompat
import android.app.Activity
import android.media.MediaMetadataRetriever
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File
import java.lang.Exception

object Utils {
    fun formatCSeconds(timeInSeconds: Long): String {
        val hours = timeInSeconds / 3600
        val secondsLeft = timeInSeconds - hours * 3600
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60
        var formattedTime = ""
        if (hours < 10) formattedTime += "0"
        formattedTime += "$hours:"
        if (minutes < 10) formattedTime += "0"
        formattedTime += "$minutes:"
        if (seconds < 10) formattedTime += "0"
        formattedTime += seconds
        return formattedTime
    }

    fun getColor(context: Context?, color: Int): Int {
        return ContextCompat.getColor(context!!, color)
    }

    fun getDuration(context: Activity?, videoPath: Uri?): Long {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, videoPath)
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val timeInMillisec = time!!.toLong()
            retriever.release()
            return timeInMillisec / 1000
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun getFileExtension(context: Context, uri: Uri): String {
        try {
            val extension: String?
            extension = if (uri.scheme != null && uri.scheme == ContentResolver.SCHEME_CONTENT) {
                val mime = MimeTypeMap.getSingleton()
                mime.getExtensionFromMimeType(context.contentResolver.getType(uri))
            } else MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
            return if (extension == null || extension.isEmpty()) ".mp4" else extension
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "mp4"
    }

    fun getVideoRotation(context: Activity?, videoPath: Uri?): Int {
        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, videoPath)
            val rotation =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)!!
                    .toInt()
            retriever.release()
            return rotation
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun formatSeconds(timeInSeconds: Long): String {
        val hours = timeInSeconds / 3600
        val secondsLeft = timeInSeconds - hours * 3600
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60
        var formattedTime = ""
        if (hours < 10 && hours != 0L) {
            formattedTime += "0"
            formattedTime += "$hours:"
        }
        if (minutes < 10) formattedTime += "0"
        formattedTime += "$minutes:"
        if (seconds < 10) formattedTime += "0"
        formattedTime += seconds
        return formattedTime
    }

    fun getLimitedTimeFormatted(secs: Long): String {
        val hours = secs / 3600
        val secondsLeft = secs - hours * 3600
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60
        val time: String
        time = if (hours != 0L) {
            hours.toString() + " Hrs " + (if (minutes != 0L) "$minutes Mins " else "") +
                    if (seconds != 0L) "$seconds Secs " else ""
        } else if (minutes != 0L) minutes.toString() + " Mins " + (if (seconds != 0L) "$seconds Secs " else "") else "$seconds Secs "
        Log.v("Log time", time)
        return time
    }

    fun clearNull(value: String?): String {
        return value?.trim { it <= ' ' } ?: ""
    }
}