package pl.sggw.sggwmeet.model.connector.dto.response

data class GetGroupsResponse(
    val groups: ArrayList<GroupResponse>
): ErrorResponse()
