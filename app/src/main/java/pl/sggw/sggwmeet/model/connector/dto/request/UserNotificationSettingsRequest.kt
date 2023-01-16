package pl.sggw.sggwmeet.model.connector.dto.request

data class UserNotificationSettingsRequest(
    val eventNotification : Boolean,
    val groupAddNotification : Boolean,
    val groupRemoveNotification: Boolean
)