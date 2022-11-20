package pl.sggw.sggwmeet.model.connector.dto.request

data class UserRegisterRequest(
    var email : String,
    var password : String,
    var userData : UserRegisterRequestData
)