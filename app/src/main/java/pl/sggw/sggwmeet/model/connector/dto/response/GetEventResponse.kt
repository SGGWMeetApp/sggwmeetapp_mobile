package pl.sggw.sggwmeet.model.connector.dto.response

data class GetEventResponse(
    val events: ArrayList<EventResponse>
): ErrorResponse()
