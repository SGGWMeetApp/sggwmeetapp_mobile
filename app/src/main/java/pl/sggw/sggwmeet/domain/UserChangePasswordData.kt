package pl.sggw.sggwmeet.domain

data class UserChangePasswordData(
    val oldPassword : String,
    val newPassword : String,
    val newPasswordRepeat : String
)
