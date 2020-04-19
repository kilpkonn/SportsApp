package ee.taltech.iti0213.sportsapp.activity

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.component.spinner.adapter.HistorySpinnerAdapter
import ee.taltech.iti0213.sportsapp.db.DatabaseHelper
import ee.taltech.iti0213.sportsapp.db.TrackSummary
import ee.taltech.iti0213.sportsapp.detector.FlingDetector
import ee.taltech.iti0213.sportsapp.track.converters.Converter
import ee.taltech.iti0213.sportsapp.view.TrackIconImageView

class HistoryActivity : AppCompatActivity() {

    companion object {
        private const val ALERT_DELETE_TITLE = "Delete track?"
        private const val ALERT_DELETE_TEXT = "Do you want to permanently delete track?"
        private const val ALERT_DELETE_CANCEL_TEXT = "Cancel"
        private const val ALERT_DELETE_DELETE_TEXT = "Delete"
    }

    private val databaseHelper: DatabaseHelper = DatabaseHelper(this)

    private lateinit var flingDetector: FlingDetector

    private lateinit var scrollViewHistory: ScrollView
    private lateinit var linearLayoutScrollContent: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // scrollViewHistory = findViewById(R.id.scroll_history)
        linearLayoutScrollContent = findViewById(R.id.linear_scroll)

        databaseHelper.readTracksSummary(0, 999).forEach { track ->
            run {
                val trackView = layoutInflater.inflate(R.layout.track_history_item, linearLayoutScrollContent, false)
                trackView.findViewById<TextView>(R.id.track_name).text = track.name
                trackView.findViewById<TextView>(R.id.distance).text = Converter.distToString(track.distance)
                trackView.findViewById<TextView>(R.id.duration).text = Converter.longToHhMmSs(track.durationMoving)
                trackView.findViewById<TextView>(R.id.elevation_gained).text = Converter.distToString(track.elevationGained)
                trackView.findViewById<TextView>(R.id.avg_speed).text =
                    Converter.speedToString(track.distance / track.durationMoving * 1_000_000_000 * 3.6)
                trackView.findViewById<TextView>(R.id.max_speed).text = Converter.speedToString(track.maxSpeed)
                trackView.findViewById<TextView>(R.id.drift).text = Converter.distToString(track.drift)

                val trackImage = trackView.findViewById<TrackIconImageView>(R.id.track_image)
                trackImage.track = databaseHelper.readTrackLocations(track.trackId)

                val deleteButton = trackView.findViewById<Button>(R.id.btn_delete)
                deleteButton.setOnClickListener {
                    onDeleteClicked(track, trackView)
                }

                val renameButton = trackView.findViewById<Button>(R.id.btn_rename)
                // TODO: rename

                val replaySpinner = trackView.findViewById<Spinner>(R.id.spinner_replay)
                setUpReplaySpinner(replaySpinner)

                val optionsView = trackView.findViewById<ConstraintLayout>(R.id.options)
                trackView.setOnClickListener {
                    if (optionsView.visibility == View.GONE) {
                        optionsView.visibility = View.VISIBLE
                    } else {
                        optionsView.visibility = View.GONE
                    }
                }

                linearLayoutScrollContent.addView(trackView)
            }
        }

        flingDetector = FlingDetector(this)
        flingDetector.onFlingRight = Runnable { onFlingRight() }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(
            R.anim.slide_in_from_left,
            R.anim.slide_out_to_right
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        databaseHelper.close()
    }

    // ============================= FLING DETECTOR CALLBACKS ==========================

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        flingDetector.update(ev)
        return super.dispatchTouchEvent(ev)
    }

    fun onFlingRight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else finish()
    }

    // ================================= HELPER FUNCTION =================================

    private fun setUpReplaySpinner(spinner: Spinner) {
        val displayOptionAdapter = HistorySpinnerAdapter(this)

        spinner.adapter = displayOptionAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // TODO: display track
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun onDeleteClicked(track: TrackSummary, trackView: View) {
        val alert = AlertDialog.Builder(this, R.style.AppCompatAlertWarnDialogStyle)
            .setTitle(ALERT_DELETE_TITLE)
            .setMessage(ALERT_DELETE_TEXT)
            .setPositiveButton(ALERT_DELETE_DELETE_TEXT) { _, _ ->
                run {
                    databaseHelper.deleteTrack(track.trackId)
                    linearLayoutScrollContent.removeView(trackView)
                }
            }
            .setNegativeButton(ALERT_DELETE_CANCEL_TEXT, null)
            .create()
        alert.show()
    }
}
