package ee.taltech.iti0213.sportsapp.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import ee.taltech.iti0213.sportsapp.track.pracelable.loaction.TrackLocation
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
class GpsLocationDto(
    @JsonProperty("id")
    val id: String?,
    @JsonProperty("recordedAt")
    val recordedAt: LocalDateTime?,
    @JsonProperty("latitude")
    val latitude: Double,
    @JsonProperty("longitude")
    val longitude: Double,
    @JsonProperty("accuracy")
    val accuracy: Double,
    @JsonProperty("altitude")
    val altitude: Double,
    @JsonProperty("verticalAccuracy")
    val verticalAccuracy: Double,
    @JsonProperty("appUserId")
    val appUserId: String?,
    @JsonProperty("gpsSessionId")
    val gpsSessionId: String?,
    @JsonProperty("gpsLocationTypeId")
    val gpsLocationTypeId: String?
) {
    companion object{

        fun fromTrackLocation(location: TrackLocation, gpsSessionId: String): GpsLocationDto {
            return GpsLocationDto(
                id = null,
                recordedAt = null,
                latitude = location.latitude,
                longitude = location.longitude,
                accuracy = location.accuracy.toDouble(),
                altitude = location.altitude,
                verticalAccuracy = location.altitudeAccuracy.toDouble(),
                appUserId = null,
                gpsSessionId = gpsSessionId,
                gpsLocationTypeId = "00000000-0000-0000-0000-000000000001"
            )
        }
    }
}