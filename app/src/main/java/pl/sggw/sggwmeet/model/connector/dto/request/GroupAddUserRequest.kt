package pl.sggw.sggwmeet.model.connector.dto.request

data class GroupAddUserRequest(
    var userId: Int,
    var position: Int = -1
)
