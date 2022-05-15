package com.ravi.videotrimsample.customseekar

import android.content.Context
import kotlin.jvm.JvmOverloads
import com.ravi.videotrimsample.interfaces.OnRangeSeekbarChangeListener
import com.ravi.videotrimsample.interfaces.OnRangeSeekbarFinalValueListener
import com.ravi.videotrimsample.customseekar.CrystalRangeSeekbar
import androidx.core.content.ContextCompat
import com.ravi.videotrimsample.R
import android.graphics.drawable.BitmapDrawable
import android.content.res.TypedArray
import android.graphics.*
import com.ravi.videotrimsample.customseekar.CrystalSeekbar
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.MeasureSpec
import java.lang.Exception
import java.lang.IllegalArgumentException
import kotlin.Throws

/**
 * Created by owais.ali on 6/20/2016.
 */
open class CrystalRangeSeekbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //private static final int DEFAULT_THUMB_WIDTH = 80;
    //private static final int DEFAULT_THUMB_HEIGHT = 80;
    private val NO_STEP = -1f
    private val NO_FIXED_GAP = -1f
    var mGapValue = 0.0F

    //////////////////////////////////////////
    // PUBLIC CONSTANTS CLASS
    //////////////////////////////////////////
    object DataType {
        const val LONG = 0
        const val DOUBLE = 1
        const val INTEGER = 2
        const val FLOAT = 3
        const val SHORT = 4
        const val BYTE = 5
    }

    object ColorMode {
        const val SOLID = 0
        const val GRADIENT = 1
    }

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////
    private var onRangeSeekbarChangeListener: OnRangeSeekbarChangeListener? = null
    private var onRangeSeekbarFinalValueListener: OnRangeSeekbarFinalValueListener? = null
    private var absoluteMinValue = 0f
    private var absoluteMaxValue = 0f
    private var absoluteMinStartValue = 0f
    private var absoluteMaxStartValue = 0f
    private var minValue = 0f
    private var maxValue = 0f
    private var minStartValue = 0f
    private var maxStartValue = 0f
    private var steps = 0f
    private var gap = 0f
    private var fixGap = 0f
    private var mActivePointerId = INVALID_POINTER_ID
    private var dataType = 0
    private var cornerRadius = 0f
    private var barColorMode = 0
    private var barColor = 0
    private var barGradientStart = 0
    private var barGradientEnd = 0
    private var barHighlightColorMode = 0
    private var barHighlightColor = 0
    private var barHighlightGradientStart = 0
    private var barHighlightGradientEnd = 0
    private var leftThumbColor = 0
    private var rightThumbColor = 0
    private var leftThumbColorNormal = 0
    private var leftThumbColorPressed = 0
    private var rightThumbColorNormal = 0
    private var rightThumbColorPressed = 0
    private var seekBarTouchEnabled = false
    private var barPadding = 0f
    private var barHeight = 0f
    private var _barHeight = 0f
    private var thumbWidth = 0f
    private var thumbDiameter = 0f

    //private float thumbHalfWidth;
    //private float thumbHalfHeight;
    private var thumbHeight = 0f
    private var leftDrawable: Drawable? = null
    private var rightDrawable: Drawable? = null
    private var leftDrawablePressed: Drawable? = null
    private var rightDrawablePressed: Drawable? = null
    private var leftThumb: Bitmap? = null
    private var leftThumbPressed: Bitmap? = null
    private var rightThumb: Bitmap? = null
    private var rightThumbPressed: Bitmap? = null

    //////////////////////////////////////////
    // PROTECTED METHODS
    //////////////////////////////////////////
    protected var pressedThumb: Thumb? = null
        private set
    private var normalizedMinValue = 0.0
    private var normalizedMaxValue = 100.0
    private var pointerIndex = 0
    private var _rect: RectF? = null
    private var _paint: Paint? = null
    protected var leftThumbRect: RectF? = null
        private set
    protected var rightThumbRect: RectF? = null
        private set
    private var mIsDragging = false

    //////////////////////////////////////////
    // ENUMERATION
    //////////////////////////////////////////
    protected enum class Thumb {
        MIN, MAX
    }

    //////////////////////////////////////////
    // INITIALIZING
    //////////////////////////////////////////
    protected fun init() {
        absoluteMinValue = minValue
        absoluteMaxValue = maxValue
        leftThumbColor = leftThumbColorNormal
        rightThumbColor = rightThumbColorNormal
        leftThumb = getBitmap(leftDrawable)
        rightThumb = getBitmap(rightDrawable)
        leftThumbPressed = getBitmap(leftDrawablePressed)
        rightThumbPressed = getBitmap(rightDrawablePressed)
        leftThumbPressed = if (leftThumbPressed == null) leftThumb else leftThumbPressed
        rightThumbPressed = if (rightThumbPressed == null) rightThumb else rightThumbPressed
        gap = Math.max(0f, Math.min(gap, absoluteMaxValue - absoluteMinValue))
        gap = gap / (absoluteMaxValue - absoluteMinValue) * 100
        if (fixGap != NO_FIXED_GAP) {
            fixGap = Math.min(fixGap, absoluteMaxValue)
            fixGap = fixGap / (absoluteMaxValue - absoluteMinValue) * 100
            addFixGap(true)
        }

        thumbWidth = getThumbWidth()
        thumbHeight = getThumbHeight()

        //thumbHalfWidth = thumbWidth / 2;
        //thumbHalfHeight = thumbHeight / 2;
        barHeight = getBarHeight()
        barPadding = getBarPadding()
        _paint = Paint(Paint.ANTI_ALIAS_FLAG)
        _rect = RectF()
        leftThumbRect = RectF()
        rightThumbRect = RectF()
        pressedThumb = null
        setMinStartValue()
        setMaxStartValue()
        setWillNotDraw(false)
    }

    //////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////
    fun setCornerRadius(cornerRadius: Float): CrystalRangeSeekbar {
        this.cornerRadius = cornerRadius
        return this
    }

    fun setMinValue(minValue: Float): CrystalRangeSeekbar {
        this.minValue = minValue
        absoluteMinValue = minValue
        return this
    }

    fun setMaxValue(maxValue: Float): CrystalRangeSeekbar {
        this.maxValue = maxValue
        absoluteMaxValue = maxValue
        return this
    }

    fun setMinStartValue(minStartValue: Float): CrystalRangeSeekbar {
        this.minStartValue = minStartValue
        absoluteMinStartValue = minStartValue
        return this
    }

    fun setMaxStartValue(maxStartValue: Float): CrystalRangeSeekbar {
        this.maxStartValue = maxStartValue
        absoluteMaxStartValue = maxStartValue
        return this
    }

    fun setSteps(steps: Float): CrystalRangeSeekbar {
        this.steps = steps
        return this
    }

    fun setGap(gap: Float): CrystalRangeSeekbar {
        this.gap = gap
        return this
    }

    fun setFixGap(fixGap: Float): CrystalRangeSeekbar {
        this.fixGap = fixGap
        return this
    }

    fun setBarHeight(height: Float): CrystalRangeSeekbar {
        _barHeight = height
        return this
    }

    fun setBarColorMode(barColorMode: Int): CrystalRangeSeekbar {
        this.barColorMode = barColorMode
        return this
    }

    fun setBarColor(barColor: Int): CrystalRangeSeekbar {
        this.barColor = barColor
        return this
    }

    fun setBarGradientStart(barGradientStart: Int): CrystalRangeSeekbar {
        this.barGradientStart = barGradientStart
        return this
    }

    fun setBarGradientEnd(barGradientEnd: Int): CrystalRangeSeekbar {
        this.barGradientEnd = barGradientEnd
        return this
    }

    fun setBarHighlightColorMode(barHighlightColorMode: Int): CrystalRangeSeekbar {
        this.barHighlightColorMode = barHighlightColorMode
        return this
    }

    fun setBarHighlightColor(barHighlightColor: Int): CrystalRangeSeekbar {
        this.barHighlightColor = barHighlightColor
        return this
    }

    fun setBarHighlightGradientStart(barHighlightGradientStart: Int): CrystalRangeSeekbar {
        this.barHighlightGradientStart = barHighlightGradientStart
        return this
    }

    fun setBarHighlightGradientEnd(barHighlightGradientEnd: Int): CrystalRangeSeekbar {
        this.barHighlightGradientEnd = barHighlightGradientEnd
        return this
    }

    fun setLeftThumbColor(leftThumbColorNormal: Int): CrystalRangeSeekbar {
        this.leftThumbColorNormal = leftThumbColorNormal
        return this
    }

    fun setLeftThumbHighlightColor(leftThumbColorPressed: Int): CrystalRangeSeekbar {
        this.leftThumbColorPressed = leftThumbColorPressed
        return this
    }

    fun setLeftThumbDrawable(resId: Int): CrystalRangeSeekbar {
        setLeftThumbDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setLeftThumbDrawable(drawable: Drawable?): CrystalRangeSeekbar {
        setLeftThumbBitmap(getBitmap(drawable))
        return this
    }

    fun setLeftThumbBitmap(bitmap: Bitmap?): CrystalRangeSeekbar {
        leftThumb = bitmap
        return this
    }

    fun setLeftThumbHighlightDrawable(resId: Int): CrystalRangeSeekbar {
        setLeftThumbHighlightDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setLeftThumbHighlightDrawable(drawable: Drawable?): CrystalRangeSeekbar {
        setLeftThumbHighlightBitmap(getBitmap(drawable))
        return this
    }

    fun setLeftThumbHighlightBitmap(bitmap: Bitmap?): CrystalRangeSeekbar {
        leftThumbPressed = bitmap
        return this
    }

    fun setRightThumbColor(rightThumbColorNormal: Int): CrystalRangeSeekbar {
        this.rightThumbColorNormal = rightThumbColorNormal
        return this
    }

    fun setRightThumbHighlightColor(rightThumbColorPressed: Int): CrystalRangeSeekbar {
        this.rightThumbColorPressed = rightThumbColorPressed
        return this
    }

    fun setRightThumbDrawable(resId: Int): CrystalRangeSeekbar {
        setRightThumbDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setRightThumbDrawable(drawable: Drawable?): CrystalRangeSeekbar {
        setRightThumbBitmap(getBitmap(drawable))
        return this
    }

    fun setRightThumbBitmap(bitmap: Bitmap?): CrystalRangeSeekbar {
        rightThumb = bitmap
        return this
    }

    fun setRightThumbHighlightDrawable(resId: Int): CrystalRangeSeekbar {
        setRightThumbHighlightDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setRightThumbHighlightDrawable(drawable: Drawable?): CrystalRangeSeekbar {
        setRightThumbHighlightBitmap(getBitmap(drawable))
        return this
    }

    fun setRightThumbHighlightBitmap(bitmap: Bitmap?): CrystalRangeSeekbar {
        rightThumbPressed = bitmap
        return this
    }

    fun setDataType(dataType: Int): CrystalRangeSeekbar {
        this.dataType = dataType
        return this
    }

    fun setOnRangeSeekbarChangeListener(onRangeSeekbarChangeListener: OnRangeSeekbarChangeListener?) {
        this.onRangeSeekbarChangeListener = onRangeSeekbarChangeListener
        if (this.onRangeSeekbarChangeListener != null) {
            this.onRangeSeekbarChangeListener!!.valueChanged(selectedMinValue, selectedMaxValue)
        }
    }

    fun setOnRangeSeekbarFinalValueListener(onRangeSeekbarFinalValueListener: OnRangeSeekbarFinalValueListener?) {
        this.onRangeSeekbarFinalValueListener = onRangeSeekbarFinalValueListener
    }

    val selectedMinValue: Number
        get() {
            var nv = normalizedMinValue
            if (steps > 0 && steps <= Math.abs(absoluteMaxValue) / 2) {
                val stp = steps / (absoluteMaxValue - absoluteMinValue) * 100
                val half_step = (stp / 2).toDouble()
                val mod = nv % stp
                if (mod > half_step) {
                    nv = nv - mod
                    nv = nv + stp
                } else {
                    nv = nv - mod
                }
            } else {
                check(steps == NO_STEP) { "steps out of range $steps" }
            }
            return formatValue(normalizedToValue(nv))
        }
    val selectedMaxValue: Number
        get() {
            var nv = normalizedMaxValue
            if (steps > 0 && steps <= Math.abs(absoluteMaxValue) / 2) {
                val stp = steps / (absoluteMaxValue - absoluteMinValue) * 100
                val half_step = (stp / 2).toDouble()
                val mod = nv % stp
                if (mod > half_step) {
                    nv = nv - mod
                    nv = nv + stp
                } else {
                    nv = nv - mod
                }
            } else {
                check(steps == NO_STEP) { "steps out of range $steps" }
            }
            return formatValue(normalizedToValue(nv))
        }

    fun apply() {

        // reset normalize min and max value
        normalizedMinValue = 0.0
        normalizedMaxValue = 100.0
        gap = Math.max(0f, Math.min(gap, absoluteMaxValue - absoluteMinValue))
        gap = gap / (absoluteMaxValue - absoluteMinValue) * 100
        if (fixGap != NO_FIXED_GAP) {
            fixGap = Math.min(fixGap, absoluteMaxValue)
            fixGap = fixGap / (absoluteMaxValue - absoluteMinValue) * 100
            addFixGap(true)
        }
        //addMaxGap(60F)
        thumbWidth = getThumbWidth()
        thumbHeight = getThumbHeight()

        //thumbHalfWidth = thumbWidth / 2;
        //thumbHalfHeight = thumbHeight / 2;
        barHeight = getBarHeight()
        barPadding = thumbWidth * 0.5f

        // set min start value
        when {
            minStartValue <= absoluteMinValue -> {
                minStartValue = 0f
                setNormalizedMinValue(minStartValue.toDouble())
            }
            minStartValue >= absoluteMaxValue -> {
                minStartValue = absoluteMaxValue
                setMinStartValue()
            }
            else -> {
                setMinStartValue()
            }
        }

        // set max start value
        if (maxStartValue < absoluteMinStartValue || maxStartValue <= absoluteMinValue) {
            maxStartValue = 0f
            setNormalizedMaxValue(maxStartValue.toDouble())
        } else if (maxStartValue >= absoluteMaxValue) {
            maxStartValue = absoluteMaxValue
            setMaxStartValue()
        } else {
            setMaxStartValue()
        }
        invalidate()
        if (onRangeSeekbarChangeListener != null) {
            onRangeSeekbarChangeListener!!.valueChanged(selectedMinValue, selectedMaxValue)
        }
    }

    protected fun getThumbWidth(): Float {
        return if (leftThumb != null) leftThumb!!.width.toFloat() else getThumbDiameter()
    }

    protected fun getThumbHeight(): Float {
        return if (leftThumb != null) leftThumb!!.height.toFloat() else getThumbDiameter()
    }

    protected fun getThumbDiameter(): Float {
        return if (thumbDiameter > 0) thumbDiameter else resources.getDimension(R.dimen.thumb_width)
    }

    protected fun getBarHeight(): Float {
        return if (_barHeight > 0) _barHeight else thumbHeight * 0.5f * 0.3f
    }

    protected fun getBarPadding(): Float {
        return thumbWidth * 0.5f
    }

    protected fun getBitmap(drawable: Drawable?): Bitmap? {
        return if (drawable != null) (drawable as BitmapDrawable).bitmap else null
    }

    protected open fun getCornerRadius(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalRangeSeekbar_corner_radius, 0f)
    }

    protected open fun getMinValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalRangeSeekbar_min_value, 0f)
    }

    protected open fun getMaxValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalRangeSeekbar_max_value, 100f)
    }

    protected open fun getMinStartValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalRangeSeekbar_min_start_value, minValue)
    }

    protected open fun getMaxStartValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalRangeSeekbar_max_start_value, maxValue)
    }

    protected open fun getSteps(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalRangeSeekbar_steps, NO_STEP)
    }

    protected open fun getGap(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalRangeSeekbar_gap, 0f)
    }

    protected open fun getFixedGap(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalRangeSeekbar_fix_gap, NO_FIXED_GAP)
    }

    protected fun getBarColorMode(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.CrystalRangeSeekbar_bar_color_mode,
            CrystalSeekbar.ColorMode.SOLID
        )
    }

    protected fun getBarHeight(typedArray: TypedArray): Float {
        return typedArray.getDimensionPixelSize(R.styleable.CrystalRangeSeekbar_bar_height, 0)
            .toFloat()
    }

    protected open fun getBarColor(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalRangeSeekbar_bar_color, Color.GRAY)
    }

    protected fun getBarGradientStart(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalRangeSeekbar_bar_gradient_start, Color.GRAY)
    }

    protected fun getBarGradientEnd(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalRangeSeekbar_bar_gradient_end, Color.DKGRAY)
    }

    protected fun getBarHighlightColorMode(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.CrystalRangeSeekbar_bar_highlight_color_mode,
            CrystalSeekbar.ColorMode.SOLID
        )
    }

    protected open fun getBarHighlightColor(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalRangeSeekbar_bar_highlight_color, Color.BLACK)
    }

    protected fun getBarHighlightGradientStart(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.CrystalRangeSeekbar_bar_highlight_gradient_start,
            Color.DKGRAY
        )
    }

    protected fun getBarHighlightGradientEnd(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.CrystalRangeSeekbar_bar_highlight_gradient_end,
            Color.BLACK
        )
    }

    protected open fun getLeftThumbColor(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalRangeSeekbar_left_thumb_color, Color.BLACK)
    }

    protected open fun getRightThumbColor(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalRangeSeekbar_right_thumb_color, Color.BLACK)
    }

    protected open fun getLeftThumbColorPressed(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.CrystalRangeSeekbar_left_thumb_color_pressed,
            Color.DKGRAY
        )
    }

    protected open fun getRightThumbColorPressed(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.CrystalRangeSeekbar_right_thumb_color_pressed,
            Color.DKGRAY
        )
    }

    protected open fun getLeftDrawable(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.CrystalRangeSeekbar_left_thumb_image)
    }

    protected open fun getRightDrawable(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.CrystalRangeSeekbar_right_thumb_image)
    }

    protected open fun getLeftDrawablePressed(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.CrystalRangeSeekbar_left_thumb_image_pressed)
    }

    protected open fun getRightDrawablePressed(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.CrystalRangeSeekbar_right_thumb_image_pressed)
    }

    protected open fun getDataType(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.CrystalRangeSeekbar_data_type, DataType.INTEGER)
    }

    protected fun isSeekBarTouchEnabled(typedArray: TypedArray): Boolean {
        return typedArray.getBoolean(R.styleable.CrystalRangeSeekbar_seek_bar_touch_enabled, false)
    }

    protected fun getDiameter(typedArray: TypedArray): Float {
        return typedArray.getDimensionPixelSize(
            R.styleable.CrystalRangeSeekbar_thumb_diameter,
            resources.getDimensionPixelSize(R.dimen.thumb_height)
        ).toFloat()
    }

    protected fun setupBar(canvas: Canvas, paint: Paint?, rect: RectF?) {
        rect!!.left = barPadding
        rect.top = 0.5f * (height - barHeight)
        rect.right = width - barPadding
        rect.bottom = 0.5f * (height + barHeight)
        paint!!.style = Paint.Style.FILL
        paint.isAntiAlias = true
        if (barColorMode == CrystalSeekbar.ColorMode.SOLID) {
            paint.color = barColor
            drawBar(canvas, paint, rect)
        } else {
            paint.shader = LinearGradient(
                rect.left, rect.bottom, rect.right, rect.top,
                barGradientStart,
                barGradientEnd,
                Shader.TileMode.MIRROR
            )
            drawBar(canvas, paint, rect)
            paint.shader = null
        }
    }

    protected fun drawBar(canvas: Canvas, paint: Paint?, rect: RectF?) {
        canvas.drawRoundRect(rect!!, cornerRadius, cornerRadius, paint!!)
    }

    protected fun setupHighlightBar(canvas: Canvas, paint: Paint?, rect: RectF?) {
        rect!!.left = normalizedToScreen(normalizedMinValue) + getThumbWidth() / 2
        rect.right = normalizedToScreen(normalizedMaxValue) + getThumbWidth() / 2
        paint!!.style = Paint.Style.FILL
        paint.isAntiAlias = true
        if (barHighlightColorMode == CrystalSeekbar.ColorMode.SOLID) {
            paint.color = barHighlightColor
            drawHighlightBar(canvas, paint, rect)
        } else {
            paint.shader = LinearGradient(
                rect.left, rect.bottom, rect.right, rect.top,
                barHighlightGradientStart,
                barHighlightGradientEnd,
                Shader.TileMode.MIRROR
            )
            drawHighlightBar(canvas, paint, rect)
            paint.shader = null
        }
    }

    protected fun drawHighlightBar(canvas: Canvas, paint: Paint?, rect: RectF?) {
        canvas.drawRoundRect(rect!!, cornerRadius, cornerRadius, paint!!)
    }

    protected fun setupLeftThumb(canvas: Canvas, paint: Paint?, rect: RectF?) {
        leftThumbColor =
            if (Thumb.MIN == pressedThumb) leftThumbColorPressed else leftThumbColorNormal
        paint!!.color = leftThumbColor

        //float leftL = normalizedToScreen(normalizedMinValue);
        //float rightL = Math.min(leftL + thumbHalfWidth + barPadding, getWidth());
        leftThumbRect!!.left = normalizedToScreen(normalizedMinValue)
        leftThumbRect!!.right =
            Math.min(leftThumbRect!!.left + getThumbWidth() / 2 + barPadding, width.toFloat())
        leftThumbRect!!.top = 0f
        leftThumbRect!!.bottom = thumbHeight
        if (leftThumb != null) {
            val lThumb = if (Thumb.MIN == pressedThumb) leftThumbPressed else leftThumb
            drawLeftThumbWithImage(canvas, paint, leftThumbRect, lThumb)
        } else {
            drawLeftThumbWithColor(canvas, paint, leftThumbRect)
        }
    }

    protected fun drawLeftThumbWithColor(canvas: Canvas, paint: Paint?, rect: RectF?) {
        canvas.drawOval(rect!!, paint!!)
    }

    protected fun drawLeftThumbWithImage(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?,
        image: Bitmap?
    ) {
        canvas.drawBitmap(image!!, rect!!.left, rect.top, paint)
    }

    protected fun setupRightThumb(canvas: Canvas, paint: Paint?, rect: RectF?) {
        rightThumbColor =
            if (Thumb.MAX == pressedThumb) rightThumbColorPressed else rightThumbColorNormal
        paint!!.color = rightThumbColor

        //float leftR = normalizedToScreen(normalizedMaxValue);
        //float rightR = Math.min(leftR + thumbHalfWidth + barPadding, getWidth());
        rightThumbRect!!.left = normalizedToScreen(normalizedMaxValue)
        rightThumbRect!!.right =
            Math.min(rightThumbRect!!.left + getThumbWidth() / 2 + barPadding, width.toFloat())
        rightThumbRect!!.top = 0f
        rightThumbRect!!.bottom = thumbHeight
        if (rightThumb != null) {
            val rThumb = if (Thumb.MAX == pressedThumb) rightThumbPressed else rightThumb
            drawRightThumbWithImage(canvas, paint, rightThumbRect, rThumb)
        } else {
            drawRightThumbWithColor(canvas, paint, rightThumbRect)
        }
    }

    protected fun drawRightThumbWithColor(canvas: Canvas, paint: Paint?, rect: RectF?) {
        canvas.drawOval(rect!!, paint!!)
    }

    protected fun drawRightThumbWithImage(
        canvas: Canvas,
        paint: Paint?,
        rect: RectF?,
        image: Bitmap?
    ) {
        canvas.drawBitmap(image!!, rect!!.left, rect.top, paint)
    }

    protected fun trackTouchEvent(event: MotionEvent) {
        val pointerIndex = event.findPointerIndex(mActivePointerId)
        try {
            val x = event.getX(pointerIndex)
            if (Thumb.MIN == pressedThumb) {
                setNormalizedMinValue(screenToNormalized(x))
            } else if (Thumb.MAX == pressedThumb) {
                setNormalizedMaxValue(screenToNormalized(x))
            }
        } catch (ignored: Exception) {
        }
    }

    protected fun touchDown(x: Float, y: Float) {}
    protected fun touchMove(x: Float, y: Float) {}
    protected fun touchUp(x: Float, y: Float) {}
    protected fun getMeasureSpecWith(widthMeasureSpec: Int): Int {
        var width = 200
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec)
        }
        return width
    }

    protected fun getMeasureSpecHeight(heightMeasureSpec: Int): Int {
        var height = Math.round(thumbHeight)
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec))
        }
        return height
    }

    protected fun log(`object`: Any) {
        Log.d("CRS=>", `object`.toString())
    }

    //////////////////////////////////////////
    // PRIVATE METHODS
    //////////////////////////////////////////
     fun setMinStartValue() {
        if (minStartValue > minValue && minStartValue <= maxValue) {
            minStartValue = Math.min(minStartValue, absoluteMaxValue)
            minStartValue -= absoluteMinValue
            minStartValue = minStartValue / (absoluteMaxValue - absoluteMinValue) * 100
            setNormalizedMinValue(minStartValue.toDouble())
        }
    }

    private fun setMaxStartValue() {
        if (maxStartValue <= absoluteMaxValue && maxStartValue > absoluteMinValue && maxStartValue >= absoluteMinStartValue) {
            maxStartValue = Math.max(absoluteMaxStartValue, absoluteMinValue)
            maxStartValue -= absoluteMinValue
            maxStartValue = maxStartValue / (absoluteMaxValue - absoluteMinValue) * 100
            setNormalizedMaxValue(maxStartValue.toDouble())
        }
    }

    private fun evalPressedThumb(touchX: Float): Thumb? {
        var result: Thumb? = null
        val minThumbPressed = isInThumbRange(touchX, normalizedMinValue)
        val maxThumbPressed = isInThumbRange(touchX, normalizedMaxValue)
        if (minThumbPressed && maxThumbPressed) {
            // if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
            result = if (touchX / width > 0.5f) Thumb.MIN else Thumb.MAX
        } else if (minThumbPressed) {
            result = Thumb.MIN
        } else if (maxThumbPressed) {
            result = Thumb.MAX
        }
        if (seekBarTouchEnabled && result == null) {
            result = findClosestThumb(touchX)
        }
        return result
    }

    private fun isInThumbRange(touchX: Float, normalizedThumbValue: Double): Boolean {
        val thumbPos = normalizedToScreen(normalizedThumbValue)
        val left = thumbPos - getThumbWidth() / 2
        val right = thumbPos + getThumbWidth() / 2
        var x = touchX - getThumbWidth() / 2
        if (thumbPos > width - thumbWidth) x = touchX
        return x >= left && x <= right
        //return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbWidth;
    }

    private fun findClosestThumb(touchX: Float): Thumb {
        val screenMinX = normalizedToScreen(normalizedMinValue)
        val screenMaxX = normalizedToScreen(normalizedMaxValue)
        if (touchX >= screenMaxX) {
            return Thumb.MAX
        } else if (touchX <= screenMinX) {
            return Thumb.MIN
        }
        val minDiff = Math.abs(screenMinX - touchX).toDouble()
        val maxDiff = Math.abs(screenMaxX - touchX).toDouble()
        return if (minDiff < maxDiff) Thumb.MIN else Thumb.MAX
    }

    private fun onStartTrackingTouch() {
        mIsDragging = true
    }

    private fun onStopTrackingTouch() {
        mIsDragging = false
    }

    private fun normalizedToScreen(normalizedCoord: Double): Float {
        val width = width - barPadding * 2
        return normalizedCoord.toFloat() / 100f * width
    }

    private fun screenToNormalized(screenCoord: Float): Double {
        var width = width.toDouble()
        return if (width <= 2 * barPadding) {
            // prevent division by zero, simply return 0.
            0.0
        } else {
            width -= this.barPadding * 2
            var result = screenCoord / width * 100.0
            result -= this.barPadding / width * 100.0
            result = Math.min(100.0, Math.max(0.0, result))
            result
        }
    }

    private fun setNormalizedMinValue(value: Double) {
        normalizedMinValue = Math.max(0.0, Math.min(100.0, Math.min(value, normalizedMaxValue)))
        if (fixGap != NO_FIXED_GAP && fixGap > 0) {
            addFixGap(true)
        } else {
            addMinGap()
        }
        invalidate()
    }

    private fun setNormalizedMaxValue(value: Double) {
        normalizedMaxValue = Math.max(0.0, Math.min(100.0, Math.max(value, normalizedMinValue)))
        if (fixGap != NO_FIXED_GAP && fixGap > 0) {
            addFixGap(false)
            addMaxGap()
        } else {
            addMaxGap()
        }
        invalidate()
    }

    private fun addFixGap(leftThumb: Boolean) {
        if (leftThumb) {
            if(fixGap<normalizedMaxValue){
                if(normalizedMaxValue-normalizedMinValue>fixGap){
                    normalizedMaxValue = fixGap + normalizedMinValue
                }
            }
        } else {
            if(fixGap<normalizedMaxValue){
                if(normalizedMaxValue-normalizedMinValue>fixGap){
                    normalizedMinValue = normalizedMaxValue-fixGap
                }
            }
        }
    }

    fun addMaxGap(maxGap:Float,leftThumb: Boolean=false) {
        if (leftThumb) {
            normalizedMaxValue = normalizedMinValue + maxGap
            if (normalizedMaxValue >= 100) {
                normalizedMaxValue = 100.0
                normalizedMinValue = normalizedMaxValue - maxGap
            }
        } else {
            normalizedMinValue = normalizedMaxValue - maxGap
            if (normalizedMinValue <= 0) {
                normalizedMinValue = 0.0
                normalizedMaxValue = normalizedMinValue + maxGap
            }
        }

//        if (normalizedMaxValue - maxGap < normalizedMinValue) {
//            val g = normalizedMaxValue - maxGap
//            normalizedMinValue = g
//            normalizedMinValue = Math.max(0.0, Math.min(100.0, Math.min(g, normalizedMaxValue)))
//            if (normalizedMaxValue <= normalizedMinValue + maxGap) {
//                normalizedMaxValue = normalizedMinValue + maxGap
//            }
//        }
    }

    private fun addMinGap() {
        if (normalizedMinValue + gap > normalizedMaxValue) {
            val g = normalizedMinValue + gap
            normalizedMaxValue = g
            normalizedMaxValue = Math.max(0.0, Math.min(100.0, Math.max(g, normalizedMinValue)))
            if (normalizedMinValue >= normalizedMaxValue - gap) {
                normalizedMinValue = normalizedMaxValue - gap
            }
        }
    }

    private fun addMaxGap() {
        if (normalizedMaxValue - gap < normalizedMinValue) {
            val g = normalizedMaxValue - gap
            normalizedMinValue = g
            normalizedMinValue = Math.max(0.0, Math.min(100.0, Math.min(g, normalizedMaxValue)))
            if (normalizedMaxValue <= normalizedMinValue + gap) {
                normalizedMaxValue = normalizedMinValue + gap
            }
        }
    }


    private fun normalizedToValue(normalized: Double): Double {
        var `val` = normalized / 100 * (maxValue - minValue)
        `val` += minValue
        return `val`
    }

    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun <T : Number?> formatValue(value: T): Number {
        val v = value as Double
        if (dataType == DataType.LONG) {
            return v.toLong()
        }
        if (dataType == DataType.DOUBLE) {
            return v
        }
        if (dataType == DataType.INTEGER) {
            return Math.round(v)
        }
        if (dataType == DataType.FLOAT) {
            return v.toFloat()
        }
        if (dataType == DataType.SHORT) {
            return v.toInt()
        }
        if (dataType == DataType.BYTE) {
            return v.toInt()
        }
        throw IllegalArgumentException("Number class '" + value.javaClass.name + "' is not supported")
    }

    //////////////////////////////////////////
    // OVERRIDE METHODS
    //////////////////////////////////////////
    @Synchronized
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // prevent render is in edit mode
        if (isInEditMode) return

        // setup bar
        setupBar(canvas, _paint, _rect)

        // setup seek bar active range line
        setupHighlightBar(canvas, _paint, _rect)

        // draw left thumb
        setupLeftThumb(canvas, _paint, _rect)

        // draw right thumb
        setupRightThumb(canvas, _paint, _rect)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getMeasureSpecWith(widthMeasureSpec),
            getMeasureSpecHeight(heightMeasureSpec)
        )
    }

    /**
     * Handles thumb selection and movement. Notifies listener callback on certain events.
     */
    @Synchronized
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = event.getPointerId(event.pointerCount - 1)
                pointerIndex = event.findPointerIndex(mActivePointerId)
                val mDownMotionX = event.getX(pointerIndex)
                pressedThumb = evalPressedThumb(mDownMotionX)
                if (pressedThumb == null) return super.onTouchEvent(event)
                touchDown(event.getX(pointerIndex), event.getY(pointerIndex))
                isPressed = true
                invalidate()
                onStartTrackingTouch()
                trackTouchEvent(event)
                attemptClaimDrag()
            }
            MotionEvent.ACTION_MOVE -> if (pressedThumb != null) {
                if (mIsDragging) {
                    touchMove(event.getX(pointerIndex), event.getY(pointerIndex))
                    trackTouchEvent(event)
                }
                if (onRangeSeekbarChangeListener != null) {
                    onRangeSeekbarChangeListener!!.valueChanged(selectedMinValue, selectedMaxValue)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                    touchUp(event.getX(pointerIndex), event.getY(pointerIndex))
                    if (onRangeSeekbarFinalValueListener != null) {
                        onRangeSeekbarFinalValueListener!!.finalValue(
                            selectedMinValue,
                            selectedMaxValue
                        )
                    }
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should be interpreted as a tap-seek to that location.
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                pressedThumb = null
                invalidate()
                if (onRangeSeekbarChangeListener != null) {
                    onRangeSeekbarChangeListener!!.valueChanged(selectedMinValue, selectedMaxValue)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
            }
            MotionEvent.ACTION_POINTER_UP ->                 /*onSecondaryPointerUp(event);*/invalidate()
            MotionEvent.ACTION_CANCEL -> {
                if (mIsDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                    touchUp(event.getX(pointerIndex), event.getY(pointerIndex))
                }
                invalidate() // see above explanation
            }
        }
        return true
    }

    companion object {
        //////////////////////////////////////////
        // PRIVATE CONSTANTS
        //////////////////////////////////////////
        private const val INVALID_POINTER_ID = 255
    }

    //////////////////////////////////////////
    // CONSTRUCTOR
    //////////////////////////////////////////
    init {

        // prevent render is in edit mode
        if (!isInEditMode) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.CrystalRangeSeekbar)
            try {
                cornerRadius = getCornerRadius(array)
                minValue = getMinValue(array)
                maxValue = getMaxValue(array)
                minStartValue = getMinStartValue(array)
                maxStartValue = getMaxStartValue(array)
                steps = getSteps(array)
                gap = getGap(array)
                fixGap = getFixedGap(array)
                _barHeight = getBarHeight(array)
                barColorMode = getBarColorMode(array)
                barColor = getBarColor(array)
                barGradientStart = getBarGradientStart(array)
                barGradientEnd = getBarGradientEnd(array)
                barHighlightColorMode = getBarHighlightColorMode(array)
                barHighlightColor = getBarHighlightColor(array)
                barHighlightGradientStart = getBarHighlightGradientStart(array)
                barHighlightGradientEnd = getBarHighlightGradientEnd(array)
                leftThumbColorNormal = getLeftThumbColor(array)
                rightThumbColorNormal = getRightThumbColor(array)
                leftThumbColorPressed = getLeftThumbColorPressed(array)
                rightThumbColorPressed = getRightThumbColorPressed(array)
                leftDrawable = getLeftDrawable(array)
                rightDrawable = getRightDrawable(array)
                leftDrawablePressed = getLeftDrawablePressed(array)
                rightDrawablePressed = getRightDrawablePressed(array)
                thumbDiameter = getDiameter(array)
                dataType = getDataType(array)
                seekBarTouchEnabled = isSeekBarTouchEnabled(array)
            } finally {
                array.recycle()
            }
            init()
        }
    }
}