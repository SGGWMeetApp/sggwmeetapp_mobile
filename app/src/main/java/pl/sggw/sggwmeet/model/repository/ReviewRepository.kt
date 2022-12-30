package pl.sggw.sggwmeet.model.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.ReviewConnector
import pl.sggw.sggwmeet.model.connector.dto.request.RateReviewRequest
import pl.sggw.sggwmeet.util.Resource

class ReviewRepository(
    private val connector: ReviewConnector
) {

    companion object {
        private const val TAG = "ReviewRepository"
    }

    suspend fun like(placeId: String, reviewId: String) : Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        Log.i(TAG, "Processing review like request. placeId: $placeId, reviewId: $reviewId")
        try {
            val response = connector.rateReview(placeId, reviewId, RateReviewRequest(true))
            if(response.isSuccessful) {
                Log.i(TAG, "Liking review was successful")
                emit(Resource.Success(reviewId))
            } else {
                throw java.lang.RuntimeException("Server exception occurred during liking review: ${response.errorBody()}");
            }
        } catch (exception : ServerException) {
            Log.e(TAG, "An backend exception occurred occurred during liking review")
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during occurred during liking review", exception)
            emit(Resource.Error(TechnicalException("An exception occurred during liking review")))
        }
    }

    suspend fun dislike(placeId: String, reviewId: String) : Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        Log.i(TAG, "Processing review dislike request. placeId: $placeId, reviewId: $reviewId")
        try {
            val response = connector.rateReview(placeId, reviewId, RateReviewRequest(false))
            if(response.isSuccessful) {
                Log.i(TAG, "Disliking review was successful")
                emit(Resource.Success(reviewId))
            } else {
                throw java.lang.RuntimeException("Server exception occurred during disliking review: ${response.errorBody()}");
            }
        } catch (exception : ServerException) {
            Log.e(TAG, "An backend exception occurred occurred during disliking review")
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during occurred during disliking review", exception)
            emit(Resource.Error(TechnicalException("An exception occurred during disliking review")))
        }
    }
}