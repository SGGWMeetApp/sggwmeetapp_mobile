package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.request.UploadImageRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserEditRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserChangePasswordRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserNotificationSettingsRequest
import pl.sggw.sggwmeet.model.connector.dto.response.*
import retrofit2.Response
import retrofit2.http.*

/**
 * REST connector for user data
 */
interface UserConnector {

    /**
     * Changes user password
     */
    @POST("/api/change_password")
    suspend fun changePassword(@Body userChangePasswordRequest: UserChangePasswordRequest) : Response<UserChangePasswordResponse>

    /**
     * Edits user data
     */
    @PATCH("/api/users/{id}")
    suspend fun editUser(@Body editUserRequest: UserEditRequest, @Path("id") id: Int) : Response<UserEditResponse>

    /**
     * Uploads user avatar
     */
    @POST("/api/user/{id}/avatar")
    suspend fun uploadAvatar(@Body uploadImageRequest: UploadImageRequest, @Path("id") id: Int) : Response<UploadAvatarResponse>

    /**
     * Gets user notification settings
     */
    @GET("/api/users/{id}/notification_settings")
    suspend fun getNotificationSettings(@Path("id") id: Int) : Response<UserNotificationSettingsResponse>

    /**
     * Updates user notification settings
     */
    @PATCH("/api/users/{id}/notification_settings")
    suspend fun setNotificationSettings(@Body userNotificationSettingsRequest: UserNotificationSettingsRequest, @Path("id") id: Int) : Response<UserNotificationSettingsResponse>

    /**
     * Gets user data
     */
    @GET("/api/users/{id}")
    suspend fun getUserData(@Path("id") id: Int) : Response<UserEditResponse>

    /**
     * Gets user eligible to add to group.
     */
    @GET("/api/groups/{id}/users/eligible")
    suspend fun getUsersEligibleToGroup(@Path("id") group_Id: Int) : Response<UsersToGroupResponse>

}