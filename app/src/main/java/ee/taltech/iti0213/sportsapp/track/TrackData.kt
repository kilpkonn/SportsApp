package ee.taltech.iti0213.sportsapp.track

import java.io.Serializable

class TrackData constructor(
    val totalDistance: Double,
    val totalTime: Long,
    val distanceFromLastCP: Double,
    val timeFromLastCP: Long,
    val distanceFromLastWP: Double,
    val timeFromLastWP: Long,
    val driftLastWP: Float,
    val driftLastCP: Float
) : Serializable {

    fun getAverageSpeedFromStart(): Double {
        val speed = (totalDistance / totalTime) * 1000 * 3.6
        return if (!speed.isNaN()) speed else 0.0
    }

    fun getAverageSpeedFromLastCP(): Double {
        val speed = (distanceFromLastCP / timeFromLastCP) * 1000 * 3.6
        return if (!speed.isNaN()) speed else 0.0
    }

    fun getAverageSpeedFromLastWP(): Double {
        val speed = (distanceFromLastWP / timeFromLastWP) * 1000 * 3.6
        return if (!speed.isNaN()) speed else 0.0
    }
}