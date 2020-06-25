package ee.taltech.iti0213.sportsapp.activity

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.component.spinner.adapter.OverviewTypeSpinnerAdapter
import ee.taltech.iti0213.sportsapp.detector.FlingDetector

class OverviewActivity : AppCompatActivity() {

    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
    }

    private lateinit var flingDetector: FlingDetector

    private lateinit var overviewTypeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        overviewTypeSpinner = findViewById(R.id.spinner_overview_type)

        flingDetector = FlingDetector(this)

        flingDetector.onFlingUp = Runnable { onFlingUp() }

        setUpSpinners()
    }


    // ======================================== FLING DETECTION =======================================

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        flingDetector.update(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun onFlingUp() {
        moveTaskToBack(true)
    }

    // ============================================= HELPER FUNCTIONS ===================================================

    private fun setUpSpinners() {
        val displayOptionAdapter = OverviewTypeSpinnerAdapter(this)
        overviewTypeSpinner.adapter = displayOptionAdapter
        overviewTypeSpinner.setSelection(0)
        overviewTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}