package pl.sggw.sggwmeet.model.connector.dto.response

data class EventAuthorResponse(
    var firstName: String,
    var lastName: String,
    var email: String,
    var avatarUrl: String
):ErrorResponse()
