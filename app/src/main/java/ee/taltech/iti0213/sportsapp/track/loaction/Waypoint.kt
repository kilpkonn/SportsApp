package ee.taltech.iti0213.sportsapp.track.loaction

import android.location.Location
import java.io.Serializable

class Waypoint(
    val latitude: Double,
    val longitude: Double
) : Serializable {

    var driftToWP = 0f

    fun update(currentLocation: TrackLocation) {
        // distance, initial bearing, end bearing
        driftToWP = calcDistanceBetween(latitude, longitude, currentLocation.latitude, currentLocation.longitude)
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