package pl.sggw.sggwmeet.model.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.domain.Review
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.mapper.PlacesMapper
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.ReviewConnector
import pl.sggw.sggwmeet.model.connector.dto.request.SaveReviewRequest
import pl.sggw.sggwmeet.model.connector.dto.request.RateReviewRequest
import pl.sggw.sggwmeet.util.Resource

class ReviewRepository(
    private val connector: ReviewConnector,
    private val mapper: PlacesMapper,
    private val userDataStore: UserDataStore
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

    suspend fun addReview(placeId: String, comment: String, isPositive: Boolean) : Flow<Resource<Review>> = flow {
        emit(Resource.Loading())
        try {
            val request = SaveReviewRequest(isPositive, comment)
            val response = connector.addReview(placeId, request)
            if(response.isSuccessful) {
                Log.i(TAG, "Adding review was successful")
                val domainReview = mapper.mapReview(response.body()!!, userDataStore.getUserEmail())
                emit(Resource.Success(domainReview))
            //review duplicate
            } else if(response.code() == 409){
                emit(Resource.Error(reviewDuplicationError()))
            } else {
                throw java.lang.RuntimeException("Server exception occurred during adding review: ${response.errorBody()}");
            }
        } catch (exception : ServerException) {
            Log.e(TAG, "An backend exception occurred occurred during adding review")
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during occurred during adding review", exception)
            emit(Resource.Error(TechnicalException("An exception occurred during adding review")))
        }
    }

    suspend fun editReview(placeId: String, reviewId: String, comment: String, isPositive: Boolean) : Flow<Resource<Review>> = flow {
        emit(Resource.Loading())
        try {
            val request = SaveReviewRequest(isPositive, comment)
            val response = connector.editReview(placeId, reviewId, request)
            if(response.isSuccessful) {
                Log.i(TAG, "Editing review was successful")
                val domainReview = mapper.mapReview(response.body()!!, userDataStore.getUserEmail())
                emit(Resource.Success(domainReview))
            } else {
                throw java.lang.RuntimeException("Server exception occurred during editing review: ${response.errorBody()}");
            }
        } catch (exception : ServerException) {
            Log.e(TAG, "An backend exception occurred occurred during editing review")
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during occurred during editing review", exception)
            emit(Resource.Error(TechnicalException("An exception occurred during editing review")))
        }
    }

    private fun reviewDuplicationError() : ServerException {
        return ServerException(
            ServerException.REVIEW_DUPLICATION_CODE,
            ServerException.REVIEW_DUPLICATION_MESSAGE,
        )
    }
}