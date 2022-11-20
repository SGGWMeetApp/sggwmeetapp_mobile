package pl.sggw.sggwmeet.model.connector.dto.response

data class UserLoginResponse(
    var token : String,
    var userData : UserLoginResponseData
) : ErrorResponse()