package pl.sggw.sggwmeet.mapper

import pl.sggw.sggwmeet.domain.PlaceEvent
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.GetEventResponse

class EventMapper {

    fun mapPlaceEvents(events: GetEventResponse) : List<PlaceEvent> {
        return events.events
            .map { mapPlaceEvent(it) }
            .sortedByDescending { it.canEdit }
            .sortedByDescending { it.startDate }
    }

    private fun mapPlaceEvent(event: EventResponse) : PlaceEvent {
        return PlaceEvent(
            event.id.toString(),
            event.name,
            event.description ?: "",
            event.startDate,
            "${event.author.firstName} ${event.author.lastName}",
            event.canEdit,
            event.attendersCount,
            event.userAttends
        )
    }
}