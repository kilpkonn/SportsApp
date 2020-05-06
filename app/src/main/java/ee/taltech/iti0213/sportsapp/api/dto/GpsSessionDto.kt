package ee.taltech.iti0213.sportsapp.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.sql.Timestamp

@JsonInclude(JsonInclude.Include.NON_NULL)
class GpsSessionDto(
    @JsonProperty("id")
    val id: String? = null,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String = "",
    @JsonProperty("recordedAt")
    val recordedAt: Timestamp? = Timestamp(0),
    @JsonProperty("duration")
    val duration: Long = 0,
    @JsonProperty("speed")
    val speed: Double = 0.0,
    @JsonProperty("distance")
    val distance: Double = 0.0,
    @JsonProperty("climb")
    val climb: Double = 0.0,
    @JsonProperty("descent")
    val descent: Double = 0.0,
    @JsonProperty("paceMin")
    val paceMin: Double = 0.0,
    @JsonProperty("paceMax")
    val paceMax: Double = 0.0,
    @JsonProperty("gpsSessionTypeId")
    val gpsSessionTypeId: String? = "00000000-0000-0000-0000-000000000001",
    @JsonProperty("appUserId")
    val appUserId: String? = null
)