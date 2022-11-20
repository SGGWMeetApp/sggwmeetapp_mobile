package pl.sggw.sggwmeet.model.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.mapper.PlacesMapper
import pl.sggw.sggwmeet.model.connector.PlacesConnector
import pl.sggw.sggwmeet.model.connector.dto.response.ErrorResponse
import pl.sggw.sggwmeet.provider.RootMarkerProvider
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException

class PlacesRepository(
    private val connector: PlacesConnector,
    private val mapper: PlacesMapper,
    private val rootMarkerProvider: RootMarkerProvider
) {

    companion object {
        private const val TAG = "PlacesRepository"
    }

    suspend fun getSimplePlaceListForMarkers(category: PlaceCategory?) : Flow<Resource<List<PlaceMarkerData>>> = flow {
        emit(Resource.Loading())
        try {
            val response = connector.getPlaces(category)
            interceptBackendErrors(response)
            val result = mapper.mapToMarkers(response.places) as MutableList
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

    private fun interceptBackendErrors(errorResponse: ErrorResponse) {
        if(errorResponse.errorCode != null) {
            throw ServerException(errorResponse.errorCode!!, errorResponse.message!!)
        }
    }

}