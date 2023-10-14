package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View.OnTouchListener
import androidx.appcompat.widget.AppCompatImageView


class TouchImageView : AppCompatImageView {

    private val matrix = Matrix()
    private val NONE = 0
    private val DRAG = 1
    private val ZOOM = 2
    private var mode = NONE
    private var last = PointF()
    private var start = PointF()
    private var minScale = 1f
    private var maxScale = 3f
    private lateinit var m: FloatArray
    private var redundantXSpace = 0f
    private var redundantYSpace: kotlin.Float = 0f
    private var width = 0f
    private var height = 0f
    private val CLICK = 3
    private var saveScale = 1f
    private var right = 0f
    private var bottom = 0f
    private var origWidth = 0f
    private var origHeight = 0f
    private var bmWidth = 0f
    private var bmHeight = 0f
    private var mScaleDetector: ScaleGestureDetector? = null
    private lateinit var context: Context


    fun sharedConstructing(context: Context) {
        super.setClickable(true)
        this.context = context

        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        matrix.setTranslate(1f, 1f)
        m = FloatArray(9)
        imageMatrix = matrix
        scaleType = ScaleType.MATRIX

        setOnTouchListener(OnTouchListener { v, event ->
            mScaleDetector!!.onTouchEvent(event)
            matrix.getValues(m)
            val x = m[Matrix.MTRANS_X]
            val y = m[Matrix.MTRANS_Y]
            val curr = PointF(event.x, event.y)
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    last[event.x] = event.y
                    start.set(last)
                    mode = DRAG
                }

                MotionEvent.ACTION_MOVE -> if (mode === DRAG) {
                    var deltaX = curr.x - last.x
                    var deltaY = curr.y - last.y
                    val scaleWidth = Math.round(origWidth * saveScale).toFloat()
                    val scaleHeight = Math.round(origHeight * saveScale).toFloat()
                    if (scaleWidth < width) {
                        deltaX = 0f
                        if (y + deltaY > 0) deltaY = -y else if (y + deltaY < -bottom) deltaY =
                            -(y + bottom)
                    } else if (scaleHeight < height) {
                        deltaY = 0f
                        if (x + deltaX > 0) deltaX = -x else if (x + deltaX < -right) deltaX =
                            -(x + right)
                    } else {
                        if (x + deltaX > 0) deltaX = -x else if (x + deltaX < -right) deltaX =
                            -(x + right)
                        if (y + deltaY > 0) deltaY = -y else if (y + deltaY < -bottom) deltaY =
                            -(y + bottom)
                    }
                    matrix.postTranslate(deltaX, deltaY)
                    last[curr.x] = curr.y
                }

                MotionEvent.ACTION_UP -> {
                    mode = NONE
                    val xDiff = Math.abs(curr.x - start.x).toInt()
                    val yDiff = Math.abs(curr.y - start.y).toInt()
                    if (xDiff < CLICK && yDiff < CLICK) performClick()
                }

                MotionEvent.ACTION_POINTER_UP -> mode = NONE
            }
            imageMatrix = matrix
            invalidate()
            true // indicate event was handled
        })
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (bm != null) {
            bmWidth = bm.getWidth().toFloat();
            bmHeight = bm.getHeight().toFloat();
        }
    }

    fun setMaxZoom(x: Float) {
        maxScale = x
    }

    constructor(context: Context) : super(context) {
        sharedConstructing(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        sharedConstructing(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        height = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        // Fit to screen.
        // Fit to screen.
        val scale: Float
        val scaleX = width / bmWidth
        val scaleY = height / bmHeight
        scale = Math.min(scaleX, scaleY)
        matrix.setScale(scale, scale)
        imageMatrix = matrix
        saveScale = 1f

        // Center the image

        // Center the image
        redundantYSpace = height as Float - scale * bmHeight as Float
        redundantXSpace = width as Float - scale * bmWidth as Float
        redundantYSpace /= 2f
        redundantXSpace /= 2f

        matrix.postTranslate(redundantXSpace, redundantYSpace)

        origWidth = width - 2 * redundantXSpace
        origHeight = height - 2 * redundantYSpace
        right = width * saveScale - width - 2 * redundantXSpace * saveScale
        bottom = height * saveScale - height - 2 * redundantYSpace * saveScale
        imageMatrix = matrix
    }

    inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
            mode = ZOOM
            return true
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            var mScaleFactor = Math.min(
                Math.max(.95f, detector.scaleFactor).toDouble(), 1.05
            ).toFloat()
            val origScale: Float = saveScale
            saveScale *= mScaleFactor
            if (saveScale > maxScale) {
                saveScale = maxScale
                mScaleFactor = maxScale / origScale
            } else if (saveScale < minScale) {
                saveScale = minScale
                mScaleFactor = minScale / origScale
            }
            right = width * saveScale - width - 2 * redundantXSpace * saveScale
            bottom = height * saveScale - height - 2 * redundantYSpace * saveScale
            if (origWidth * saveScale <= width || origHeight * saveScale <= height) {
                matrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2)
                if (mScaleFactor < 1) {
                    matrix.getValues(m)
                    val x: Float = m.get(Matrix.MTRANS_X)
                    val y: Float = m.get(Matrix.MTRANS_Y)
                    if (mScaleFactor < 1) {
                        if (Math.round(origWidth * saveScale) < width) {
                            if (y < -bottom) matrix.postTranslate(
                                0f,
                                -(y + bottom)
                            ) else if (y > 0) matrix.postTranslate(0f, -y)
                        } else {
                            if (x < -right) matrix.postTranslate(
                                -(x + right),
                                0f
                            ) else if (x > 0) matrix.postTranslate(-x, 0f)
                        }
                    }
                }
            } else {
                matrix.postScale(
                    mScaleFactor, mScaleFactor, detector.focusX,
                    detector.focusY
                )
                matrix.getValues(m)
                val x: Float = m.get(Matrix.MTRANS_X)
                val y: Float = m.get(Matrix.MTRANS_Y)
                if (mScaleFactor < 1) {
                    if (x < -right) matrix.postTranslate(
                        -(x + right),
                        0f
                    ) else if (x > 0) matrix.postTranslate(-x, 0f)
                    if (y < -bottom) matrix.postTranslate(
                        0f,
                        -(y + bottom)
                    ) else if (y > 0) matrix.postTranslate(0f, -y)
                }
            }
            return true
        }
    }
}