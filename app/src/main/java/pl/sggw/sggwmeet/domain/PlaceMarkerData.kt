package pl.sggw.sggwmeet.domain

data class PlaceMarkerData(
    val id: String,
    val name : String,
    val category : PlaceCategory,
    val geolocation: Geolocation
)