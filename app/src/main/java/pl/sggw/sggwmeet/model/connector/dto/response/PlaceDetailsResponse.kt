package pl.sggw.sggwmeet.model.connector.dto.response

import pl.sggw.sggwmeet.domain.Geolocation
import pl.sggw.sggwmeet.domain.PlaceCategory
import java.util.Date

data class PlaceDetailsResponse(
    var id: String,
    var name: String,
    var geolocation: Geolocation,
    var description: String?,
    var photoPath: String?,
    var textLocation: String?,
    var rating: Rating,
    var locationCategoryCodes: List<PlaceCategory>
) {
    data class Rating(
        var positivePercent : Float,
        var reviews: List<Review>
    ) {
        data class Review(
            var id: String,
            var comment: String,
            var author: Author,
            var upvoteCount: Int,
            var downvoteCount: Int,
            var publicationDate: Date,
            var isPositive: Boolean,
            var userVote: Boolean?
        ) {
            data class Author(
               var firstName: String,
               var lastName: String,
               var email: String,
               var avatarUrl: String?,
            ) {}
        }
    }

}
