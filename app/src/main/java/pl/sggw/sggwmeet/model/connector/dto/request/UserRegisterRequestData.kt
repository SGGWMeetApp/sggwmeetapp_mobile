package pl.sggw.sggwmeet.model.connector.dto.request

data class UserRegisterRequestData(
    var firstName : String,
    var lastName : String,
    var phoneNumberPrefix: String,
    var phoneNumber : String,
    var description : String?
)