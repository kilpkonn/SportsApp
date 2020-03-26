package ee.taltech.iti0213.sportsapp.track.loaction

import java.io.Serializable

class Checkpoint(location: TrackLocation, val distanceFromLastCP: Double, lastCP: Checkpoint?): Serializable {
    val latitude: Double
    val longitude: Double
    val timestamp: Long
    val driftFromLastCP: Float
    val timeSinceLastCP: Long

    init {
        this.latitude = location.latitude
        this.longitude = location.longitude
        this.timestamp = location.timestamp
        if (lastCP != null) {
            driftFromLastCP = TrackLocation.calcDistanceBetween(latitude, longitude, lastCP.latitude, lastCP.longitude)
            timeSinceLastCP = lastCP.timestamp - location.timestamp
        } else {
            driftFromLastCP = 0f
            timeSinceLastCP = 0
        }
    }
    companion object {
        fun fromLocation(location: TrackLocation, distance: Double, lastCP: Checkpoint?): Checkpoint {
            return Checkpoint(location, distance, lastCP)
        }
    }

}