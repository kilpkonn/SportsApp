package ee.taltech.iti0213.sportsapp.util

import ee.taltech.iti0213.sportsapp.component.imageview.TrackTypeIcons
import ee.taltech.iti0213.sportsapp.track.TrackType
import java.text.SimpleDateFormat
import java.util.*

class TrackUtils {
    companion object {
        private val trackNameRegex = "\\w+\\s\\d{1,2}-\\d{1,2}+-\\d{4}".toRegex()
        private val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)

        fun generateNameIfNeeded(name: String, type: TrackType): String {
            if (name == "" || TrackTypeIcons.OPTIONS.any { t -> name.startsWith(t) } && name.matches(trackNameRegex)) {
                val date = Date()
                return "${TrackTypeIcons.getString(type)} ${dateFormatter.format(date)}"
            }
            return name
        }
    }
}