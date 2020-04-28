package ee.taltech.iti0213.sportsapp.track

import ee.taltech.iti0213.sportsapp.track.pracelable.DetailedTrackData
import ee.taltech.iti0213.sportsapp.track.pracelable.TrackData
import ee.taltech.iti0213.sportsapp.track.pracelable.TrackSyncData
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.Checkpoint
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.TrackLocation
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.WayPoint
import ee.taltech.iti0213.sportsapp.util.TrackUtils
import kotlin.math.max

class Track {

    var name: String = TrackUtils.generateNameIfNeeded("", TrackType.UNKNOWN)
    var type: TrackType = TrackType.UNKNOWN

    val track = mutableListOf<TrackLocation>()
    val waypoints = mutableListOf<WayPoint>()
    val checkpoints = mutableListOf<Checkpoint>()
    val pauses = mutableListOf<Int>()

    var runningDistance = 0.0
    var runningDistanceFromLastCP = 0.0
    var runningDistanceFromLastWP = 0.0

    var elevationGained = 0.0
    var lastAltitude = 0.0

    var maxSpeed = 0.0
    var minSpeed = 0.0

    var lastLocation: TrackLocation? = null

    var startTime = 0L
    var startTimeElapsed = 0L
    var lastCPTime = 0L
    var lastWPTime = 0L
    var currentTimeElapsed = 0L
    var movingTime = 0L

    fun update(location: TrackLocation) {
        if (lastLocation == null) {
            startTime = location.timestamp
            startTimeElapsed = location.elapsedTimestamp
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

            if (location.altitude != 0.0) {
                if (lastAltitude != 0.0)
                    elevationGained += max(
                        0.0,
                        location.altitude - lastAltitude - max(location.altitudeAccuracy, lastLocation?.altitudeAccuracy ?: 0f) / 2
                    )
                lastAltitude = location.altitude
            }

            if (pauses.isEmpty() || pauses.last() != track.size) {
                movingTime += location.elapsedTimestamp - currentTimeElapsed

                // No funny stuff with pauses
                if (3.6 * 1_000_000_000 * distance / (location.elapsedTimestamp - currentTimeElapsed) > maxSpeed) {
                    maxSpeed = (distance / (location.elapsedTimestamp - currentTimeElapsed)).toDouble() * 1_000_000_000 * 3.6
                }
                if (3.6 * 1_000_000_000 * distance / (location.elapsedTimestamp - currentTimeElapsed) < minSpeed) {
                    minSpeed = (distance / (location.elapsedTimestamp - currentTimeElapsed)).toDouble() * 1_000_000_000 * 3.6
                }
            }
        }
        currentTimeElapsed = location.elapsedTimestamp
        lastLocation = location
        track.add(location)
    }

    fun onPause() {
        pauses.add(track.size)
    }

    fun addCheckpoint(trackLocation: TrackLocation) {
        checkpoints.add(
            Checkpoint.fromLocation(
                trackLocation,
                runningDistance,
                checkpoints.lastOrNull()
            )
        )
        runningDistanceFromLastCP = 0.0
        lastCPTime = trackLocation.elapsedTimestamp
    }

    fun addWayPoint(wayPoint: WayPoint) {
        waypoints.add(wayPoint)
        runningDistanceFromLastWP = 0.0
        lastWPTime = wayPoint.timeAdded
    }

    fun removeWayPoint(wayPoint: WayPoint) {
        waypoints.find { wp -> wp == wayPoint }?.timeRemoved = wayPoint.timeRemoved
    }

    fun getTimeSinceStart(): Long {
        return currentTimeElapsed - startTimeElapsed
    }

    fun getTimeSinceLastCP(): Long {
        return currentTimeElapsed - lastCPTime
    }

    fun getTimeSinceLastWP(): Long {
        return currentTimeElapsed - lastWPTime
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

    fun getDrift(): Float {
        if (lastLocation == null) return 0f
        val start = track.first()
        return TrackLocation.calcDistanceBetween(
            lastLocation!!.latitude,
            lastLocation!!.longitude,
            start.latitude,
            start.longitude
        )
    }

    fun getTrackData(): TrackData {
        return TrackData(
            runningDistance,
            getTimeSinceStart(),
            runningDistanceFromLastCP,
            getTimeSinceLastCP(),
            runningDistanceFromLastWP,
            getTimeSinceLastWP(),
            getDriftToLastWP(),
            getDriftLastCP()
        )
    }

    fun getDetailedTrackData(): DetailedTrackData {
        val avgElevation = track.map { p -> p.altitude }.filter { a -> a != 0.0 }.average()
        val drift = if (track.size > 1) TrackLocation.calcDistanceBetween(track.first(), track.last()).toDouble() else 0.0
        return DetailedTrackData(name, type.value, getTimeSinceStart(), runningDistance, elevationGained, avgElevation, drift, checkpoints.size)
    }

    fun getTrackSyncData(since: Long): TrackSyncData {
        return TrackSyncData(
            track.filter { p -> p.elapsedTimestamp >= since },
            waypoints.filter { p -> p.timeAdded >= since },
            checkpoints.filter { p -> p.elapsedTimestamp >= since },
            pauses
        )
    }
}