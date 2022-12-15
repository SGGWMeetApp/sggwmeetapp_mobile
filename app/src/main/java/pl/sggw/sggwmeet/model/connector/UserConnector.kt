package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.request.UploadImageRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserEditRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserChangePasswordRequest
import pl.sggw.sggwmeet.model.connector.dto.response.UploadAvatarResponse
import pl.sggw.sggwmeet.model.connector.dto.response.UserChangePasswordResponse
import pl.sggw.sggwmeet.model.connector.dto.response.UserEditResponse
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
}