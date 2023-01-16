package pl.sggw.sggwmeet.domain

data class PlaceMarkerData(
    val id: String,
    val name : String,
    val category : PlaceCategory,
    val geolocation: Geolocation,
    val photoPath: String?,
    var textLocation: String=""
) {
    var positiveReviewsPercent: Float? = 0.00f
    var reviewsCount: Int = 0
}