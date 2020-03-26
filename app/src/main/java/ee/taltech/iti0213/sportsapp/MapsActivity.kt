package ee.taltech.iti0213.sportsapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import ee.taltech.iti0213.sportsapp.track.TrackData
import ee.taltech.iti0213.sportsapp.track.converters.Converter
import ee.taltech.iti0213.sportsapp.track.loaction.Checkpoint
import ee.taltech.iti0213.sportsapp.track.loaction.TrackLocation
import ee.taltech.iti0213.sportsapp.track.loaction.WayPoint


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName

        private const val DEFAULT_ZOOM_LEVEL = 13f
    }

    private val broadcastReceiver = InnerBroadcastReceiver()
    private val broadcastReceiverIntentFilter: IntentFilter = IntentFilter()

    private val wpMarkers = mutableMapOf<WayPoint, Marker>()
    private val cpMarkers = mutableMapOf<Checkpoint, Marker>()


    private var locationServiceActive = false
    private var lastLocation: LatLng? = null


    private lateinit var mMap: GoogleMap

    private lateinit var btnStartStop: ImageButton
    private lateinit var btnAddWP: ImageButton
    private lateinit var btnAddCP: ImageButton

    private lateinit var textViewLatitude: TextView
    private lateinit var textViewLongitude: TextView

    private lateinit var textViewTotalDistance: TextView
    private lateinit var textViewTotalTime: TextView
    private lateinit var textViewAverageSpeed: TextView

    private lateinit var textViewDistanceLastCP: TextView
    private lateinit var textViewDriftLastCP: TextView
    private lateinit var textViewAverageSpeedLastCP: TextView

    private lateinit var textViewDistanceLastWP: TextView
    private lateinit var textViewDriftLastWP: TextView
    private lateinit var textViewAverageSpeedLastWP: TextView


    // ============================================== MAIN ENTRY - ON CREATE =============================================
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // safe to call every time
        createNotificationChannel()

        if (!checkPermissions()) {
            requestPermissions()
        }

        broadcastReceiverIntentFilter.addAction(C.LOCATION_UPDATE_ACTION)
        broadcastReceiverIntentFilter.addAction(C.TRACK_STATS_UPDATE_ACTION)

        // Obtain the SupportMapFragment and get notified when the activity_maps is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnStartStop = findViewById(R.id.btn_startStop)
        btnAddWP = findViewById(R.id.btn_add_wp)
        btnAddCP = findViewById(R.id.btn_add_cp)
        textViewLatitude = findViewById(R.id.textViewLatitude)
        textViewLongitude = findViewById(R.id.textViewLongitude)

        btnAddWP.setOnClickListener { btnWPOnClick() }
        btnAddCP.setOnClickListener { btnCPOnClick() }
        btnStartStop.setImageResource(if (locationServiceActive) R.drawable.ic_pause_circle_outline_24px else R.drawable.ic_play_circle_outline_24px)

        textViewTotalDistance = findViewById(R.id.total_distance)
        textViewTotalTime = findViewById(R.id.duration)
        textViewAverageSpeed = findViewById(R.id.avg_speed)

        textViewDistanceLastCP = findViewById(R.id.distance_cp)
        textViewDriftLastCP = findViewById(R.id.drift_cp)
        textViewAverageSpeedLastCP = findViewById(R.id.avg_speed_cp)

        textViewDistanceLastWP = findViewById(R.id.distance_wp)
        textViewDriftLastWP = findViewById(R.id.drift_wp)
        textViewAverageSpeedLastWP = findViewById(R.id.avg_speed_wp)
    }
    // ================================================ MAPS CALLBACKS ===============================================

    override fun onMapReady(map: GoogleMap?) {
        mMap = map ?: return

        mMap.isMyLocationEnabled = true
    }


    // ============================================== LIFECYCLE CALLBACKS =============================================
    override fun onStart() {
        Log.d(TAG, "onStart")
        super.onStart()
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, broadcastReceiverIntentFilter)

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

    // ============================================== NOTIFICATION CHANNEL CREATION =============================================
    private fun createNotificationChannel() {
        // when on 8 Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                C.NOTIFICATION_CHANNEL,
                C.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )

            //.setShowBadge(false).setSound(null, null);

            channel.description = C.NOTIFICATION_CHANNEL_DESCRIPTION

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    // ============================================== PERMISSION HANDLING =============================================
    // Returns the current state of the permissions needed.
    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            Snackbar.make(
                findViewById(R.id.activity_main),
                C.SNAKBAR_REQUEST_FINE_LOCATION_ACCESS_TEXT,
                Snackbar.LENGTH_INDEFINITE
            )
                .setAction(C.SNAKBAR_REQUEST_FINE_LOCATION_CONFIRM_TEXT) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        C.REQUEST_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            Log.i(TAG, "Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                C.REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == C.REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.count() <= 0 -> {
                    // If user interaction was interrupted, the permission request is cancelled and
                    // you receive empty arrays.
                    Log.i(TAG, "User interaction was cancelled.")
                    Toast.makeText(
                        this,
                        C.TOAST_USER_INTERACTION_CANCELLED_TEXT,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    // Permission was granted.
                    Log.i(TAG, "Permission was granted")
                    Toast.makeText(this, C.TOAST_PERMISSION_GRANTED_TEXT, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Permission denied.
                    Snackbar.make(
                        findViewById(R.id.activity_main),
                        C.SNAKBAR_REQUEST_DENIED_TEXT,
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction(C.SNAKBAR_OPEN_SETTINGS_TEXT) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri: Uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID, null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }

    }


    // ============================================== CLICK HANDLERS =============================================
    fun buttonStartStopOnClick(view: View) {
        Log.d(TAG, "buttonStartStopOnClick. locationServiceActive: $locationServiceActive")
        // try to start/stop the background service

        if (locationServiceActive) {
            // stopping the service
            stopService(Intent(this, LocationService::class.java))
            btnStartStop.setImageResource(R.drawable.ic_play_circle_outline_24px)
        } else {
            if (Build.VERSION.SDK_INT >= 26) {
                // starting the FOREGROUND service
                // service has to display non-dismissable notification within 5 secs
                startForegroundService(Intent(this, LocationService::class.java))
            } else {
                startService(Intent(this, LocationService::class.java))
            }
            btnStartStop.setImageResource(R.drawable.ic_pause_circle_outline_24px)
        }

        locationServiceActive = !locationServiceActive
    }

    private fun btnWPOnClick() {
        Log.d(TAG, "buttonWPOnClick")
        val intent = Intent(C.NOTIFICATION_ACTION_WP)
        // TODO: wp from map
        intent.putExtra(C.NOTIFICATION_ACTION_WP_LAT_LNG, LatLng(59.43, 24.75))
        sendBroadcast(intent)
    }

    private fun btnCPOnClick() {
        Log.d(TAG, "buttonCPOnClick")
        sendBroadcast(Intent(C.NOTIFICATION_ACTION_CP))
    }

    // ============================================== BROADCAST RECEIVER =============================================
    private inner class InnerBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, intent!!.action!!)
            when (intent.action) {
                C.LOCATION_UPDATE_ACTION -> onLocationUpdate(intent)
                C.TRACK_STATS_UPDATE_ACTION -> onTrackDataUpdate(intent)
            }
        }

        // ------------------------------------- BROADCAST RECEIVER CALLBACKS ------------------------------------------

        private fun onLocationUpdate(intent: Intent) {
            if (!intent.hasExtra(C.LOCATION_UPDATE_ACTION_TRACK_LOCATION)) return

            val trackLocation =
                intent.getSerializableExtra(C.LOCATION_UPDATE_ACTION_TRACK_LOCATION) as TrackLocation
            textViewLatitude.text = trackLocation.latitude.toString()
            textViewLongitude.text = trackLocation.longitude.toString()
            val location = LatLng(trackLocation.latitude, trackLocation.longitude)

            // mMap.addMarker(MarkerOptions().position(location).title("Current loc"))
            if (lastLocation == null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM_LEVEL))
            } else {
                // mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                mMap.addPolyline(
                    PolylineOptions()
                        .add(lastLocation, location)
                        .width(5f)
                        .color(Color.RED)
                )
            }

            lastLocation = location
        }

        @SuppressLint("SetTextI18n") // Just to format numbers...
        private fun onTrackDataUpdate(intent: Intent) {
            if (!intent.hasExtra(C.TRACK_STATS_UPDATE_ACTION_DATA)) return

            val trackData =
                intent.getSerializableExtra(C.TRACK_STATS_UPDATE_ACTION_DATA) as TrackData

            textViewTotalDistance.text = "%.2f".format(trackData.totalDistance)
            textViewTotalTime.text = Converter.longToHhMmSs(trackData.totalTime)
            textViewAverageSpeed.text = "%.1f km/h".format(trackData.getAverageSpeedFromStart())

            textViewDistanceLastCP.text = "%.2f".format(trackData.distanceFromLastCP)
            textViewDriftLastCP.text = "%.2f".format(trackData.driftLastCP)
            textViewAverageSpeedLastCP.text =
                "%.1f km/h".format(trackData.getAverageSpeedFromLastCP())

            textViewDistanceLastWP.text = "%.2f".format(trackData.distanceFromLastWP)
            textViewDriftLastWP.text = "%.2f".format(trackData.driftLastWP)
            textViewAverageSpeedLastWP.text =
                "%.1f km/h".format(trackData.getAverageSpeedFromLastWP())

            val markersToRemove = mutableSetOf<Marker>()
            wpMarkers.forEach { entry -> markersToRemove.add(entry.value) }
            cpMarkers.forEach { entry -> markersToRemove.add(entry.value) }

            for (wp in trackData.waypoints) {
                val wpLatLng = LatLng(wp.latitude, wp.longitude)
                if (!wpMarkers.containsKey(wp)) {
                    wpMarkers[wp] = mMap.addMarker(
                        MarkerOptions()
                            .position(wpLatLng)
                            .snippet(wp.driftToWP.toString())
                    )
                } else {
                    val marker = wpMarkers[wp]
                    markersToRemove.remove(marker)
                    marker!!.snippet = wp.driftToWP.toString()
                }
            }

            for (cp in trackData.checkpoints) {
                val wpLatLng = LatLng(cp.latitude, cp.longitude)
                if (!cpMarkers.containsKey(cp)) {
                    cpMarkers[cp] = mMap.addMarker(MarkerOptions().position(wpLatLng))
                } else {
                    markersToRemove.remove(cpMarkers[cp])
                }
            }

            markersToRemove.forEach{ m -> m.remove()}
        }
    }
}
