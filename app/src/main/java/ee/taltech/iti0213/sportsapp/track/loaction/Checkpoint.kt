package ee.taltech.iti0213.sportsapp.track.loaction

import android.location.Location

class Checkpoint(location: TrackLocation, distance: Double, lastCP: Checkpoint?) {
    val latitude: Double
    val longitude: Double
    val timestamp: Long
    val distanceSinceLastCP: Double = distance
    val driftFromLastCP: Double
    val timeSinceLastCP: Long

    init {
        this.latitude = location.latitude
        this.longitude = location.longitude
        this.timestamp = location.timestamp
        if (lastCP != null) {
            driftFromLastCP = calcDistanceBetween(latitude, longitude, lastCP.latitude, lastCP.longitude)
            timeSinceLastCP = lastCP.timestamp - location.timestamp
        } else {
            driftFromLastCP = 0.0
            timeSinceLastCP = 0
        }
    }
    companion object {
        fun fromLocation(location: TrackLocation, distance: Double, lastCP: Checkpoint?): Checkpoint {
            return Checkpoint(location, distance, lastCP)
        }
    }

    private fun calcDistanceBetween(
        lat: Double,
        lng: Double,
        endLat: Double,
        endLng: Double
    ): Double {
        val distance = floatArrayOf(0f, 0f, 0f)
        Location.distanceBetween(lat, lng, endLat, endLng, distance)
        return distance[0].toDouble()
    }
}