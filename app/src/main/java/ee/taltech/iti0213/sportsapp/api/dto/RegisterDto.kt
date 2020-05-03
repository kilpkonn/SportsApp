package ee.taltech.iti0213.sportsapp.api.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
class RegisterDto(
    @JsonProperty("email")
    var email: String,
    @JsonProperty("password")
    var password: String,
    @JsonProperty("lastName")
    var lastName: String,
    @JsonProperty("firstName")
    var firstName: String
)