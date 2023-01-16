package pl.sggw.sggwmeet.model.connector.dto.response

data class GroupMemberResponse(
    var id: Int,
    var firstName: String,
    var lastName: String,
    var email: String,
    var isAdmin: Boolean
) :ErrorResponse()
