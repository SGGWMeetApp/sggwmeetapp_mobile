package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.request.GroupAddUserRequest
import pl.sggw.sggwmeet.model.connector.dto.request.GroupCreateRequest
import pl.sggw.sggwmeet.model.connector.dto.request.GroupEventNotificationRequest
import pl.sggw.sggwmeet.model.connector.dto.response.*
import retrofit2.Response
import retrofit2.http.*

/**
 * REST connector for group data
 */
interface GroupConnector {

    /**
     * Gets all groups
     */
    @GET("/api/groups/")
    suspend fun getAllGroups() : Response<GetGroupsResponse>

    /**
     * Gets user groups
     */
    @GET("/api/users/{id}/groups")
    suspend fun getUserGroups(@Path("id") user_Id: Int) : Response<GetGroupsResponse>

    /**
     * Creates new group
     */
    @POST("/api/groups")
    suspend fun createNewGroup(@Body groupCreateRequest: GroupCreateRequest) : Response<GroupCreateResponse>

    /**
     * Adds user to group
     */
    @POST("/api/groups/{group_id}/users")
    suspend fun addUserToGroup(@Body addUserRequest: GroupAddUserRequest, @Path("group_id") group_id: Int) : Response<GroupAddUserResponse>

    /**
     * Gets response after adding user to group
     */
    @GET("/api/groups/{group_id}/users")
    suspend fun getGroupMembers(@Path("group_id") group_id: Int) : Response<GroupGetMembersResponse>

    /**
     * Gets group events
     */
    @GET("/api/groups/{group_id}/events")
    suspend fun getGroupEvents(@Path("group_id") group_id: Int) : Response<GetEventResponse>

    /**
     * Leaves or deletes group
     */
    @DELETE("/api/groups/{group_id}/users")
    suspend fun leaveGroup(@Path("group_id") group_id: Int) : Response<GetGroupsResponse>

    /**
     * Changes notification settings for group for the event
     */
    @POST("/api/groups/{group_id}/events/{event_id}/notifications")
    suspend fun switchGroupEventNotification(@Body groupNotificationRequest:GroupEventNotificationRequest, @Path("group_id") group_id: Int, @Path("event_id") event_id: Int) : Response<EventResponse>

    /**
     * Deletes group event
     */
    @DELETE("/api/groups/{group_id}/events/{event_id}")
    suspend fun deleteGroupEvent(@Path("group_id") group_id: Int, @Path("event_id") event_id: Int) : Response<String>

    /**
     * Deletes group
     */
    @DELETE("/api/groups/{group_id}")
    suspend fun deleteGroup(@Path("group_id") group_id: Int) : Response<String>

}