package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.request.GroupCreateRequest
import pl.sggw.sggwmeet.model.connector.dto.response.GetGroupsResponse
import pl.sggw.sggwmeet.model.connector.dto.response.GroupCreateResponse
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
     * Gets user groups
     */
    @POST("/api/groups")
    suspend fun createNewGroup(@Body groupCreateRequest: GroupCreateRequest) : Response<GroupCreateResponse>

}