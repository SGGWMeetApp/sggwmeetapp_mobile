package pl.sggw.sggwmeet.model.connector.dto.response

import java.util.*


data class EventResponse(
    var id: Int,
    var name: String,
    var description: String?,
    var locationData: EventLocationResponse,
    var startDate: Date,
    var author: EventAuthorResponse,
    var canEdit: Boolean,
    var notification24hEnabled: Boolean,
    var attendersCount: Int = 0,
    var userAttends: Boolean = true
) :ErrorResponse()
