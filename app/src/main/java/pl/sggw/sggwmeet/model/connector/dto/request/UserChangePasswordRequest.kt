package pl.sggw.sggwmeet.model.connector.dto.request

data class UserChangePasswordRequest (
    var oldPassword : String,
    var newPassword : String
    )