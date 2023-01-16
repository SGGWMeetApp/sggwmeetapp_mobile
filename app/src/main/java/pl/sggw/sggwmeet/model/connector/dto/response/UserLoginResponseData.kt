package pl.sggw.sggwmeet.model.connector.dto.response

data class UserLoginResponseData(
    var firstName : String,
    var lastName : String,
    var phoneNumberPrefix: String,
    var phoneNumber: String,
    var description: String?,
    var avatarUrl : String?
)