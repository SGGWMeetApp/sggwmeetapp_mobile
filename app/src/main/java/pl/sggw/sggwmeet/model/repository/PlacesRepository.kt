package pl.sggw.sggwmeet.model.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceDetails
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.mapper.PlacesMapper
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.PlacesConnector
import pl.sggw.sggwmeet.model.connector.dto.response.ErrorResponse
import pl.sggw.sggwmeet.provider.RootMarkerProvider
import pl.sggw.sggwmeet.util.Resource

class PlacesRepository(
    private val connector: PlacesConnector,
    private val mapper: PlacesMapper,
    private val rootMarkerProvider: RootMarkerProvider,
    private val userDataStore: UserDataStore
) {

    companion object {
        private const val TAG = "PlacesRepository"
    }

    suspend fun getSimplePlaceListForMarkers(categoryCodes: Array<PlaceCategory?>) : Flow<Resource<List<PlaceMarkerData>>> = flow {
        emit(Resource.Loading())
        try {
            val response = connector.getPlaces(categoryCodes).body()
//            interceptBackendErrors(response)
            val result = mapper.mapToMarkers(response!!.places) as MutableList
            //add SGGW marker
            result.add(rootMarkerProvider.getRootMarker())
            Log.i(TAG, "Getting simple places list was successful, places : $response")
            emit(Resource.Success(result))
        } catch (exception : ServerException) {
            Log.e(TAG, "An backend exception occurred during getting place simple list")
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during getting place simple list", exception)
            emit(Resource.Error(TechnicalException("An exception occurred during getting place simple list")))
        }
    }

    suspend fun getPlaceDetails(id: String) : Flow<Resource<PlaceDetails>> = flow {
        emit(Resource.Loading())
        try {
            val response = connector.getPlaceDetails(id).body()
//            interceptBackendErrors(response)
            val currentUserEmail = userDataStore.getUserEmail()
            val result = mapper.mapToPlaceDetails(response!!, currentUserEmail)
            //add SGGW marker
            Log.i(TAG, "Getting place details was successful, place : $response")
            emit(Resource.Success(result))
        } catch (exception : ServerException) {
            Log.e(TAG, "An backend exception occurred during getting place details")
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during getting place details", exception)
            emit(Resource.Error(TechnicalException("An exception occurred during getting place details")))
        }
    }

    private fun interceptBackendErrors(errorResponse: ErrorResponse) {
        if(errorResponse.errorCode != null) {
            throw ServerException(errorResponse.errorCode!!, errorResponse.message!!)
        }
    }

}