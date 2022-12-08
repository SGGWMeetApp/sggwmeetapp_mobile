package pl.sggw.sggwmeet.model.connector.dto.request

data class UserLoginRequest(
    val username : String,
    val password : String
)