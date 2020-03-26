package ee.taltech.iti0213.sportsapp.track.loaction

import android.location.Location
import java.io.Serializable

class WayPoint(
    val latitude: Double,
    val longitude: Double
) : Serializable {

    var driftToWP = 0f

    fun update(currentLocation: TrackLocation) {
        driftToWP = TrackLocation.calcDistanceBetween(latitude, longitude, currentLocation.latitude, currentLocation.longitude)
    }

}