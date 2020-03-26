package ee.taltech.iti0213.sportsapp.track.loaction

import android.location.Location
import android.os.Build
import java.io.Serializable

class TrackLocation(
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val accuracy: Float,
    val altitude: Double,
    val altitudeAccuracy: Float
) : Serializable {

    companion object {
        fun fromLocation(location: Location): TrackLocation {
            val verticalAccuracy =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) location.verticalAccuracyMeters else 0f

            return TrackLocation(
                location.latitude,
                location.longitude,
                location.time,
                location.accuracy,
                location.altitude,
                verticalAccuracy
            )
        }

        fun calcDistanceBetween(
            lat: Double,
            lng: Double,
            endLat: Double,
            endLng: Double
        ): Float {
            // distance, initial bearing, end bearing
            val distance = floatArrayOf(0f, 0f, 0f)
            Location.distanceBetween(lat, lng, endLat, endLng, distance)
            return distance[0]
        }
    }

    var currentCP: WayPoint? = null

}