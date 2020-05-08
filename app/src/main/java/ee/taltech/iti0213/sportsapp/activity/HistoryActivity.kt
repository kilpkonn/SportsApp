package ee.taltech.iti0213.sportsapp.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.textfield.TextInputLayout
import ee.taltech.iti0213.sportsapp.C
import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.component.imageview.TrackTypeIcons
import ee.taltech.iti0213.sportsapp.component.spinner.ReplaySpinnerItems
import ee.taltech.iti0213.sportsapp.component.spinner.adapter.HistorySpinnerAdapter
import ee.taltech.iti0213.sportsapp.db.domain.TrackSummary
import ee.taltech.iti0213.sportsapp.db.repository.TrackLocationsRepository
import ee.taltech.iti0213.sportsapp.db.repository.TrackSummaryRepository
import ee.taltech.iti0213.sportsapp.detector.FlingDetector
import ee.taltech.iti0213.sportsapp.track.TrackType
import ee.taltech.iti0213.sportsapp.track.converters.Converter
import ee.taltech.iti0213.sportsapp.component.view.TrackIconImageView
import ee.taltech.iti0213.sportsapp.db.domain.User
import ee.taltech.iti0213.sportsapp.db.repository.UserRepository

class HistoryActivity : AppCompatActivity() {

    companion object {
        private const val BUNDLE_SELECTED_REPLAYS = "selected_replays"

        private const val ALERT_DELETE_TITLE = "Delete track?"
        private const val ALERT_DELETE_TEXT = "Do you want to permanently delete track?"
        private const val ALERT_DELETE_CANCEL_TEXT = "Cancel"
        private const val ALERT_DELETE_DELETE_TEXT = "Delete"
    }

    private val trackSummaryRepository = TrackSummaryRepository.open(this)
    private val trackLocationsRepository = TrackLocationsRepository.open(this)

    private val userRepository = UserRepository.open(this)

    private var user: User? = null

    private var selectedItems = hashMapOf<Long, Int>()

    private lateinit var flingDetector: FlingDetector

    private lateinit var scrollViewHistory: ScrollView
    private lateinit var linearLayoutScrollContent: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        selectedItems = savedInstanceState?.getSerializable(BUNDLE_SELECTED_REPLAYS) as? HashMap<Long, Int> ?: hashMapOf()

        // scrollViewHistory = findViewById(R.id.scroll_history)
        linearLayoutScrollContent = findViewById(R.id.linear_scroll)

        flingDetector = FlingDetector(this)
        flingDetector.onFlingRight = Runnable { onFlingRight() }
    }

    override fun onStart() {
        super.onStart()

        user = userRepository.readUser()

        overridePendingTransition(
            R.anim.slide_in_from_right,
            R.anim.slide_out_to_left
        )

        linearLayoutScrollContent.removeAllViews()
        trackSummaryRepository.readTrackSummaries(0, 999).forEach { track ->
            run {
                val trackView = layoutInflater.inflate(R.layout.track_history_item, linearLayoutScrollContent, false)
                trackView.findViewById<TextView>(R.id.track_name).text = track.name
                trackView.findViewById<ImageView>(R.id.track_type_icon)
                    .setImageResource(TrackTypeIcons.getIcon(TrackType.fromInt(track.type)!!))
                trackView.findViewById<TextView>(R.id.distance).text = Converter.distToString(track.distance)
                trackView.findViewById<TextView>(R.id.duration).text = Converter.longToHhMmSs(track.durationMoving)
                trackView.findViewById<TextView>(R.id.elevation_gained).text = Converter.distToString(track.elevationGained)
                trackView.findViewById<TextView>(R.id.avg_speed).text =
                    Converter.speedToString(track.distance / track.durationMoving * 1_000_000_000 * 3.6, user?.speedMode ?: true)
                trackView.findViewById<TextView>(R.id.max_speed).text = Converter.speedToString(track.maxSpeed, user?.speedMode ?: true)
                trackView.findViewById<TextView>(R.id.drift).text = Converter.distToString(track.drift)

                val trackImage = trackView.findViewById<TrackIconImageView>(R.id.track_image)
                trackImage.track = trackLocationsRepository.readTrackLocations(track.trackId, 0L, Long.MAX_VALUE)
                trackImage.maxSpeed = track.maxSpeed

                val deleteButton = trackView.findViewById<Button>(R.id.btn_delete)
                deleteButton.setOnClickListener {
                    onDeleteClicked(track, trackView)
                }

                val renameButton = trackView.findViewById<Button>(R.id.btn_rename)
                renameButton.setOnClickListener { onRenameClicked(track, trackView) }

                val replaySpinner = trackView.findViewById<Spinner>(R.id.spinner_replay)
                setUpReplaySpinner(replaySpinner, track, trackImage)

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
    }

    override fun onDestroy() {
        super.onDestroy()
        trackSummaryRepository.close()
        trackLocationsRepository.close()
        userRepository.close()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(
            R.anim.slide_in_from_left,
            R.anim.slide_out_to_right
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(BUNDLE_SELECTED_REPLAYS, selectedItems)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        selectedItems = savedInstanceState.getSerializable(BUNDLE_SELECTED_REPLAYS) as? HashMap<Long, Int> ?: hashMapOf()
    }

    // ============================= FLING DETECTOR CALLBACKS ==========================

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        flingDetector.update(ev)
        return super.dispatchTouchEvent(ev)
    }

    fun onFlingRight() {
        moveTaskToBack(true)
    }

    // ================================= HELPER FUNCTION =================================

    private fun setUpReplaySpinner(spinner: Spinner, track: TrackSummary, trackIcon: TrackIconImageView) {
        val displayOptionAdapter = HistorySpinnerAdapter(this)

        spinner.adapter = displayOptionAdapter
        spinner.setSelection(selectedItems[track.trackId] ?: 0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedItems[track.trackId] = position
                val intent = Intent(C.TRACK_SET_RABBIT)
                intent.putExtra(C.TRACK_SET_RABBIT_NAME, ReplaySpinnerItems.OPTIONS[position])
                intent.putExtra(C.TRACK_SET_RABBIT_VALUE, track.trackId)
                if (ReplaySpinnerItems.OPTIONS[position] != ReplaySpinnerItems.NONE) {
                    trackIcon.color = ReplaySpinnerItems.COLORS[ReplaySpinnerItems.OPTIONS[position]]!!.toInt()
                } else {
                    trackIcon.color = Color.RED
                }
                trackIcon.colorMax = ReplaySpinnerItems.COLORS_MAX_SPEED[ReplaySpinnerItems.OPTIONS[position]]!!.toInt()
                trackIcon.invalidate()
                LocalBroadcastManager.getInstance(this@HistoryActivity).sendBroadcast(intent)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        if (ReplaySpinnerItems.OPTIONS[selectedItems[track.trackId] ?: 0] != ReplaySpinnerItems.NONE) {
            trackIcon.color = ReplaySpinnerItems.COLORS[ReplaySpinnerItems.OPTIONS[selectedItems[track.trackId] ?: 0]]!!.toInt()
        } else {
            trackIcon.color = Color.RED
        }
        trackIcon.colorMax = ReplaySpinnerItems.COLORS_MAX_SPEED[ReplaySpinnerItems.OPTIONS[selectedItems[track.trackId] ?: 0]]!!.toInt()
        trackIcon.invalidate()
    }

    private fun onDeleteClicked(track: TrackSummary, trackView: View) {
        val alert = AlertDialog.Builder(this, R.style.AppCompatAlertWarnDialogStyle)
            .setTitle(ALERT_DELETE_TITLE)
            .setMessage(ALERT_DELETE_TEXT)
            .setPositiveButton(ALERT_DELETE_DELETE_TEXT) { _, _ ->
                run {
                    trackSummaryRepository.deleteTrack(track.trackId)
                    linearLayoutScrollContent.removeView(trackView)
                }
            }
            .setNegativeButton(ALERT_DELETE_CANCEL_TEXT, null)
            .create()
        alert.show()
    }

    private fun onRenameClicked(track: TrackSummary, trackView: View) {
        val textInputLayout = TextInputLayout(this)
        textInputLayout.setPadding(19, 0, 19, 0)
        val input = EditText(this)
        input.setTextColor(0xFFb8e7ff.toInt())
        textInputLayout.addView(input)

        val alert = AlertDialog.Builder(this, R.style.AppCompatAlertInfoDialogStyle)
            .setTitle("Rename track")
            .setView(textInputLayout)
            .setPositiveButton("Rename") { dialog, _ ->
                // do some thing with input.text
                track.name = input.text.toString()
                trackView.findViewById<TextView>(R.id.track_name).text = track.name
                trackSummaryRepository.updateTrackName(track)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.create()

        alert.show()
    }
}
