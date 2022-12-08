package pl.sggw.sggwmeet.model.connector.dto.response

import pl.sggw.sggwmeet.domain.UserData

data class UserLoginResponse(
    var token : String,
    var userData : UserData
) : ErrorResponse()