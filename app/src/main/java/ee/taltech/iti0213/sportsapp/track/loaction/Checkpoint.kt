package ee.taltech.iti0213.sportsapp.track.loaction

import android.location.Location
import java.io.Serializable

class Checkpoint(
    val latitude: Double,
    val longitude: Double,
    val timeAdded: Long,
    val previousCP: Checkpoint?
) : Serializable {

    var timeCompleted: Long? = null
    var distanceFromLastCP = 0.0
    var driftToLastCP = 0.0
    var timeSinceLastCP: Long = 0

    init {
        if (previousCP != null)
            driftToLastCP = calcDistanceBetween(
                latitude,
                longitude,
                previousCP.latitude,
                previousCP.longitude
            ).toDouble()
    }

    fun update(currentLocation: TrackLocation) {
        // distance, initial bearing, end bearing
        distanceFromLastCP += calcDistanceBetween(latitude, longitude, currentLocation.latitude, currentLocation.longitude)
        if (previousCP != null) {
            timeSinceLastCP = currentLocation.timestamp - previousCP.timeCompleted!!
        }
    }

    fun isCompleted(): Boolean {
        return timeCompleted != null
    }

    private fun calcDistanceBetween(
        lat: Double,
        lng: Double,
        endLat: Double,
        endLng: Double
    ): Float {
        val distance = floatArrayOf(0f, 0f, 0f)
        Location.distanceBetween(lat, lng, endLat, endLng, distance)
        return distance[0]
    }

}