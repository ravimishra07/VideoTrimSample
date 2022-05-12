package com.ravi.videotrimsample.customseekar

import android.content.Context
import kotlin.jvm.JvmOverloads
import com.ravi.videotrimsample.interfaces.OnSeekbarChangeListener
import com.ravi.videotrimsample.interfaces.OnSeekbarFinalValueListener
import com.ravi.videotrimsample.customseekar.CrystalSeekbar
import androidx.core.content.ContextCompat
import com.ravi.videotrimsample.R
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
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
open class CrystalSeekbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    //private static int DEFAULT_THUMB_WIDTH ;
    //private static int DEFAULT_THUMB_HEIGHT;
    private val NO_STEP = -1f

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

    object Position {
        const val LEFT = 0
        const val RIGHT = 1
    }

    object ColorMode {
        const val SOLID = 0
        const val GRADIENT = 1
    }

    //////////////////////////////////////////
    // PRIVATE VAR
    //////////////////////////////////////////
    private var onSeekbarChangeListener: OnSeekbarChangeListener? = null
    private var onSeekbarFinalValueListener: OnSeekbarFinalValueListener? = null
    private var absoluteMinValue = 0f
    private var absoluteMaxValue = 0f
    var minValue = 0f
        private set
    var maxValue = 0f
        private set
    var minStartValue = 0f
        private set
    var steps = 0f
        private set
    private var mActivePointerId = INVALID_POINTER_ID
    var position = 0
        private set
    private var nextPosition = 0
    var dataType = 0
        private set
    var cornerRadius = 0f
        private set
    private var barColorMode = 0
    var barColor = 0
        private set
    private var barGradientStart = 0
    private var barGradientEnd = 0
    private var barHighlightColorMode = 0
    var barHighlightColor = 0
        private set
    private var barHighlightGradientStart = 0
    private var barHighlightGradientEnd = 0
    var leftThumbColor = 0
        private set
    private var thumbColorNormal = 0
    var leftThumbColorPressed = 0
        private set
    private var seekBarTouchEnabled = false
    private var barPadding = 0f
    private var _barHeight = 0f
    private var barHeight = 0f
    private var thumbWidth = 0f
    private var thumbHeight = 0f
    private var thumbDiameter = 0f
    var leftDrawable: Drawable? = null
    var leftDrawablePressed: Drawable? = null
    private var thumb: Bitmap? = null
    private var thumbPressed: Bitmap? = null
    var pressedThumb: Thumb? = null
        private set
    private var normalizedMinValue = 0.0
    private var normalizedMaxValue = 100.0
    private var pointerIndex = 0
    private var _rect: RectF? = null
    private var _paint: Paint? = null
    var thumbRect: RectF? = null
        private set
    private var mIsDragging = false

    //////////////////////////////////////////
    // ENUMERATION
    //////////////////////////////////////////
    enum class Thumb {
        MIN
    }

    //////////////////////////////////////////
    // INITIALIZING
    //////////////////////////////////////////
    protected fun init() {
        absoluteMinValue = minValue
        absoluteMaxValue = maxValue
        leftThumbColor = thumbColorNormal
        thumb = getBitmap(leftDrawable)
        thumbPressed = getBitmap(leftDrawablePressed)
        thumbPressed = if (thumbPressed == null) thumb else thumbPressed
        thumbWidth = getThumbWidth()
        thumbHeight = getThumbHeight()
        barHeight = getBarHeight()
        barPadding = getBarPadding()
        _paint = Paint(Paint.ANTI_ALIAS_FLAG)
        _rect = RectF()
        thumbRect = RectF()
        pressedThumb = null
        setMinStartValue()
        setWillNotDraw(false)
    }

    //////////////////////////////////////////
    // PUBLIC METHODS
    //////////////////////////////////////////
    fun setCornerRadius(cornerRadius: Float): CrystalSeekbar {
        this.cornerRadius = cornerRadius
        return this
    }

    fun setMinValue(minValue: Float): CrystalSeekbar {
        this.minValue = minValue
        absoluteMinValue = minValue
        return this
    }

    fun setMaxValue(maxValue: Float): CrystalSeekbar {
        this.maxValue = maxValue
        absoluteMaxValue = maxValue
        return this
    }

    fun setMinStartValue(minStartValue: Float): CrystalSeekbar {
        this.minStartValue = minStartValue
        return this
    }

    fun setSteps(steps: Float): CrystalSeekbar {
        this.steps = steps
        return this
    }

    fun setBarHeight(barHeight: Float): CrystalSeekbar {
        _barHeight = barHeight
        return this
    }

    fun setBarColorMode(barColorMode: Int): CrystalSeekbar {
        this.barColorMode = barColorMode
        return this
    }

    fun setBarColor(barColor: Int): CrystalSeekbar {
        this.barColor = barColor
        return this
    }

    fun setBarGradientStart(barGradientStart: Int): CrystalSeekbar {
        this.barGradientStart = barGradientStart
        return this
    }

    fun setBarGradientEnd(barGradientEnd: Int): CrystalSeekbar {
        this.barGradientEnd = barGradientEnd
        return this
    }

    fun setBarHighlightColorMode(barHighlightColorMode: Int): CrystalSeekbar {
        this.barHighlightColorMode = barHighlightColorMode
        return this
    }

    fun setBarHighlightColor(barHighlightColor: Int): CrystalSeekbar {
        this.barHighlightColor = barHighlightColor
        return this
    }

    fun setBarHighlightGradientStart(barHighlightGradientStart: Int): CrystalSeekbar {
        this.barHighlightGradientStart = barHighlightGradientStart
        return this
    }

    fun setBarHighlightGradientEnd(barHighlightGradientEnd: Int): CrystalSeekbar {
        this.barHighlightGradientEnd = barHighlightGradientEnd
        return this
    }

    fun setThumbColor(leftThumbColorNormal: Int): CrystalSeekbar {
        thumbColorNormal = leftThumbColorNormal
        return this
    }

    fun setThumbHighlightColor(leftThumbColorPressed: Int): CrystalSeekbar {
        this.leftThumbColorPressed = leftThumbColorPressed
        return this
    }

    fun setThumbDrawable(resId: Int): CrystalSeekbar {
        setThumbDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setThumbDrawable(drawable: Drawable?): CrystalSeekbar {
        setThumbBitmap(getBitmap(drawable))
        return this
    }

    fun setThumbBitmap(bitmap: Bitmap?): CrystalSeekbar {
        thumb = bitmap
        return this
    }

    fun setThumbHighlightDrawable(resId: Int): CrystalSeekbar {
        setThumbHighlightDrawable(ContextCompat.getDrawable(context, resId))
        return this
    }

    fun setThumbHighlightDrawable(drawable: Drawable?): CrystalSeekbar {
        setThumbHighlightBitmap(getBitmap(drawable))
        return this
    }

    fun setThumbHighlightBitmap(bitmap: Bitmap?): CrystalSeekbar {
        thumbPressed = bitmap
        return this
    }

    fun setDataType(dataType: Int): CrystalSeekbar {
        this.dataType = dataType
        return this
    }

    fun setPosition(pos: Int): CrystalSeekbar {
        nextPosition = pos
        return this
    }

    fun setOnSeekbarChangeListener(onSeekbarChangeListener: OnSeekbarChangeListener?) {
        this.onSeekbarChangeListener = onSeekbarChangeListener
        if (this.onSeekbarChangeListener != null) {
            this.onSeekbarChangeListener!!.valueChanged(selectedMinValue)
        }
    }

    fun setOnSeekbarFinalValueListener(onSeekbarFinalValueListener: OnSeekbarFinalValueListener?) {
        this.onSeekbarFinalValueListener = onSeekbarFinalValueListener
    }

    fun getThumbWidth(): Float {
        return if (thumb != null) thumb!!.width.toFloat() else getThumbDiameter()
    }

    fun getThumbHeight(): Float {
        return if (thumb != null) thumb!!.height.toFloat() else getThumbDiameter()
    }

    protected fun getThumbDiameter(): Float {
        return if (thumbDiameter > 0) thumbDiameter else resources.getDimension(R.dimen.thumb_width)
    }

    fun getBarHeight(): Float {
        return if (_barHeight > 0) _barHeight else thumbHeight * 0.5f * 0.3f
    }

    fun getDiameter(typedArray: TypedArray): Float {
        return typedArray.getDimensionPixelSize(
            R.styleable.CrystalSeekbar_thumb_diameter,
            resources.getDimensionPixelSize(R.dimen.thumb_height)
        ).toFloat()
    }

    protected fun isSeekBarTouchEnabled(typedArray: TypedArray): Boolean {
        return typedArray.getBoolean(R.styleable.CrystalSeekbar_seek_bar_touch_enabled, false)
    }

    fun getBarPadding(): Float {
        return thumbWidth * 0.5f
    }

    val selectedMinValue: Number
        get() {
            var nv = normalizedMinValue
            if (steps > 0 && steps <= absoluteMaxValue / 2) {
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
            nv = if (position == Position.LEFT) nv else Math.abs(nv - maxValue)
            return formatValue(normalizedToValue(nv))
        }
    val selectedMaxValue: Number
        get() {
            var nv = normalizedMaxValue
            if (steps > 0 && steps <= absoluteMaxValue / 2) {
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
        //normalizedMinValue = 0d;
        //normalizedMaxValue = 100d;
        thumbWidth =
            if (thumb != null) thumb!!.width.toFloat() else resources.getDimension(R.dimen.thumb_width)
        thumbHeight =
            if (thumb != null) thumb!!.height.toFloat() else resources.getDimension(R.dimen.thumb_height)
        barHeight = thumbHeight * 0.5f * 0.3f
        barPadding = thumbWidth * 0.5f

        // set min start value
        if (minStartValue <= minValue) {
            minStartValue = 0f
            setNormalizedMinValue(minStartValue.toDouble())
        } else if (minStartValue > maxValue) {
            minStartValue = maxValue
            setNormalizedMinValue(minStartValue.toDouble())
        } else {
            //minStartValue = (long)getSelectedMinValue();
            if (nextPosition != position) {
                minStartValue = Math.abs(normalizedMaxValue - normalizedMinValue).toFloat()
            }
            if (minStartValue > minValue) {
                minStartValue = Math.min(minStartValue, absoluteMaxValue)
                minStartValue -= absoluteMinValue
                minStartValue = minStartValue / (absoluteMaxValue - absoluteMinValue) * 100
            }
            setNormalizedMinValue(minStartValue.toDouble())
            position = nextPosition
        }
        invalidate()
        if (onSeekbarChangeListener != null) {
            onSeekbarChangeListener!!.valueChanged(selectedMinValue)
        }
    }

    //////////////////////////////////////////
    // PROTECTED METHODS
    //////////////////////////////////////////
    protected fun getBitmap(drawable: Drawable?): Bitmap? {
        return if (drawable != null) (drawable as BitmapDrawable).bitmap else null
    }

    protected open fun getCornerRadius(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalSeekbar_corner_radius, 0f)
    }

    protected open fun getMinValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalSeekbar_min_value, 0f)
    }

    protected open fun getMaxValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalSeekbar_max_value, 100f)
    }

    protected open fun getMinStartValue(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalSeekbar_min_start_value, minValue)
    }

    protected open fun getSteps(typedArray: TypedArray): Float {
        return typedArray.getFloat(R.styleable.CrystalSeekbar_steps, NO_STEP)
    }

    protected fun getBarHeight(typedArray: TypedArray): Float {
        return typedArray.getDimensionPixelSize(R.styleable.CrystalSeekbar_bar_height, 0).toFloat()
    }

    protected fun getBarColorMode(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.CrystalSeekbar_bar_color_mode, ColorMode.SOLID)
    }

    protected open fun getBarColor(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalSeekbar_bar_color, Color.GRAY)
    }

    protected fun getBarGradientStart(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalSeekbar_bar_gradient_start, Color.GRAY)
    }

    protected fun getBarGradientEnd(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalSeekbar_bar_gradient_end, Color.DKGRAY)
    }

    protected fun getBarHighlightColorMode(typedArray: TypedArray): Int {
        return typedArray.getInt(
            R.styleable.CrystalSeekbar_bar_highlight_color_mode,
            ColorMode.SOLID
        )
    }

    protected open fun getBarHighlightColor(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalSeekbar_bar_highlight_color, Color.BLACK)
    }

    protected fun getBarHighlightGradientStart(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.CrystalSeekbar_bar_highlight_gradient_start,
            Color.DKGRAY
        )
    }

    protected fun getBarHighlightGradientEnd(typedArray: TypedArray): Int {
        return typedArray.getColor(
            R.styleable.CrystalSeekbar_bar_highlight_gradient_end,
            Color.BLACK
        )
    }

    protected open fun getThumbColor(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalSeekbar_thumb_color, Color.BLACK)
    }

    protected open fun getThumbColorPressed(typedArray: TypedArray): Int {
        return typedArray.getColor(R.styleable.CrystalSeekbar_thumb_color_pressed, Color.DKGRAY)
    }

    protected open fun getThumbDrawable(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.CrystalSeekbar_thumb_image)
    }

    protected open fun getThumbDrawablePressed(typedArray: TypedArray): Drawable? {
        return typedArray.getDrawable(R.styleable.CrystalSeekbar_thumb_image_pressed)
    }

    protected open fun getDataType(typedArray: TypedArray): Int {
        return typedArray.getInt(R.styleable.CrystalSeekbar_data_type, DataType.INTEGER)
    }

    protected fun getPosition(typedArray: TypedArray): Int {
        val pos = typedArray.getInt(R.styleable.CrystalSeekbar_position, Position.LEFT)
        normalizedMinValue = if (pos == Position.LEFT) normalizedMinValue else normalizedMaxValue
        return pos
    }

    protected fun setupBar(canvas: Canvas, paint: Paint?, rect: RectF?) {
        rect!!.left = barPadding
        rect.top = 0.5f * (height - barHeight)
        rect.right = width - barPadding
        rect.bottom = 0.5f * (height + barHeight)
        paint!!.style = Paint.Style.FILL
        paint.isAntiAlias = true
        if (barColorMode == ColorMode.SOLID) {
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
        if (position == Position.RIGHT) {
            rect!!.left = normalizedToScreen(normalizedMinValue) + getThumbWidth() / 2
            rect.right = width - getThumbWidth() / 2
        } else {
            rect!!.left = getThumbWidth() / 2
            rect.right = normalizedToScreen(normalizedMinValue) + getThumbWidth() / 2
        }
        paint!!.style = Paint.Style.FILL
        paint.isAntiAlias = true
        if (barHighlightColorMode == ColorMode.SOLID) {
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
        leftThumbColor = if (Thumb.MIN == pressedThumb) leftThumbColorPressed else thumbColorNormal
        paint!!.color = leftThumbColor
        thumbRect!!.left = normalizedToScreen(normalizedMinValue)
        thumbRect!!.right =
            Math.min(thumbRect!!.left + getThumbWidth() / 2 + barPadding, width.toFloat())
        thumbRect!!.top = 0f
        thumbRect!!.bottom = thumbHeight
        if (thumb != null) {
            val lThumb = if (Thumb.MIN == pressedThumb) thumbPressed else thumb
            drawLeftThumbWithImage(canvas, paint, thumbRect, lThumb)
        } else {
            drawLeftThumbWithColor(canvas, paint, thumbRect)
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

    protected fun trackTouchEvent(event: MotionEvent) {
        val pointerIndex = event.findPointerIndex(mActivePointerId)
        try {
            val x = event.getX(pointerIndex)
            if (Thumb.MIN == pressedThumb) {
                setNormalizedMinValue(screenToNormalized(x))
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
    private fun setMinStartValue() {
        if (minStartValue > minValue && minStartValue < maxValue) {
            minStartValue = Math.min(minStartValue, absoluteMaxValue)
            minStartValue -= absoluteMinValue
            minStartValue = minStartValue / (absoluteMaxValue - absoluteMinValue) * 100
            setNormalizedMinValue(minStartValue.toDouble())
        }
    }

    private fun evalPressedThumb(touchX: Float): Thumb? {
        var result: Thumb? = null
        val minThumbPressed = isInThumbRange(touchX, normalizedMinValue)
        if (seekBarTouchEnabled || minThumbPressed) {
            // if both thumbs are pressed (they lie on top of each other), choose the one with more room to drag. this avoids "stalling" the thumbs in a corner, not being able to drag them apart anymore.
            result = Thumb.MIN
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
            width = width - barPadding * 2
            var result = screenCoord / width * 100.0
            result = result - barPadding / width * 100.0
            result = Math.min(100.0, Math.max(0.0, result))
            result
        }
    }

    private fun setNormalizedMinValue(value: Double) {
        normalizedMinValue = Math.max(0.0, Math.min(100.0, Math.min(value, normalizedMaxValue)))
        invalidate()
    }

    private fun setNormalizedMaxValue(value: Double) {
        normalizedMaxValue = Math.max(0.0, Math.min(100.0, Math.max(value, normalizedMinValue)))
        invalidate()
    }

    private fun normalizedToValue(normalized: Double): Double {
        var `val` = normalized / 100 * (maxValue - minValue)
        `val` = if (position == Position.LEFT) `val` + minValue else `val`
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
                if (onSeekbarChangeListener != null) {
                    onSeekbarChangeListener!!.valueChanged(selectedMinValue)
                }
            }
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                    touchUp(event.getX(pointerIndex), event.getY(pointerIndex))
                    if (onSeekbarFinalValueListener != null) {
                        onSeekbarFinalValueListener!!.finalValue(selectedMinValue)
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
                if (onSeekbarChangeListener != null) {
                    onSeekbarChangeListener!!.valueChanged(selectedMinValue)
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
            val array = context.obtainStyledAttributes(attrs, R.styleable.CrystalSeekbar)
            try {
                cornerRadius = getCornerRadius(array)
                minValue = getMinValue(array)
                maxValue = getMaxValue(array)
                minStartValue = getMinStartValue(array)
                steps = getSteps(array)
                _barHeight = getBarHeight(array)
                barColorMode = getBarColorMode(array)
                barColor = getBarColor(array)
                barGradientStart = getBarGradientStart(array)
                barGradientEnd = getBarGradientEnd(array)
                barHighlightColorMode = getBarHighlightColorMode(array)
                barHighlightColor = getBarHighlightColor(array)
                barHighlightGradientStart = getBarHighlightGradientStart(array)
                barHighlightGradientEnd = getBarHighlightGradientEnd(array)
                thumbColorNormal = getThumbColor(array)
                leftThumbColorPressed = getThumbColorPressed(array)
                leftDrawable = getThumbDrawable(array)
                leftDrawablePressed = getThumbDrawablePressed(array)
                dataType = getDataType(array)
                position = getPosition(array)
                nextPosition = position
                thumbDiameter = getDiameter(array)
                seekBarTouchEnabled = isSeekBarTouchEnabled(array)
            } finally {
                array.recycle()
            }
            init()
        }
    }
}