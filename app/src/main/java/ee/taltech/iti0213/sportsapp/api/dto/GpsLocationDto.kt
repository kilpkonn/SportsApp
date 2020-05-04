package ee.taltech.iti0213.sportsapp.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
class GpsLocationDto(
    @JsonProperty("id")
    val id: String?,
    @JsonProperty("recordedAt")
    val recordedAt: LocalDateTime,
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
    val appUserId: String,
    @JsonProperty("gpsSessionId")
    val gpsSessionId: String,
    @JsonProperty("gpsLocationTypeId")
    val gpsLocationTypeId: String
)