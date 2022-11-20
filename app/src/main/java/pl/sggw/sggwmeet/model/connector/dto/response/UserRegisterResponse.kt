package pl.sggw.sggwmeet.model.connector.dto.response

data class UserRegisterResponse(
    var token : String
) : ErrorResponse()