package ee.taltech.iti0213.sportsapp

import android.util.Log
import android.view.MotionEvent

class FlingDetector {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
    }

    var onFlingUp: Runnable? = null
    var onFlingDown: Runnable? = null
    var onFlingLeft: Runnable? = null
    var onFlingRight: Runnable? = null

    var flingMinSpeedX = 0.0000002f
    var flingMinSpeedY = 0.0000002f

    private var x1: Float? = 0f
    private var x2: Float? = 0f
    private var velocityX1: Float? = 0f
    private var velocityX2: Float? = 0f
    private var y1: Float? = 0f
    private var y2: Float? = 0f
    private var velocityY1: Float? = 0f
    private var velocityY2: Float? = 0f

    fun update(event: MotionEvent?) {
        val action = event?.action
        if (x1 == 0f) {
            x1 = event?.rawX
            return
        }
        if (y1 == 0f) {
            y1 = event?.rawY
            return
        }

        x2 = event?.rawX
        y2 = event?.rawY
        val distanceX: Float? = x1!! - x2!!
        val distanceY: Float? = y1!! - y2!!
        val time: Float? = event?.downTime?.toFloat()

        velocityX2 = velocityX1
        velocityY2 = velocityY1
        velocityX1 = distanceX!! / time!!
        velocityY1 = distanceY!! / time

        Log.d(TAG, "velocity x1: " + velocityX1?.toString())
        Log.d(TAG, "velocity x2: " + velocityX2?.toString())
        Log.d(TAG, "velocity y1: " + velocityY1?.toString())
        Log.d(TAG, "velocity y2: " + velocityY2?.toString())

        val velocityDeltaX: Float = velocityX2!! - velocityX1!!
        val velocityDeltaY: Float = velocityY2!! - velocityY1!!
        velocityX2 = 0f
        velocityY2 = 0f

        if (action == MotionEvent.ACTION_MOVE) {
            if (velocityX1!! > flingMinSpeedX) {
                // fling left
                Log.d(TAG, "fling left!")
                onFlingLeft?.run()
            }
            if (velocityX1!! < -flingMinSpeedX) {
                // fling right
                Log.d(TAG, "fling right!")
                onFlingRight?.run()
            }
            if (velocityY1!! > flingMinSpeedY) {
                // fling up
                Log.d(TAG, "fling up!")
                onFlingUp?.run()
            }
            if (velocityY1!! < -flingMinSpeedY) {
                // fling down
                Log.d(TAG, "fling down!")
                onFlingDown?.run()
            }
        }
        x1 = 0f
        y1 = 0f
    }
}