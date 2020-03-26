package ee.taltech.iti0213.sportsapp

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
import ee.taltech.iti0213.sportsapp.track.TrackData
import ee.taltech.iti0213.sportsapp.track.converters.Converter
import ee.taltech.iti0213.sportsapp.track.loaction.TrackLocation


class LocationService : Service() {
    companion object {
        private val TAG = this::class.java.declaringClass!!.simpleName

        // The desired intervals for location updates. Inexact. Updates may be more or less frequent.
        private const val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 2000
        private const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2
    }

    private val broadcastReceiver = InnerBroadcastReceiver()
    private val broadcastReceiverIntentFilter: IntentFilter = IntentFilter()

    private val mLocationRequest: LocationRequest = LocationRequest()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var mLocationCallback: LocationCallback? = null

    private var track: Track? = null

    override fun onCreate() {
        Log.d(TAG, "onCreate")
        super.onCreate()

        broadcastReceiverIntentFilter.addAction(C.NOTIFICATION_ACTION_CP)
        broadcastReceiverIntentFilter.addAction(C.NOTIFICATION_ACTION_WP)
        broadcastReceiverIntentFilter.addAction(C.LOCATION_UPDATE_ACTION)

        registerReceiver(broadcastReceiver, broadcastReceiverIntentFilter)

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
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
        } catch (unlikely: SecurityException) {
            Log.e(TAG, "Lost location permission. Could not request updates. $unlikely")
        }
    }

    private fun onNewLocation(location: Location) {
        Log.i(TAG, "New location: $location")
        // First location

        track?.update(TrackLocation.fromLocation(location))

        val trackData = track?.getTrackData()
        showNotification(trackData)

        // broadcast new location to UI
        val intent = Intent(C.LOCATION_UPDATE_ACTION)
        intent.putExtra(
            C.LOCATION_UPDATE_ACTION_TRACK_LOCATION,
            TrackLocation.fromLocation(location)
        )
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

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
        //return super.onStartCommand(intent, flags, startId)
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
        val intentCp = Intent(C.NOTIFICATION_ACTION_CP)
        val intentWp = Intent(C.NOTIFICATION_ACTION_WP)
        if (track!= null && track!!.lastLocation != null) {
            val loc = LatLng(track!!.lastLocation!!.latitude, track!!.lastLocation!!.longitude)
            intentWp.putExtra(C.NOTIFICATION_ACTION_WP_LAT_LNG, loc)
        }

        val pendingIntentCp = PendingIntent.getBroadcast(this, 0, intentCp, 0)
        val pendingIntentWp = PendingIntent.getBroadcast(this, 0, intentWp, 0)

        val notifyView = RemoteViews(packageName, R.layout.track_control)

        notifyView.setOnClickPendingIntent(R.id.btn_add_cp, pendingIntentCp)
        notifyView.setOnClickPendingIntent(R.id.btn_add_wp, pendingIntentWp)

        notifyView.setTextViewText(R.id.total_distance, "%.2f".format(trackData?.totalDistance ?: 0f))
        notifyView.setTextViewText(R.id.duration, Converter.longToHhMmSs(trackData?.totalTime ?: 0))
        notifyView.setTextViewText(R.id.avg_speed, "%.1f km/h".format( trackData?.getAverageSpeedFromStart() ?: 0f))

        notifyView.setTextViewText(R.id.distance_cp, "%.2f".format(trackData?.distanceFromLastCP ?: 0f))
        notifyView.setTextViewText(R.id.drift_cp, "%.2f".format(trackData?.driftLastCP ?: 0f))
        notifyView.setTextViewText(R.id.avg_speed_cp, "%.1f km/h".format(trackData?.getAverageSpeedFromLastCP() ?: 0f))

        notifyView.setTextViewText(R.id.distance_wp, "%.2f".format(trackData?.distanceFromLastWP ?: 0f))
        notifyView.setTextViewText(R.id.drift_wp, "%.2f".format(trackData?.driftLastWP ?: 0f))
        notifyView.setTextViewText(R.id.avg_speed_wp, "%.1f km/h".format(trackData?.getAverageSpeedFromLastWP()))

        // construct and show notification
        val builder = NotificationCompat.Builder(applicationContext, C.NOTIFICATION_CHANNEL)
            .setSmallIcon(R.drawable.baseline_gps_fixed_24)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCustomBigContentView(notifyView)


        //builder.setContent(notifyView)

        // Super important, start as foreground service - ie android considers this as an active app.
        // Need visual reminder - notification.
        // must be called within 5 secs after service starts.
        startForeground(C.NOTIFICATION_ID, builder.build())
    }


    private inner class InnerBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d(TAG, intent!!.action!!)
            when (intent.action) {
                C.NOTIFICATION_ACTION_WP -> {
                    if (!intent.hasExtra(C.NOTIFICATION_ACTION_WP_LAT_LNG)) return
                    track?.addWayPoint(intent.getParcelableExtra(C.NOTIFICATION_ACTION_WP_LAT_LNG)!!)
                    showNotification(track?.getTrackData())
                }
                C.NOTIFICATION_ACTION_CP -> {
                    track?.addCheckpoint()
                    showNotification(track?.getTrackData())
                }
            }
        }

    }

}
