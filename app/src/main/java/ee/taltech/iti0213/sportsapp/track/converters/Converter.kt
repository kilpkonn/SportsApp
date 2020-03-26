package ee.taltech.iti0213.sportsapp.track.converters

class Converter {
    companion object {
        fun longToHhMmSs(millis: Long): String {
            val seconds = millis / 1000
            val s = seconds % 60
            val m = seconds / 60 % 60
            val h = seconds / (60 * 60) % 24
            return String.format("%d:%02d:%02d", h, m, s)
        }
    }
}