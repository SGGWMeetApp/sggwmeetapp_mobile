package pl.sggw.sggwmeet.model.connector.dto.response

data class GroupAddUserResponse(
    var firstName: String,
    var lastName: String,
    var email: String,
    var avatarUrl: String?,
    var isAdmin: Boolean
) :ErrorResponse()
