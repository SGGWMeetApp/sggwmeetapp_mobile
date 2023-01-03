package pl.sggw.sggwmeet.mapper

import pl.sggw.sggwmeet.domain.*
import pl.sggw.sggwmeet.model.connector.dto.response.PlaceDetailsResponse
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData

class PlacesMapper {

    fun mapToMarkers(places : List<SimplePlaceResponseData>) : List<PlaceMarkerData> {
        return places.map { mapToMarker(it) }
    }

    fun mapToPlaceDetails(placeResponse: PlaceDetailsResponse, userEmail: String) : PlaceDetails {
        return PlaceDetails(
            placeResponse.name,
            placeResponse.description,
            placeResponse.rating.positivePercent,
            placeResponse.rating.reviews.size,
            placeResponse.textLocation,
            placeResponse.photoPath,
            placeResponse.rating.reviews
                .map { mapReview(it, userEmail) }
                .sortedWith(
                    compareBy(
                        { it.isOwnedByUser },
                        { it.publicationDate }
                    )
                )
                .asReversed(),
            placeResponse.menuPath
        )
    }

    fun mapReview(responseReview: PlaceDetailsResponse.Rating.Review, userEmail: String) : Review {
        return Review(
            responseReview.id,
            responseReview.comment ?: "",
            mapReviewer(responseReview.author),
            responseReview.isPositive,
            responseReview.upvoteCount,
            responseReview.downvoteCount,
            responseReview.publicationDate,
            responseReview.userVote,
            responseReview.author.email == userEmail
        )
    }

    private fun mapReviewer(responseReviewer: PlaceDetailsResponse.Rating.Review.Author) : Reviewer {
        return Reviewer(
            responseReviewer.firstName,
            responseReviewer.lastName,
            responseReviewer.avatarUrl
        )
    }

    private fun mapToMarker(simplePlaceData : SimplePlaceResponseData) : PlaceMarkerData {
        val markerData =  PlaceMarkerData(
            simplePlaceData.id,
            simplePlaceData.name,
            resolveCategoryCode(simplePlaceData.locationCategoryCodes),
            simplePlaceData.geolocation,
            simplePlaceData.photoPath
        )
        simplePlaceData.reviewSummary.reviewsCount?.let { markerData.reviewsCount = it }
        simplePlaceData.reviewSummary.positivePercent?.let { markerData.positiveReviewsPercent = it }
        return markerData
    }

    private fun resolveCategoryCode(categories : List<PlaceCategory>) : PlaceCategory {
        if(categories.isEmpty()) {
            return PlaceCategory.OTHER
        }
        return categories[0]
    }
}