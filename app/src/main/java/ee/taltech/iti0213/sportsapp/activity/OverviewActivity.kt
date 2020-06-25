package ee.taltech.iti0213.sportsapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.component.spinner.adapter.TrackTypeSpinnerAdapter
import ee.taltech.iti0213.sportsapp.track.TrackType

class OverviewActivity : AppCompatActivity() {

    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
    }

    private lateinit var overviewTypeSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)

        overviewTypeSpinner = findViewById(R.id.spinner_overview_type)
    }

    // ============================================= HELPER FUNCTIONS ===================================================

    private fun setUpSpinners() {
        val displayOptionAdapter = TrackTypeSpinnerAdapter(this)
        overviewTypeSpinner.adapter = displayOptionAdapter
        overviewTypeSpinner.setSelection(0)
        overviewTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}