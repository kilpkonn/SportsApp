package ee.taltech.iti0213.sportsapp.track.loaction

import android.location.Location

class Checkpoint(val latitude: Double, val longitude: Double, val timestamp: Long) {
    companion object {
        fun fromLocation(location: Location): Checkpoint {
            return Checkpoint(location.latitude, location.longitude, location.time)
        }
    }
}