package pl.sggw.sggwmeet.model.connector.dto.response

data class GroupResponse(
    var id: Int,
    var name: String,
    var memberCount: Int,
    var adminData: AdminDataResponse,
    var incomingEventsCount: Int
) :ErrorResponse()
