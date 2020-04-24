package ee.taltech.iti0213.sportsapp.component.imageview

import ee.taltech.iti0213.sportsapp.R
import ee.taltech.iti0213.sportsapp.track.TrackType

class TrackTypeIcons {
    companion object {
        const val UNKNOWN = "Activity"
        const val RUNNING = "Running"
        const val CYCLING = "Cycling"
        val OPTIONS = arrayOf(
            UNKNOWN, RUNNING, CYCLING
        )

        fun getIcon(trackType: TrackType): Int {
            return when (trackType) {
                TrackType.UNKNOWN -> R.drawable.ic_activity_24px
                TrackType.RUNNING -> R.drawable.ic_run_24px
                TrackType.CYCLING -> R.drawable.ic_bike_24px
            }
        }

        fun getString(trackType: TrackType): String {
            return when (trackType) {
                TrackType.UNKNOWN -> UNKNOWN
                TrackType.RUNNING -> RUNNING
                TrackType.CYCLING -> CYCLING
            }
        }
    }
}