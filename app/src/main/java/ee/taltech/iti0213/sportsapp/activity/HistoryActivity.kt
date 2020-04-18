package ee.taltech.iti0213.sportsapp.activity

import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.db.DatabaseHelper
import ee.taltech.iti0213.sportsapp.detector.FlingDetector
import ee.taltech.iti0213.sportsapp.track.converters.Converter
import ee.taltech.iti0213.sportsapp.view.TrackIconImageView

class HistoryActivity : AppCompatActivity() {

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

                trackView.findViewById<Button>(R.id.btn_blue).background.setTint(0xFF0000AC.toInt())
                trackView.findViewById<Button>(R.id.btn_green).background.setTint(0xFF005200.toInt())
                trackView.findViewById<Button>(R.id.btn_orange).background.setTint(0xFFCE4D00.toInt())
                trackView.findViewById<Button>(R.id.btn_purple).background.setTint(0xFF5C0095.toInt())
                trackView.findViewById<Button>(R.id.btn_none).background.setTint(0xFFAAAAAA.toInt())

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
}
