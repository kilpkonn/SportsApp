package ee.taltech.iti0213.sportsapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ee.taltech.iti0213.sportsapp.detector.FlingDetector
import ee.taltech.iti0213.sportsapp.track.converters.Converter
import ee.taltech.iti0213.sportsapp.track.pracelable.DetailedTrackData

class DetailActivity : AppCompatActivity() {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
    }

    private val broadcastReceiver = InnerBroadcastReceiver()
    private val broadcastReceiverIntentFilter: IntentFilter = IntentFilter()

    private lateinit var flingDetector: FlingDetector

    private lateinit var buttonReset: Button

    private lateinit var txtViewDuration: TextView
    private lateinit var txtViewAverageSpeed: TextView
    private lateinit var txtViewElevationGained: TextView
    private lateinit var txtViewDrift: TextView
    private lateinit var txtViewDistance: TextView
    private lateinit var txtViewElevation: TextView
    private lateinit var txtViewCheckpoints: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        flingDetector = FlingDetector(this)
        broadcastReceiverIntentFilter.addAction(C.TRACK_DETAIL_RESPONSE)  // Remove?

        //registerReceiver(broadcastReceiver, broadcastReceiverIntentFilter)

        buttonReset = findViewById(R.id.btn_reset)

        txtViewDuration = findViewById(R.id.duration)
        txtViewAverageSpeed = findViewById(R.id.avg_speed)
        txtViewElevationGained = findViewById(R.id.elevation_gained)
        txtViewDrift = findViewById(R.id.drift)
        txtViewDistance = findViewById(R.id.total_distance)
        txtViewElevation = findViewById(R.id.avg_elevation)
        txtViewCheckpoints = findViewById(R.id.checkpoints_count)

        flingDetector.onFlingDown = Runnable { onFlingDown() }
    }

    // ============================================== ON CLICK CALLBACKS ==============================================

    fun buttonResetOnClick(view: View) {
        val intent = Intent(C.TRACK_RESET)
        sendBroadcast(intent)
    }


    // ============================================== LIFECYCLE CALLBACKS =============================================
    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
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

    // ======================================== FLING DETECTION =======================================

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        flingDetector.update(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun onFlingDown() {
        finish()
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
        sendBroadcast(intent)
    }
}
