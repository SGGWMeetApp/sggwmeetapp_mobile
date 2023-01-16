package pl.sggw.sggwmeet.model.connector.dto.response

data class GroupGetMembersResponse(
    var id: Int,
    var name: String,
    var users: ArrayList<GroupMemberResponse>,
    var isUserAdmin: Boolean
)
