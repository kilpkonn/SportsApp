package ee.taltech.iti0213.sportsapp.activity

import android.content.*
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ee.taltech.iti0213.sportsapp.C
import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.component.spinner.ReplaySpinnerItems
import ee.taltech.iti0213.sportsapp.component.spinner.adapter.HistorySpinnerAdapter
import ee.taltech.iti0213.sportsapp.component.spinner.adapter.TrackTypeSpinnerAdapter
import ee.taltech.iti0213.sportsapp.db.TrackSummary
import ee.taltech.iti0213.sportsapp.detector.FlingDetector
import ee.taltech.iti0213.sportsapp.track.converters.Converter
import ee.taltech.iti0213.sportsapp.track.pracelable.DetailedTrackData
import ee.taltech.iti0213.sportsapp.view.TrackIconImageView

class DetailActivity : AppCompatActivity() {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName

        private const val ALERT_RESET_TITLE = "Reset track?"
        private const val ALERT_RESET_TEXT = "Last track data will be lost! Do you want to continue?"
        private const val ALERT_RESET_CANCEL_TEXT = "Cancel"
        private const val ALERT_RESET_RESET_TEXT = "Reset"

        private const val ALERT_SAVE_TITLE = "Save track?"
        private const val ALERT_SAVE_TEXT = "After saving current session will be cleared! Do you want to continue?"
        private const val ALERT_SAVE_CANCEL_TEXT = "Cancel"
        private const val ALERT_SAVE_RESET_TEXT = "Save"
    }

    private val broadcastReceiver = InnerBroadcastReceiver()
    private val broadcastReceiverIntentFilter: IntentFilter = IntentFilter()

    private lateinit var flingDetector: FlingDetector

    private lateinit var buttonSave: Button
    private lateinit var buttonReset: Button

    private lateinit var txtViewDuration: TextView
    private lateinit var txtViewAverageSpeed: TextView
    private lateinit var txtViewElevationGained: TextView
    private lateinit var txtViewDrift: TextView
    private lateinit var txtViewDistance: TextView
    private lateinit var txtViewElevation: TextView
    private lateinit var txtViewCheckpoints: TextView
    private lateinit var spinnerTrackType: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        flingDetector = FlingDetector(this)
        broadcastReceiverIntentFilter.addAction(C.TRACK_DETAIL_RESPONSE)  // Remove?

        buttonSave = findViewById(R.id.btn_save)
        buttonReset = findViewById(R.id.btn_reset)

        txtViewDuration = findViewById(R.id.duration)
        txtViewAverageSpeed = findViewById(R.id.avg_speed)
        txtViewElevationGained = findViewById(R.id.elevation_gained)
        txtViewDrift = findViewById(R.id.drift)
        txtViewDistance = findViewById(R.id.total_distance)
        txtViewElevation = findViewById(R.id.avg_elevation)
        txtViewCheckpoints = findViewById(R.id.checkpoints_count)
        spinnerTrackType = findViewById(R.id.spinner_track_type)
        setUpTypeSpinner(spinnerTrackType)

        flingDetector.onFlingDown = Runnable { onFlingDown() }
        flingDetector.onFlingLeft = Runnable { onFlingLeft() }
    }

    // ============================================== ON CLICK CALLBACKS ==============================================

    fun buttonSaveOnClick(view: View) {
        val alert = AlertDialog.Builder(this, R.style.AppCompatAlertInfoDialogStyle)
            .setTitle(ALERT_SAVE_TITLE)
            .setMessage(ALERT_SAVE_TEXT)
            .setPositiveButton(ALERT_SAVE_RESET_TEXT) { _, _ ->
                run {
                    val intent = Intent(C.TRACK_SAVE)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                }
            }
            .setNegativeButton(ALERT_SAVE_CANCEL_TEXT, null)
            .create()
        alert.show()
    }

    fun buttonResetOnClick(view: View) {
        val alert = AlertDialog.Builder(this, R.style.AppCompatAlertWarnDialogStyle)
            .setTitle(ALERT_RESET_TITLE)
            .setMessage(ALERT_RESET_TEXT)
            .setPositiveButton(ALERT_RESET_RESET_TEXT) { _, _ ->
                run {
                    val intent = Intent(C.TRACK_RESET)
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                }
            }
            .setNegativeButton(ALERT_RESET_CANCEL_TEXT, null)
            .create()
        alert.show()

    }


    // ============================================== LIFECYCLE CALLBACKS =============================================
    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
        //LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, broadcastReceiverIntentFilter)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, broadcastReceiverIntentFilter)
        requestData(true)
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }


    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
        requestData(false)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    override fun onRestart() {
        Log.d(TAG, "onRestart")
        super.onRestart()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_from_top,
            R.anim.slide_out_to_bottom
        );
    }

    // ======================================== FLING DETECTION =======================================

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        flingDetector.update(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun onFlingDown() {
        moveTaskToBack(false)
    }

    private fun onFlingLeft() {
        val intent = Intent(this, HistoryActivity::class.java)
        startActivity(intent)
        overridePendingTransition(
            R.anim.slide_in_from_right,
            R.anim.slide_out_to_left
        )
    }

    // ====================================== BROADCAST RECEIVER ======================================

    private inner class InnerBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, intent!!.action!!)
            when (intent.action) {
                C.TRACK_DETAIL_RESPONSE -> onTrackDetailResponse(intent)
            }
        }

        // ----------------------------------- BROADCAST RECEIVER CALLBACKS ------------------------------
        private fun onTrackDetailResponse(intent: Intent) {
            val data: DetailedTrackData = intent.getParcelableExtra(C.TRACK_DETAIL_DATA) ?: return
            txtViewDuration.text = Converter.longToHhMmSs(data.duration);
            txtViewAverageSpeed.text = Converter.speedToString(data.getSpeed())
            txtViewElevationGained.text = Converter.elevationToString(data.elevationGained)
            txtViewDrift.text = Converter.distToString(data.drift)
            txtViewDistance.text = Converter.distToString(data.distance)
            txtViewElevation.text = Converter.elevationToString(data.averageElevation)
            txtViewCheckpoints.text = data.checkpointsCount.toString()
        }
    }

    // ==================================== HELPER FUNCTIONS =============================================
    private fun requestData(keepBroadcasting: Boolean) {
        val intent = Intent(C.TRACK_DETAIL_REQUEST)
        intent.putExtra(C.TRACK_DETAIL_REQUEST_DATA, keepBroadcasting)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun setUpTypeSpinner(spinner: Spinner) {
        val displayOptionAdapter = TrackTypeSpinnerAdapter(this)

        spinner.adapter = displayOptionAdapter
        spinner.setSelection(0)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val intent = Intent(C.TRACK_SET_TYPE)
                intent.putExtra(C.TRACK_SET_TYPE_DATA, position)
                LocalBroadcastManager.getInstance(this@DetailActivity).sendBroadcast(intent)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
