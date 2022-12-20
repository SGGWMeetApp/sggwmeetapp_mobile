package pl.sggw.sggwmeet.model.connector.dto.response

data class AdminDataResponse(
    var firstName: String,
    var lastName: String,
    var isUserAdmin: Boolean
) :ErrorResponse()
