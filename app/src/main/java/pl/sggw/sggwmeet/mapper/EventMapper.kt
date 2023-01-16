package pl.sggw.sggwmeet.mapper

import pl.sggw.sggwmeet.domain.PlaceEvent
import pl.sggw.sggwmeet.model.connector.dto.request.EventCreatePublicRequest
import pl.sggw.sggwmeet.model.connector.dto.request.EventEditRequest
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.GetEventResponse
import pl.sggw.sggwmeet.ui.dialog.AddPublicEventDialog
import java.text.SimpleDateFormat
import java.util.*

class EventMapper {

    companion object {
        private val DATE_TO_ISO_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
    }

    fun mapPlaceEvents(events: GetEventResponse) : List<PlaceEvent> {
        return events.events
            .map { mapPlaceEvent(it) }
            .sortedByDescending { it.canEdit }
            .sortedByDescending { it.startDate }
    }

    fun mapPlaceEvent(event: EventResponse) : PlaceEvent {
        return PlaceEvent(
            event.id.toString(),
            event.name,
            event.description ?: "",
            event.startDate,
            "${event.author.firstName} ${event.author.lastName}",
            event.canEdit,
            event.attendersCount,
            event.userAttends,
            event.author.email
        )
    }

    fun buildEventCreateRequest(eventSnapshot: AddPublicEventDialog.PublicEventSnapshot, placeId: Int): EventCreatePublicRequest {
        return EventCreatePublicRequest(
            eventSnapshot.eventName,
            placeId,
            eventSnapshot.eventDescription,
            DATE_TO_ISO_FORMAT.format(eventSnapshot.eventDate)
        )
    }

    fun buildEventEditRequest(eventSnapshot: AddPublicEventDialog.PublicEventSnapshot, placeId: Int): EventEditRequest {
        return EventEditRequest(
            eventSnapshot.eventName,
            placeId,
            eventSnapshot.eventDescription,
            DATE_TO_ISO_FORMAT.format(eventSnapshot.eventDate)
        )
    }
}