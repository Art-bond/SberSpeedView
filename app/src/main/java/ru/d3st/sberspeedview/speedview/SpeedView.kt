package ru.d3st.sberspeedview.speedview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import ru.d3st.sberspeedview.R
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private const val DEFAULT_WIDTH = 200 // default width
private const val TAG = "MySpeedView"


class SpeedView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    private lateinit var paintArrowBlack: Paint // feature black arrow
    private lateinit var paintText: Paint

    private var strokeWidth = 0f
    private val circleRect = RectF()

    /**
     * Length items speedometer
     */
    private val radiusCenter = 10f
    //min value short arrow
    private val shortR = 40f



    private val textArray =
        arrayOf("0", "20", "40", "60", "80", "100", "120", "140", "160", "180", "200", "220")

    private var progress: Int = 0

    private val textBoundsRect = Rect()


    init {
        initPaints(attrs)
    }


    private fun initPaints(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SpeedView,
            R.attr.speedProgressDefaultAttr,
            0
        )
        //arrow
        paintArrowBlack = Paint()
        with(paintArrowBlack) {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.STROKE
            progress = typedArray.getInt(R.styleable.SpeedView_android_progress, 0)
            strokeWidth =
                typedArray.getDimensionPixelSize(R.styleable.SpeedView_strokeWidth, 0).toFloat()
        }
        //digits on circle
        paintText = Paint()
        with(paintText) {
            color = typedArray.getColor(R.styleable.SpeedView_android_color, 0)
            textSize = typedArray.getDimensionPixelSize(R.styleable.SpeedView_android_textSize, 0)
                .toFloat()
            isAntiAlias = true
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //Draw the outermost circle first
        drawOuterCircle(canvas)

        //Draw scale
        drawScale(canvas)

        //Draw digits
        drawSpeedValues(canvas)

        //Draw the needle
        drawArrow(canvas)

        //Draw epicenter
        drawCenter(canvas)
    }


    private fun drawOuterCircle(canvas: Canvas?) {
        paintArrowBlack.strokeWidth = 5f
        canvas?.drawCircle(
/*            measuredWidth / 2.toFloat(),
            measuredHeight / 2.toFloat(),
            (measuredWidth / 2 - 5).toFloat(),*/
            circleRect.width() / 2.toFloat(),
            circleRect.height() / 2.toFloat(),
            (circleRect.width() / 2 - 5),
            paintArrowBlack
        )
    }

    private fun drawCenter(canvas: Canvas?) {
        canvas?.drawCircle(
/*            measuredWidth / 2.toFloat(),
            measuredHeight / 2.toFloat(),*/
            circleRect.width() / 2.toFloat(),
            circleRect.height() / 2.toFloat(),
            radiusCenter,
            paintArrowBlack
        )
    }

    private fun drawArrow(canvas: Canvas?) {

        paintArrowBlack.strokeWidth = 15f
        calculateArrowDraw(canvas, paintArrowBlack)
    }


    private fun drawSpeedValues(canvas: Canvas?) {
/*        val textR = (measuredWidth / 2 - 50).toFloat() // radius of circle formed by text
        for (i in 0..11) {
            val startX =
                (measuredWidth / 2 + textR * sin(Math.PI / 6 * i) - paintText.measureText(
                    textArray[i]
                ) / 2).toFloat()
            val startY =
                (measuredHeight / 2 - textR * cos(Math.PI / 6 * i) + paintText.measureText(
                    textArray[i]
                ) / 2).toFloat()*/
        val textR = (circleRect.width() / 2 - 50) // radius of circle formed by text
        for (i in 0..11) {
            val startX =
                (circleRect.width() / 2 + textR * sin(Math.PI / 6 * i) - paintText.measureText(
                    textArray[i]
                ) / 2).toFloat()
            val startY =
                (circleRect.height() / 2 - textR * cos(Math.PI / 6 * i) + paintText.measureText(
                    textArray[i]
                ) / 2).toFloat()
            canvas?.drawText(textArray[i], startX, startY, paintText)
        }
    }

    private fun drawScale(canvas: Canvas?) {
        var scaleLength:Float?
        canvas?.save()
        //0.. 59 for [0,59]
        for (i in 0..59) {
            if (i % 5 == 0) {
                //Large scale
                paintArrowBlack.strokeWidth = 5f
                scaleLength = 20f
            } else {
                //Small scale
                paintArrowBlack.strokeWidth = 3f
                scaleLength = 10f
            }
            canvas?.drawLine(
/*                measuredWidth / 2.toFloat(),
                5f,
                measuredWidth / 2.toFloat(),*/
                circleRect.width() / 2.toFloat(),
                5f,
                circleRect.width() / 2.toFloat(),
                (5 + scaleLength),
                paintArrowBlack
            )
            canvas?.rotate(
                360 / 60.toFloat(),
/*                measuredWidth / 2.toFloat(),
                measuredHeight / 2.toFloat()*/
                circleRect.width() / 2.toFloat(),
                circleRect.height() / 2.toFloat()
            )
        }
        //Restore the original state
        canvas?.restore()
    }


    private fun calculateArrowDraw(canvas: Canvas?, paint: Paint?) {
        //The radius is a little smaller
        /* val longR = measuredWidth / 2 - 120
         val shortR = 40
         val startX = (measuredWidth / 2 - shortR * sin(progress.times(Math.PI / 50))).toFloat()
         val startY = (measuredWidth / 2 + shortR * cos(progress.times(Math.PI / 50))).toFloat()
         val endX = (measuredWidth / 2 + longR * sin(progress.times(Math.PI / 50))).toFloat()
         val endY = (measuredWidth / 2 - longR * cos(progress.times(Math.PI / 50))).toFloat()*/
        val longR = circleRect.width() / 2 - 120
        val startX = (circleRect.width() / 2 - shortR * sin(progress.times(Math.PI / 50))).toFloat()
        val startY = (circleRect.width() / 2 + shortR * cos(progress.times(Math.PI / 50))).toFloat()
        val endX = (circleRect.width() / 2 + longR * sin(progress.times(Math.PI / 50))).toFloat()
        val endY = (circleRect.width() / 2 - longR * cos(progress.times(Math.PI / 50))).toFloat()
        canvas?.drawLine(startX, startY, endX, endY, paint!!)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d(TAG, "onSizeChanged() called with: w = $w, h = $h, oldw = $oldw, oldh = $oldh")

        val size = min(w, h) - strokeWidth / 2
        circleRect.set(
            strokeWidth / 2 + paddingLeft,
            strokeWidth / 2 + paddingTop,
            size - paddingRight,
            size - paddingBottom
        )

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.d(
            TAG,
            "onMeasure() called with: widthMeasureSpec = ${MeasureSpec.toString(widthMeasureSpec)}, heightMeasureSpec = ${
                MeasureSpec.toString(
                    heightMeasureSpec
                )
            }"
        )
        Log.d(TAG, "suggestedMinimumWidth = $suggestedMinimumWidth , suggestedMinimumHeight = $suggestedMinimumHeight")
        paintText.getTextBounds("240", 0, "240".length, textBoundsRect)
        //min length long arrow
        val longRmin = 360f
        //max length scale
        val maxScaleLength = 20f


        val measuredWidth =
            (strokeWidth * 2 + textBoundsRect.width() + radiusCenter + longRmin + shortR + maxScaleLength + paddingLeft + paddingRight).toInt()

        Log.d(TAG, "measuredWidth = $measuredWidth ")

        val measuredHeight =
            (strokeWidth * 2 + textBoundsRect.height() + radiusCenter + longRmin + shortR + maxScaleLength + paddingTop + paddingBottom).toInt()
        Log.d(TAG, "measuredHeight = $measuredHeight ")

        val requestedWidth = max(measuredWidth, suggestedMinimumWidth)
        val requestedHeight = max(measuredHeight, suggestedMinimumHeight)

        val requestedSize = max(requestedHeight, requestedWidth)
        Log.d(TAG, "requestedSize = $requestedSize ")


        setMeasuredDimension(
            resolveSizeAndState(requestedSize, widthMeasureSpec, 0),
            resolveSizeAndState(requestedSize, heightMeasureSpec, 0)
        )

/*        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val result =
            if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
                DEFAULT_WIDTH
            } else {
                min(widthSpecSize, heightSpecSize)
            }
        setMeasuredDimension(result, result)*/


    }
}





