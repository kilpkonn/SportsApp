package ee.taltech.iti0213.sportsapp.track

import ee.taltech.iti0213.sportsapp.track.loaction.Checkpoint
import ee.taltech.iti0213.sportsapp.track.loaction.TrackLocation

class Track {

    val track = mutableListOf<TrackLocation>()
    val checkpoints = mutableListOf<Checkpoint>()

    fun update(location: TrackLocation) {
        track.add(location)
        checkpoints.filter { checkpoint -> !checkpoint.isCompleted() }
            .forEach { checkpoint -> checkpoint.update(location) }
    }
}