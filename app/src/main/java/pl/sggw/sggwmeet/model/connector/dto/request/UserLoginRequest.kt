package pl.sggw.sggwmeet.model.connector.dto.request

data class UserLoginRequest(
    val email : String,
    val password : String
)