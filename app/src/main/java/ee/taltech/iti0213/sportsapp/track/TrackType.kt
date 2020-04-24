package ee.taltech.iti0213.sportsapp.track

enum class TrackType(val value: Int) {

    UNKNOWN(0),
    RUNNING(1),
    CYCLING(2);

    companion object {
        private val map = values().associateBy(TrackType::value)
        fun fromInt(type: Int) = map[type]
    }
}