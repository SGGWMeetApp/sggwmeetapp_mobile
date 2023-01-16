package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.model.connector.dto.response.PlaceDetailsResponse
import pl.sggw.sggwmeet.model.connector.dto.response.PlaceListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST connector for places data
 */
interface PlacesConnector {

    /**
     * Returns basic places data near SGGW, category parameter is optional
     */
    @GET("/api/places")
    suspend fun getPlaces(@Query("categoryCodes[]") categoryCodes : Array<PlaceCategory?>) : Response<PlaceListResponse>

    @GET("/api/places/{id}")
    suspend fun getPlaceDetails(@Path("id") id : String) : Response<PlaceDetailsResponse>
}