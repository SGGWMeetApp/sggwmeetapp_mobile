package pl.sggw.sggwmeet.domain

import java.util.Date

data class PlaceEvent(
    val id: String,
    val name: String,
    val description: String,
    val startDate: Date,
    val authorFullName: String,
    val canEdit: Boolean
) {

    fun containsSameDataAs(event: PlaceEvent) : Boolean {
        return name == event.name &&
                description == event.description &&
                startDate == event.startDate &&
                authorFullName == event.authorFullName &&
                canEdit == event.canEdit;
    }
}