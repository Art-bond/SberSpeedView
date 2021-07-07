package ru.d3st.sberspeedview.speedview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import ru.d3st.sberspeedview.R

class SpeedView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_WIDTH = 200 // default width
    }

    private lateinit var arrowBlack: Paint // feature black arrow
    private lateinit var textPaint: Paint

    private val textArray =
        arrayOf("0", "20", "40", "60", "80", "100", "120", "140", "160", "180", "200", "220")
    private var refreshThread: Thread? = null

    private var progress: Int = 0


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
        arrowBlack = Paint()
        with(arrowBlack) {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.STROKE
            progress = typedArray.getInt(R.styleable.SpeedView_android_progress, 0)
            strokeWidth =
                typedArray.getDimensionPixelSize(R.styleable.SpeedView_strokeWidth, 0).toFloat()
        }
        //digits on circle
        textPaint = Paint()
        with(textPaint) {
            color = typedArray.getColor(R.styleable.SpeedView_android_color, 0)
            textSize = typedArray.getDimensionPixelSize(R.styleable.SpeedView_android_textSize, 0).toFloat()
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
        arrowBlack.strokeWidth = 5f
        canvas?.drawCircle(
            measuredWidth / 2.toFloat(),
            measuredHeight / 2.toFloat(),
            (measuredWidth / 2 - 5).toFloat(),
            arrowBlack
        )
    }

    private fun drawCenter(canvas: Canvas?) {
        canvas?.drawCircle(
            measuredWidth / 2.toFloat(),
            measuredHeight / 2.toFloat(),
            10f,
            arrowBlack
        )
    }

    private fun drawArrow(canvas: Canvas?) {

        arrowBlack.strokeWidth = 10f
        arrowBlack.strokeWidth = 15f
        calculateArrowDraw(canvas, arrowBlack)
    }


    private fun drawSpeedValues(canvas: Canvas?) {
        val textR = (measuredWidth / 2 - 50).toFloat() // radius of circle formed by text
        for (i in 0..11) {
            val startX =
                (measuredWidth / 2 + textR * Math.sin(Math.PI / 6 * i) - textPaint.measureText(
                    textArray[i]
                ) / 2).toFloat()
            val startY =
                (measuredHeight / 2 - textR * Math.cos(Math.PI / 6 * i) + textPaint.measureText(
                    textArray[i]
                ) / 2).toFloat()
            canvas?.drawText(textArray[i], startX, startY, textPaint)
        }
    }

    private fun drawScale(canvas: Canvas?) {
        var scaleLength: Float?
        canvas?.save()
        //0.. 59 for [0,59]
        for (i in 0..59) {
            if (i % 5 == 0) {
                //Large scale
                arrowBlack.strokeWidth = 5f
                scaleLength = 20f
            } else {
                //Small scale
                arrowBlack.strokeWidth = 3f
                scaleLength = 10f
            }
            canvas?.drawLine(
                measuredWidth / 2.toFloat(),
                5f,
                measuredWidth / 2.toFloat(),
                (5 + scaleLength),
                arrowBlack
            )
            canvas?.rotate(
                360 / 60.toFloat(),
                measuredWidth / 2.toFloat(),
                measuredHeight / 2.toFloat()
            )
        }
        //Restore the original state
        canvas?.restore()
    }


    private fun calculateArrowDraw(canvas: Canvas?, paint: Paint?) {
        //The radius is a little smaller than the second hand
        val longR = measuredWidth / 2 - 120
        val shortR = 40
        val startX = (measuredWidth / 2 - shortR * Math.sin(progress.times(Math.PI / 50))).toFloat()
        val startY = (measuredWidth / 2 + shortR * Math.cos(progress.times(Math.PI / 50))).toFloat()
        val endX = (measuredWidth / 2 + longR * Math.sin(progress.times(Math.PI / 50))).toFloat()
        val endY = (measuredWidth / 2 - longR * Math.cos(progress.times(Math.PI / 50))).toFloat()
        canvas?.drawLine(startX, startY, endX, endY, paint!!)
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)
        val result =
            if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
                DEFAULT_WIDTH
            } else {
                Math.min(widthSpecSize, heightSpecSize)
            }
        setMeasuredDimension(result, result)
    }
}





