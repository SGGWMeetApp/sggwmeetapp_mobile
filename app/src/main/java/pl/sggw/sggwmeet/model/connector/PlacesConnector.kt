package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.model.connector.dto.response.PlaceListResponse

/**
 * REST connector for places data
 */
interface PlacesConnector {

    /**
     * Returns basic places data near SGGW, category parameter is optional
     */
    suspend fun getPlaces(category : PlaceCategory?) : PlaceListResponse
}