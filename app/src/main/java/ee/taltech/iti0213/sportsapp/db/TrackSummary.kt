package ee.taltech.iti0213.sportsapp.db

class TrackSummary(
    val trackId: Long,
    val name: String,
    val type: Int,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val durationMoving: Long,
    val distance: Double,
    val drift: Double,
    val elevationGained: Double,
    val maxSpeed: Double,
    val minSpeed: Double
) {

}