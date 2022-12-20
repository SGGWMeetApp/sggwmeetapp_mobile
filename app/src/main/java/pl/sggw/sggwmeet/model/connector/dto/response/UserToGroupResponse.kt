package pl.sggw.sggwmeet.model.connector.dto.response

data class UserToGroupResponse(
    var id: Int,
    var firstName : String,
    var lastName : String,
    var email : String,
    var avatarUrl : String?
) : ErrorResponse()