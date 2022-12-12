package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.model.connector.dto.response.PlaceListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * REST connector for places data
 */
interface PlacesConnector {

    /**
     * Returns basic places data near SGGW, category parameter is optional
     */
    @GET("/api/places")
    suspend fun getPlaces(@Query("category") category : PlaceCategory?) : Response<PlaceListResponse>
}