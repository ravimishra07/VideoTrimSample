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
class MySeekbar : CrystalSeekbar {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun getCornerRadius(typedArray: TypedArray): Float {
        return super.getCornerRadius(typedArray)
    }

    override fun getMinValue(typedArray: TypedArray): Float {
        return 5f
    }

    override fun getMaxValue(typedArray: TypedArray): Float {
        return 90f
    }

    override fun getMinStartValue(typedArray: TypedArray): Float {
        return 20f
    }

    override fun getSteps(typedArray: TypedArray): Float {
        return super.getSteps(typedArray)
    }

    override fun getBarColor(typedArray: TypedArray): Int {
        return Color.parseColor("#A0E3F7")
    }

    override fun getBarHighlightColor(typedArray: TypedArray): Int {
        return Color.parseColor("#53C9ED")
    }

    override fun getThumbColor(typedArray: TypedArray): Int {
        return Color.parseColor("#058EB7")
    }

    override fun getThumbColorPressed(typedArray: TypedArray): Int {
        return Color.parseColor("#046887")
    }

    override fun getThumbDrawable(typedArray: TypedArray): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.thumb)
    }

    override fun getThumbDrawablePressed(typedArray: TypedArray): Drawable? {
        return ContextCompat.getDrawable(context, R.drawable.thumb_pressed)
    }

    override fun getDataType(typedArray: TypedArray): Int {
        return super.getDataType(typedArray)
    }
}