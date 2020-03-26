package ee.taltech.iti0213.sportsapp.track

import ee.taltech.iti0213.sportsapp.track.loaction.Checkpoint
import ee.taltech.iti0213.sportsapp.track.loaction.Waypoint
import ee.taltech.iti0213.sportsapp.track.loaction.TrackLocation

class Track {

    val track = mutableListOf<TrackLocation>()
    val waypoints = mutableListOf<Waypoint>()
    val checkpoints = mutableListOf<Checkpoint>()

    var runningDistance = 0.0

    fun update(location: TrackLocation) {
        track.add(location)
        waypoints.filter { checkpoint -> !checkpoint.isCompleted() }
            .forEach { checkpoint -> checkpoint.update(location) }
    }

    fun addCheckpoint(location: TrackLocation) {
        checkpoints.add(Checkpoint.fromLocation(location, runningDistance, checkpoints.lastOrNull()))
    }
}