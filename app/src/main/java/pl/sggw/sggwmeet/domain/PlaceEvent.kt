package pl.sggw.sggwmeet.domain

import java.util.Date

data class PlaceEvent(
    val id: String,
    val name: String,
    val description: String,
    val startDate: Date,
    val authorFullName: String,
    val canEdit: Boolean,
    var attendersCount: Int,
    var userAttends: Boolean,
) {

    var isLeaving = false
    var isJoining = false
    var isEditing = false

    fun containsSameDataAs(event: PlaceEvent) : Boolean {
        return name == event.name &&
                description == event.description &&
                startDate == event.startDate &&
                authorFullName == event.authorFullName &&
                canEdit == event.canEdit &&
                attendersCount == event.attendersCount &&
                userAttends == event.userAttends &&
                isLeaving == event.isLeaving &&
                isJoining == event.isJoining &&
                isEditing == event.isEditing
    }
}