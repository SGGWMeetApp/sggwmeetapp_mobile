package pl.sggw.sggwmeet.model.connector.dto.response

import pl.sggw.sggwmeet.domain.UserData

data class UserEditResponse(
    var email : String,
    var userData: UserData
) : ErrorResponse()
