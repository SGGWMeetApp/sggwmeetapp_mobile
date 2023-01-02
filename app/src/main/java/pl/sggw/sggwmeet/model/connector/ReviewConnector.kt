package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.request.SaveReviewRequest
import pl.sggw.sggwmeet.model.connector.dto.request.RateReviewRequest
import pl.sggw.sggwmeet.model.connector.dto.response.PlaceDetailsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewConnector {

    @POST("/api/places/{placeId}/reviews/{reviewId}/votes")
    suspend fun rateReview(@Path("placeId") placeId: String,
                           @Path("reviewId") reviewId: String,
                           @Body rateReviewRequest: RateReviewRequest
    ) : Response<Void>

    @POST("/api/places/{placeId}/reviews")
    suspend fun addReview(@Path("placeId") placeId: String,
                          @Body request: SaveReviewRequest,
    ) : Response<PlaceDetailsResponse.Rating.Review>

    @PUT("/api/places/{placeId}/reviews/{reviewId}")
    suspend fun editReview(@Path("placeId") placeId: String,
                           @Path("reviewId") reviewId: String,
                           @Body request: SaveReviewRequest
    ) : Response<PlaceDetailsResponse.Rating.Review>
}