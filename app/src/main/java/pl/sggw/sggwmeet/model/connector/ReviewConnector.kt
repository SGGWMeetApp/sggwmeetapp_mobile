package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.request.RateReviewRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewConnector {

    @POST("/api/places/{placeId}/reviews/{reviewId}/votes")
    suspend fun rateReview(@Path("placeId") placeId: String,
                           @Path("reviewId") reviewId: String,
                           @Body rateReviewRequest: RateReviewRequest
    ) : Response<Void>
}