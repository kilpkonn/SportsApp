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
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ee.taltech.iti0213.sportsapp.detector.FlingDetector

class OptionsActivity : AppCompatActivity() {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName
    }

    private val broadcastReceiver = InnerBroadcastReceiver()
    private val broadcastReceiverIntentFilter: IntentFilter = IntentFilter()

    private lateinit var flingDetector: FlingDetector

    private lateinit var buttonReset: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)
        flingDetector = FlingDetector(this)
        broadcastReceiverIntentFilter.addAction(C.TRACK_RESET)  // Remove?

        registerReceiver(broadcastReceiver, broadcastReceiverIntentFilter)

        buttonReset = findViewById(R.id.btn_reset)

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
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }


    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
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
        //val intent = Intent(this, MapsActivity::class.java)
        //startActivity(intent)
    }

    // ====================================== BROADCAST RECEIVER ======================================

    private inner class InnerBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, intent!!.action!!)
            when (intent.action) {
                C.TRACK_RESET -> return
            }
        }

        // ----------------------------------- BROADCAST RECEIVER CALLBACKS ------------------------------

    }
}
