package pl.sggw.sggwmeet.domain

data class UserData(
    val firstName : String,
    val lastName : String,
    val phoneNumberPrefix: String,
    val phoneNumber : String,
    val description : String? = null,
    val avatarUrl : String? = null,
    val id: Int = 0
)