package com.ravi.videotrimsample.customseekar

import com.ravi.videotrimsample.R
import android.content.Context
import androidx.core.content.ContextCompat
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet

/**
 * Created by owais.ali on 7/12/2016.
 */
class MyRangeSeekbar : CrystalRangeSeekbar {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun getMinValue(typedArray: TypedArray): Float {
        return 5f
    }

    override fun getMaxValue(typedArray: TypedArray): Float {
        return 90f
    }

    override fun getMinStartValue(typedArray: TypedArray): Float {
        return 20f
    }

    override fun getMaxStartValue(typedArray: TypedArray): Float {
        return 50f
    }

    override fun getBarColor(typedArray: TypedArray): Int {
        return Color.parseColor("#A0E3F7")
    }

    override fun getBarHighlightColor(typedArray: TypedArray): Int {
        return Color.parseColor("#53C9ED")
    }

    override fun getLeftThumbColor(typedArray: TypedArray): Int {
        return Color.parseColor("#058EB7")
    }

    override fun getRightThumbColor(typedArray: TypedArray): Int {
        return Color.parseColor("#058EB7")
    }

    override fun getLeftThumbColorPressed(typedArray: TypedArray): Int {
        return Color.parseColor("#046887")
    }

    override fun getRightThumbColorPressed(typedArray: TypedArray): Int {
        return Color.parseColor("#046887")
    }

    override fun getLeftDrawable(typedArray: TypedArray): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.thumb)
    }

    override fun getRightDrawable(typedArray: TypedArray): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.thumb)
    }

    override fun getLeftDrawablePressed(typedArray: TypedArray): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.thumb_pressed)
    }

    override fun getRightDrawablePressed(typedArray: TypedArray): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.thumb_pressed)
    }

}