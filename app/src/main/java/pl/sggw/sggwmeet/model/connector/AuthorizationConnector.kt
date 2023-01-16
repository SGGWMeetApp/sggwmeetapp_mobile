package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.request.ResetPasswordRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserLoginRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserRegisterRequest
import pl.sggw.sggwmeet.model.connector.dto.response.ResetPasswordResponse
import pl.sggw.sggwmeet.model.connector.dto.response.UserLoginResponse
import pl.sggw.sggwmeet.model.connector.dto.response.UserRegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * REST connector for authorization
 */
interface AuthorizationConnector {

    /**
     * Logins user in the backend application
     */
    @POST("/api/login_check")
    suspend fun login(@Body loginRequest: UserLoginRequest) : Response<UserLoginResponse>

    /**
     * Registers user in the backend application
     */
    @POST("/register")
    suspend fun register(@Body registerRequest: UserRegisterRequest) : Response<UserRegisterResponse>

    /**
     * Registers user in the backend application
     */
    @POST("/reset_password")
    suspend fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest) : Response<ResetPasswordResponse>
}