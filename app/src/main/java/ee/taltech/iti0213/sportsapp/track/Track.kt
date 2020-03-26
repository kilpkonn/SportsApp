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
    var runningDistanceFromLastWP = 0.0

    var lastLocation: TrackLocation? = null

    var startTime = 0L
    var lastCPTime = 0L
    var lastWPTime = 0L
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
            runningDistanceFromLastWP += distance
        }
        currentTime = location.timestamp
        lastLocation = location
        track.add(location)
        waypoints.forEach { checkpoint -> checkpoint.update(location) }
    }

    fun addCheckpoint() {
        checkpoints.add(
            Checkpoint.fromLocation(
                track.last(),
                runningDistance,
                checkpoints.lastOrNull()
            )
        )
        runningDistanceFromLastCP = 0.0
        lastCPTime = track.last().timestamp
    }

    fun addWayPoint(latLng: LatLng) {
        waypoints.add(WayPoint(latLng.latitude, latLng.longitude))
        runningDistanceFromLastWP = 0.0
        lastWPTime = track.last().timestamp
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

    fun getTimeSinceLastWP(): Long {
        return currentTime - lastWPTime
    }

    fun getDriftLastCP(): Float {
        if (lastLocation == null) return 0f
        val cp = checkpoints.lastOrNull() ?: return 0f
        return TrackLocation.calcDistanceBetween(
            lastLocation!!.latitude,
            lastLocation!!.longitude,
            cp.latitude,
            cp.longitude
        )
    }

    fun getDriftToLastWP(): Float {
        if (lastLocation == null) return 0f
        val wp = waypoints.lastOrNull() ?: return 0f
        return TrackLocation.calcDistanceBetween(
            lastLocation!!.latitude,
            lastLocation!!.longitude,
            wp.latitude,
            wp.longitude
        )
    }

    fun getAverageSpeedFromStart(): Double {
        val speed = (runningDistance / getTimeSinceStart()) * 1000 * 3.6
        return if (!speed.isNaN()) speed  else 0.0
    }

    fun getAverageSpeedFromLastCP(): Double {
        val speed = (runningDistanceFromLastCP / getTimeSinceLastCP()) * 1000 * 3.6
        return if (!speed.isNaN()) speed  else 0.0
    }

    fun getAverageSpeedFromLastWP(): Double {
        val speed = (runningDistanceFromLastWP / getTimeSinceLastWP()) * 1000 * 3.6
        return if (!speed.isNaN()) speed  else 0.0
    }
}