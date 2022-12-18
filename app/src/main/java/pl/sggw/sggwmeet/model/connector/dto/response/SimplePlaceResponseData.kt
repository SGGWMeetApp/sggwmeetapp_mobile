package pl.sggw.sggwmeet.model.connector.dto.response

import pl.sggw.sggwmeet.domain.Geolocation
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.ReviewSummary

data class SimplePlaceResponseData(
    var id: String,
    var name : String,
    var geolocation : Geolocation,
    var locationCategoryCodes: List<PlaceCategory>,
    var photoPath: String,
    var reviewSummary: ReviewSummary,
    var description: String="",
    var textLocation: String=""
)