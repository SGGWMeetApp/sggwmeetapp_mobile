package pl.sggw.sggwmeet.model.connector

import pl.sggw.sggwmeet.model.connector.dto.request.UserLoginRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserRegisterRequest
import pl.sggw.sggwmeet.model.connector.dto.response.UserLoginResponse
import pl.sggw.sggwmeet.model.connector.dto.response.UserRegisterResponse

/**
 * REST connector for authorization
 */
interface AuthorizationConnector {

    /**
     * Logins user in the backend application
     */
    suspend fun login(loginRequest: UserLoginRequest) : UserLoginResponse

    /**
     * Registers user in the backend application
     */
    suspend fun register(registerRequest: UserRegisterRequest) : UserRegisterResponse
}