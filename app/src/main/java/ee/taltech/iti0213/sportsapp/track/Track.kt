package ee.taltech.iti0213.sportsapp.track

import com.google.android.gms.maps.model.LatLng
import ee.taltech.iti0213.sportsapp.track.loaction.Checkpoint
import ee.taltech.iti0213.sportsapp.track.loaction.WayPoint
import ee.taltech.iti0213.sportsapp.track.loaction.TrackLocation

class Track {

    val track = mutableListOf<TrackLocation>()
    val waypoints = mutableListOf<WayPoint>()
    val checkpoints = mutableListOf<Checkpoint>()

    var runningDistance = 0.0
    var runningDistanceFromLastCP = 0.0

    var lastLocation: TrackLocation? = null

    var startTime = 0L
    var lastCPTime = 0L
    var currentTime = 0L

    fun update(location: TrackLocation) {
        if (lastLocation == null) {
            startTime = location.timestamp
        } else {
            val distance = TrackLocation.calcDistanceBetween(
                lastLocation!!.latitude,
                lastLocation!!.longitude,
                location.latitude,
                location.longitude
            )
            runningDistance += distance
            runningDistanceFromLastCP += distance
        }
        currentTime = location.timestamp

        track.add(location)
        waypoints.forEach { checkpoint -> checkpoint.update(location) }
    }

    fun addCheckpoint(location: TrackLocation) {
        checkpoints.add(
            Checkpoint.fromLocation(
                location,
                runningDistance,
                checkpoints.lastOrNull()
            )
        )
        runningDistanceFromLastCP = 0.0
        lastCPTime = location.timestamp
    }

    fun addWayPoint(latLng: LatLng) {
        waypoints.add(WayPoint(latLng.latitude, latLng.longitude))
    }

    fun removeWayPoint(wayPoint: WayPoint) {
        waypoints.remove(wayPoint)
    }

    fun getTimeSinceStart(): Long {
        return currentTime - startTime
    }

    fun getTimeSinceLastCP(): Long {
        return currentTime - lastCPTime
    }
}