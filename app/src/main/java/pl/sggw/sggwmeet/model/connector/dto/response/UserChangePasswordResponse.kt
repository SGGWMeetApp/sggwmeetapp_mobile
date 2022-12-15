package pl.sggw.sggwmeet.model.connector.dto.response

data class UserChangePasswordResponse(
    var messageReceived : String
) : ErrorResponse()
