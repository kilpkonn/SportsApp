package ee.taltech.iti0213.sportsapp.db.domain

import ee.taltech.iti0213.sportsapp.track.TrackType

class User(
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val speedMode: Boolean = true,
    val defaultActivityType: TrackType = TrackType.UNKNOWN,
    val autoSync: Boolean = true
) {
}