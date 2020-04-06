package ee.taltech.iti0213.sportsapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import ee.taltech.iti0213.sportsapp.track.Track
import ee.taltech.iti0213.sportsapp.track.pracelable.TrackData
import ee.taltech.iti0213.sportsapp.track.converters.Converter
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.TrackLocation
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.WayPoint


class LocationService : Service() {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName

        // The desired intervals for location updates. Inexact. Updates may be more or less frequent.
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 2000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    private val broadcastReceiver = InnerBroadcastReceiver()
    private val broadcastReceiverIntentFilter: IntentFilter = IntentFilter()

    private val mLocationRequest: LocationRequest = LocationRequest()

    private var mLocationCallback: LocationCallback? = null

    private var track: Track? = null
    private var isAddingToTrack = false

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var notificationManager: NotificationManager


    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()

        broadcastReceiverIntentFilter.addAction(C.NOTIFICATION_ACTION_ADD_CP)
        broadcastReceiverIntentFilter.addAction(C.NOTIFICATION_ACTION_ADD_WP)
        broadcastReceiverIntentFilter.addAction(C.TRACK_ACTION_ADD_WP)
        broadcastReceiverIntentFilter.addAction(C.TRACK_ACTION_ADD_CP)
        broadcastReceiverIntentFilter.addAction(C.TRACK_ACTION_REMOVE_WP)
        broadcastReceiverIntentFilter.addAction(C.TRACK_SYNC_REQUEST)
        broadcastReceiverIntentFilter.addAction(C.TRACK_DETAIL_REQUEST)
        broadcastReceiverIntentFilter.addAction(C.TRACK_RESET)
        broadcastReceiverIntentFilter.addAction(C.TRACK_START)
        broadcastReceiverIntentFilter.addAction(C.TRACK_STOP)

        registerReceiver(broadcastReceiver, broadcastReceiverIntentFilter)

        notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                onNewLocation(locationResult.lastLocation)
            }
        }

        getLastLocation()

        createLocationRequest()
        requestLocationUpdates()

    }

    private fun requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates")

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    private fun onNewLocation(location: Location) {
        Log.i(TAG, "New location: $location")
        // First location

        if (isAddingToTrack) track?.update(TrackLocation.fromLocation(location))

        // broadcast trackData
        val trackData = track?.getTrackData()
        showNotification(trackData)
        val trackDataIntent = Intent(C.TRACK_STATS_UPDATE_ACTION)
        trackDataIntent.putExtra(C.TRACK_STATS_UPDATE_ACTION_DATA, trackData)

        // broadcast new location to UI
        val locationIntent = Intent(C.LOCATION_UPDATE_ACTION)
        locationIntent.putExtra(C.LOCATION_UPDATE_ACTION_TRACK_LOCATION, TrackLocation.fromLocation(location))
        LocalBroadcastManager.getInstance(this).sendBroadcast(locationIntent)
        LocalBroadcastManager.getInstance(this).sendBroadcast(trackDataIntent)

    }

    private fun createLocationRequest() {
        mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.maxWaitTime = UPDATE_INTERVAL_IN_MILLISECONDS
    }


    private fun getLastLocation() {
        try {
            mFusedLocationClient.lastLocation
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.w(TAG, "Get location task successful");
                            if (task.result != null) {
                                onNewLocation(task.result!!)
                            }
                        } else {
                            Log.w(TAG, "Failed to get location." + task.exception)
                        }
                    }
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. $unlikely")
        }
    }


    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()

        //stop location updates
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)

        // remove notifications
        NotificationManagerCompat.from(this).cancelAll()

        // don't forget to unregister broadcast receiver!!!!
        unregisterReceiver(broadcastReceiver)

        // broadcast stop to UI
        val intent = Intent(C.LOCATION_UPDATE_ACTION)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

    }

    override fun onLowMemory() {
        Log.d(TAG, "onLowMemory")
        super.onLowMemory()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")

        // set counters and locations to 0/null
        track = Track()

        showNotification(track?.getTrackData())

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind")
        TODO("not implemented")
    }

    override fun onRebind(intent: Intent?) {
        Log.d(TAG, "onRebind")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    fun showNotification(trackData: TrackData?) {
        val intentCp = Intent(C.NOTIFICATION_ACTION_ADD_CP)
        val intentWp = Intent(C.NOTIFICATION_ACTION_ADD_WP)
        if (track != null && track!!.lastLocation != null) {
            val locWP = WayPoint(track!!.lastLocation!!.latitude, track!!.lastLocation!!.longitude, track!!.lastLocation!!.elapsedTimestamp)
            intentWp.putExtra(C.NOTIFICATION_ACTION_ADD_WP_DATA, locWP)

            intentCp.putExtra(C.NOTIFICATION_ACTION_ADD_CP_DATA, track!!.lastLocation)
        }

        val notifyView = RemoteViews(packageName, R.layout.track_control)

        val pendingIntentCp = PendingIntent.getBroadcast(this, 0, intentCp, PendingIntent.FLAG_UPDATE_CURRENT)
        val pendingIntentWp = PendingIntent.getBroadcast(this, 0, intentWp, PendingIntent.FLAG_UPDATE_CURRENT)

        notifyView.setOnClickPendingIntent(R.id.btn_add_cp, pendingIntentCp)
        notifyView.setOnClickPendingIntent(R.id.btn_add_wp, pendingIntentWp)

        notifyView.setTextViewText(R.id.total_distance, Converter.distToString(trackData?.totalDistance ?: 0.0))
        notifyView.setTextViewText(R.id.duration, Converter.longToHhMmSs(trackData?.totalTime ?: 0))
        notifyView.setTextViewText(R.id.avg_speed, Converter.speedToString(trackData?.getAverageSpeedFromStart() ?: 0.0))

        notifyView.setTextViewText(R.id.distance_cp, Converter.distToString(trackData?.distanceFromLastCP ?: 0.0))
        notifyView.setTextViewText(R.id.drift_cp, Converter.distToString(trackData?.driftLastCP?.toDouble() ?: 0.0))
        notifyView.setTextViewText(R.id.avg_speed_cp, Converter.speedToString(trackData?.getAverageSpeedFromLastCP() ?: 0.0))

        notifyView.setTextViewText(R.id.distance_wp, Converter.distToString(trackData?.distanceFromLastWP ?: 0.0))
        notifyView.setTextViewText(R.id.drift_wp, Converter.distToString(trackData?.driftLastWP?.toDouble() ?: 0.0))
        notifyView.setTextViewText(R.id.avg_speed_wp, Converter.speedToString(trackData?.getAverageSpeedFromLastWP() ?: 0.0))
        notifyView.setViewPadding(R.id.track_control_bar, 0, 100, 1, 0)

        // construct and show notification
        val builder = NotificationCompat.Builder(applicationContext, C.NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.baseline_gps_fixed_24)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_NAVIGATION)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContent(notifyView)
                .setCustomContentView(notifyView)
                .setCustomBigContentView(notifyView)

        // TODO: Why is this not being displayed on my phone???

        // Super important, start as foreground service - ie android considers this as an active app.
        // Need visual reminder - notification.
        // must be called within 5 secs after service starts.
        startForeground(C.NOTIFICATION_ID, builder.build())
    }

    private fun sendTrackData(since: Long) {
        if (track == null) return
        val intent = Intent(C.TRACK_SYNC_RESPONSE)
        val data = track!!.getTrackSyncData(since)
        intent.putExtra(C.TRACK_SYNC_DATA, data)
        sendBroadcast(intent)
    }

    // ===================================== BROADCAST RECEIVER ========================================

    private inner class InnerBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, intent!!.action!!)
            when (intent.action) {
                C.NOTIFICATION_ACTION_ADD_WP -> {
                    if (!intent.hasExtra(C.NOTIFICATION_ACTION_ADD_WP_DATA)) return
                    track?.addWayPoint(intent.getParcelableExtra(C.NOTIFICATION_ACTION_ADD_WP_DATA)!!)
                }
                C.NOTIFICATION_ACTION_ADD_CP -> {
                    if (!intent.hasExtra(C.NOTIFICATION_ACTION_ADD_CP_DATA)) return
                    track?.addCheckpoint(intent.getParcelableExtra(C.NOTIFICATION_ACTION_ADD_CP_DATA)!!)
                }
                C.TRACK_ACTION_ADD_WP -> {
                    if (!intent.hasExtra(C.TRACK_ACTION_ADD_WP_DATA)) return
                    track?.addWayPoint(intent.getParcelableExtra(C.TRACK_ACTION_ADD_WP_DATA)!!)
                }
                C.TRACK_ACTION_ADD_CP -> {
                    if (!intent.hasExtra(C.TRACK_ACTION_ADD_CP_DATA)) return
                    track?.addCheckpoint(intent.getParcelableExtra(C.TRACK_ACTION_ADD_CP_DATA)!!)
                }
                C.TRACK_ACTION_REMOVE_WP -> {
                    if (!intent.hasExtra(C.TRACK_ACTION_REMOVE_WP_LOCATION)) return
                    track?.removeWayPoint(intent.getParcelableExtra(C.TRACK_ACTION_REMOVE_WP_LOCATION) as WayPoint)
                }
                C.TRACK_SYNC_REQUEST -> {
                    if (!intent.hasExtra(C.TRACK_SYNC_REQUEST_TIME)) return
                    sendTrackData(intent.getLongExtra(C.TRACK_SYNC_REQUEST_TIME, 0L))
                }
                C.TRACK_DETAIL_REQUEST -> onDetailTrackDataRequest()
                C.TRACK_RESET -> {
                    track = Track()
                    isAddingToTrack = false
                }
                C.TRACK_START -> isAddingToTrack = true
                C.TRACK_STOP -> isAddingToTrack = false
            }
        }

        // ----------------------------------- BROADCAST RECEIVER CALLBACKS --------------------------------
        private fun onDetailTrackDataRequest() {
            val intent = Intent(C.TRACK_DETAIL_RESPONSE)
            val data = track?.getDetailedTrackData()
            intent.putExtra(C.TRACK_DETAIL_DATA, data)
            sendBroadcast(intent)
        }
    }
}
