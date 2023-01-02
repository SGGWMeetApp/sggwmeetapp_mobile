package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.request.EventCreatePublicRequest
import pl.sggw.sggwmeet.model.connector.dto.request.EventEditRequest
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.GetEventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.PlaceListResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * REST connector for user data
 */
interface EventConnector {

    /**
     * Gets all events
     */
    @GET("/api/events")
    suspend fun getAllEvents() : Response<GetEventResponse>

    /**
     * Gets upcoming events
     */
    @GET("/api/events/upcoming")
    suspend fun getUpcomingEvents() : Response<GetEventResponse>

    /**
     * Get all places
     */
    @GET("/api/places")
    suspend fun getAllPlaces() : Response<PlaceListResponse>

    /**
     * Edit event
     */
    @PUT("/api/events/{id}")
    suspend fun editEvent(@Body eventEditRequest: EventEditRequest, @Path("id") eventId: Int) : Response<EventResponse>

    /**
     * Creates event
     */
    @POST("/api/events")
    suspend fun createPublicEvent(@Body eventCreatePublicRequest: EventCreatePublicRequest) : Response<EventResponse>

    /**
     * Creates private event
     */
    @POST("/api/groups/{id}/events")
    suspend fun createGroupEvent(@Body eventCreatePublicRequest: EventCreatePublicRequest, @Path("id") groupId: Int) : Response<EventResponse>

    @GET("/api/places/{placeId}/events")
    suspend fun getPlacePublicEvents(@Path("placeId") placeId: String) : Response<GetEventResponse>

}