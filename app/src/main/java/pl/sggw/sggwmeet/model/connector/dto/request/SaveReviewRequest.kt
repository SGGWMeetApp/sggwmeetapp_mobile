package pl.sggw.sggwmeet.model.connector.dto.request

data class SaveReviewRequest(
    val isPositive: Boolean,
    val comment: String
) {
}