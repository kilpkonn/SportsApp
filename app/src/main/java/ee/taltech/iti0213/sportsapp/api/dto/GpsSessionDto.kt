package ee.taltech.iti0213.sportsapp.api.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
class GpsSessionDto(
    @JsonProperty("id")
    val id: String? = null,
    @JsonProperty("name")
    val name: String,
    @JsonProperty("description")
    val description: String = "",
    @JsonProperty("recordedAt")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd'T'HH:mm:ss'Z'", timezone="GMT")
    val recordedAt: Date,
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
    val paceMin: Double = 60.0,
    @JsonProperty("paceMax")
    val paceMax: Double = 120.0,
    @JsonProperty("gpsSessionTypeId")
    val gpsSessionTypeId: String? = "00000000-0000-0000-0000-000000000001",
    @JsonProperty("appUserId")
    val appUserId: String? = null
)