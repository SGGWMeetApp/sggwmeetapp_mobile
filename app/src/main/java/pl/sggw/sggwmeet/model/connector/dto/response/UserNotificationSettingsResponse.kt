package pl.sggw.sggwmeet.model.connector.dto.response

data class UserNotificationSettingsResponse(
    val event_notification : Boolean,
    val group_add_notification : Boolean,
    val group_remove_notification: Boolean
):ErrorResponse()