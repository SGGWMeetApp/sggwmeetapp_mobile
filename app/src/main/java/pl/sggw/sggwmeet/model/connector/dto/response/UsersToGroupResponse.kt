package pl.sggw.sggwmeet.model.connector.dto.response

data class UsersToGroupResponse(
    var users : ArrayList<UserToGroupResponse>
) : ErrorResponse()