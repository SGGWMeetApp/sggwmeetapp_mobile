package pl.sggw.sggwmeet.domain

data class UserData(
    val firstName : String,
    val lastName : String,
    val phoneNumber : String?,
    val description : String?,
    val avatarUrl : String?
)