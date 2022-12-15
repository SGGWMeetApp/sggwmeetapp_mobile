package pl.sggw.sggwmeet.domain

data class PlaceDetails(
    val name: String,
    val positivePercent: Float,
    val reviewsCount: Int,
    val textLocation: String?,
    val photoPath: String?,
    val reviews: List<Review>
)
