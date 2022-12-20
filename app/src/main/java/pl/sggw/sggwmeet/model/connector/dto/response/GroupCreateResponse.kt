package pl.sggw.sggwmeet.model.connector.dto.response

data class GroupCreateResponse(
    var id : Int,
    var name: String
) : ErrorResponse()