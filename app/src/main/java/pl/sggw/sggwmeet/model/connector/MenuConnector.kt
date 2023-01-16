package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.response.MenuResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * REST connector for place menu
 */
interface MenuConnector {

    /**
     * Gets place menu by dynamic path provided as parameter
     */
    @GET
    suspend fun getPlaceMenu(@Url url: String) : Response<MenuResponse>
}