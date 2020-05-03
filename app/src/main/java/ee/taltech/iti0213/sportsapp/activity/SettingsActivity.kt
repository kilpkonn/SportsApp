package ee.taltech.iti0213.sportsapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.detector.FlingDetector

class SettingsActivity : AppCompatActivity() {

    private lateinit var flingDetector: FlingDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        flingDetector = FlingDetector(this)

        flingDetector.onFlingLeft = Runnable { onFlingLeft() }
    }

    // ======================================= LIFECYCLE CALLBACKS ====================================

    override fun onStart() {
        super.onStart()

        overridePendingTransition(
            R.anim.slide_in_from_left,
            R.anim.slide_out_to_right
        )
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(
            R.anim.slide_in_from_right,
            R.anim.slide_out_to_left
        )
    }

    // ======================================== FLING DETECTION =======================================

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        flingDetector.update(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun onFlingLeft() {
        moveTaskToBack(true)
    }
}
