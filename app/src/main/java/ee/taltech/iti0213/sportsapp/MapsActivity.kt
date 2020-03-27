package ee.taltech.iti0213.sportsapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.SensorManager.SENSOR_DELAY_GAME
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
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
import ee.taltech.iti0213.sportsapp.spinner.CompassMode
import ee.taltech.iti0213.sportsapp.spinner.DisplayMode
import ee.taltech.iti0213.sportsapp.track.TrackData
import ee.taltech.iti0213.sportsapp.track.converters.Converter
import ee.taltech.iti0213.sportsapp.track.loaction.TrackLocation
import ee.taltech.iti0213.sportsapp.track.loaction.WayPoint
import java.lang.Math.toDegrees


class MapsActivity : AppCompatActivity(), SensorEventListener, OnMapReadyCallback {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName

        private const val DEFAULT_ZOOM_LEVEL = 13f
        private const val FOCUSED_ZOOM_LEVEL = 16f
    }

    private val broadcastReceiver = InnerBroadcastReceiver()
    private val broadcastReceiverIntentFilter: IntentFilter = IntentFilter()

    private val wpMarkers = mutableMapOf<Marker, WayPoint>()

    private var locationServiceActive = false
    private var lastLocation: TrackLocation? = null

    private var isAddingWP = false
    private var displayMode = DisplayMode.CENTERED
    private var compassMode = CompassMode.IMAGE

    private var currentDegree = 0.0f
    private var lastAccelerometer = FloatArray(3)
    private var lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false


    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var magnetometer: Sensor

    private lateinit var mMap: GoogleMap

    private lateinit var btnStartStop: ImageButton
    private lateinit var btnAddWP: ImageButton
    private lateinit var btnAddCP: ImageButton

    //private lateinit var textViewLatitude: TextView
    //private lateinit var textViewLongitude: TextView

    private lateinit var textViewTotalDistance: TextView
    private lateinit var textViewTotalTime: TextView
    private lateinit var textViewAverageSpeed: TextView

    private lateinit var textViewDistanceLastCP: TextView
    private lateinit var textViewDriftLastCP: TextView
    private lateinit var textViewAverageSpeedLastCP: TextView

    private lateinit var textViewDistanceLastWP: TextView
    private lateinit var textViewDriftLastWP: TextView
    private lateinit var textViewAverageSpeedLastWP: TextView

    private lateinit var imageVieWCompass: TextView
    private lateinit var spinnerDisplayMode: Spinner
    private lateinit var spinnerCompassMode: Spinner


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

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)

        broadcastReceiverIntentFilter.addAction(C.LOCATION_UPDATE_ACTION)
        broadcastReceiverIntentFilter.addAction(C.TRACK_STATS_UPDATE_ACTION)

        // Obtain the SupportMapFragment and get notified when the activity_maps is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.retainInstance = true
        mapFragment.getMapAsync(this)

        btnStartStop = findViewById(R.id.btn_startStop)
        btnAddWP = findViewById(R.id.btn_add_wp)
        btnAddCP = findViewById(R.id.btn_add_cp)
        // textViewLatitude = findViewById(R.id.textViewLatitude)
        // textViewLongitude = findViewById(R.id.textViewLongitude)

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

        imageVieWCompass = findViewById(R.id.compass)
        spinnerDisplayMode = findViewById(R.id.spinner_display_mode)
        spinnerCompassMode = findViewById(R.id.spinner_compass_mode)

        setUpSpinners()
    }
    // ================================================ MAPS CALLBACKS ===============================================

    override fun onMapReady(map: GoogleMap?) {
        mMap = map ?: return
        mMap.setOnMapClickListener { latLng -> onMapClicked(latLng) }
        mMap.setOnMarkerClickListener { marker ->  onMarkerClicked(marker)}
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isCompassEnabled = false;
        mMap.uiSettings.isMapToolbarEnabled = false
    }

    private fun onMapClicked(latLng: LatLng?) {
        if (latLng == null) return

        val marker = mMap.addMarker(MarkerOptions().position(latLng))
        marker.showInfoWindow()

        val wp = WayPoint(latLng.latitude, latLng.longitude, System.currentTimeMillis())
        wpMarkers[marker] = wp

        val intent = Intent(C.NOTIFICATION_ACTION_ADD_WP)
        intent.putExtra(C.NOTIFICATION_ACTION_ADD_WP_DATA, wp)
        sendBroadcast(intent)
    }

    private fun onMarkerClicked(marker: Marker): Boolean {
        if (wpMarkers.containsKey(marker)) {
            marker.remove()
            val intent = Intent(C.TRACK_ACTION_REMOVE_WP)
            intent.putExtra(C.TRACK_ACTION_REMOVE_WP_LOCATION, wpMarkers[marker])
            sendBroadcast(intent)
            wpMarkers.remove(marker)
        }
        return true
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
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY_GAME)
        sensorManager.registerListener(this, magnetometer, SENSOR_DELAY_GAME)

    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
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

    // ================================================= COMPASS CALLBACKS ======================================================

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

    @SuppressLint("SetTextI18n")
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor === accelerometer) {
            lowPass(event.values, lastAccelerometer)
            lastAccelerometerSet = true
        } else if (event.sensor === magnetometer) {
            lowPass(event.values, lastMagnetometer)
            lastMagnetometerSet = true
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            val r = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, null, lastAccelerometer, lastMagnetometer)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                val degree = (toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360

                val rotateAnimation = RotateAnimation(
                    currentDegree,
                    -degree,
                    RELATIVE_TO_SELF, 0.5f,
                    RELATIVE_TO_SELF, 0.5f)
                rotateAnimation.duration = 1000
                rotateAnimation.fillAfter = true

                if (compassMode == CompassMode.IMAGE) {
                    imageVieWCompass.startAnimation(rotateAnimation)
                } else if (compassMode == CompassMode.NUMERIC){
                    imageVieWCompass.text = "%.1f".format(degree)
                }
                currentDegree = -degree
            }
        }
    }

    fun lowPass(input: FloatArray, output: FloatArray) {
        val alpha = 0.05f
        for (i in input.indices) {
            output[i] = output[i] + alpha * (input[i] - output[i])
        }
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

            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
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
        isAddingWP = !isAddingWP // Toggle
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
                intent.getParcelableExtra(C.LOCATION_UPDATE_ACTION_TRACK_LOCATION) as TrackLocation
            // textViewLatitude.text = trackLocation.latitude.toString()
            // textViewLongitude.text = trackLocation.longitude.toString()
            val location = LatLng(trackLocation.latitude, trackLocation.longitude)

            // mMap.addMarker(MarkerOptions().position(location).title("Current loc"))
            if (lastLocation == null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM_LEVEL))
            } else {
                val lastLoc = LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
                mMap.addPolyline(
                    PolylineOptions()
                        .add(lastLoc, location)
                        .width(5f)
                        .color(Color.RED)
                )
                if (displayMode == DisplayMode.CENTERED)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, FOCUSED_ZOOM_LEVEL)) // TODO: smoother
            }

            lastLocation = trackLocation
        }

        @SuppressLint("SetTextI18n") // Just to format numbers...
        private fun onTrackDataUpdate(intent: Intent) {
            if (!intent.hasExtra(C.TRACK_STATS_UPDATE_ACTION_DATA)) return

            val trackData =
                intent.getParcelableExtra(C.TRACK_STATS_UPDATE_ACTION_DATA) as TrackData

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

            if (lastLocation != null) {
                for ((marker, wp) in wpMarkers.entries) {
                    marker.title = "%.1f m".format(wp.getDriftToWP(lastLocation!!))
                    marker.showInfoWindow()
                }
            }
        }
    }

    // ======================================= HELPER FUNCTIONS ======================================
    private fun setUpSpinners() {
        // Create an ArrayAdapters
        val displayOptionAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            DisplayMode.OPTIONS
        )
        spinnerDisplayMode.adapter = displayOptionAdapter
        spinnerDisplayMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                displayMode = DisplayMode.OPTIONS[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val compassOptionAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            CompassMode.OPTIONS
        )
        spinnerCompassMode.adapter = compassOptionAdapter
        spinnerCompassMode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                compassMode = CompassMode.OPTIONS[position]
                when (compassMode) {
                    CompassMode.IMAGE -> {
                        imageVieWCompass.visibility = View.VISIBLE
                        imageVieWCompass.text = ""
                        imageVieWCompass.setBackgroundResource(R.drawable.ic_compass)
                    }
                    CompassMode.NUMERIC -> {
                        imageVieWCompass.visibility = View.INVISIBLE
                        val animation = RotateAnimation(
                            180f,
                            0f,
                            RELATIVE_TO_SELF, 0.5f,
                            RELATIVE_TO_SELF, 0.5f)
                        animation.fillAfter = true
                        animation.duration = 1000
                        imageVieWCompass.startAnimation(animation)
                        imageVieWCompass.background = null
                    }
                    CompassMode.NONE -> {
                        imageVieWCompass.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}
