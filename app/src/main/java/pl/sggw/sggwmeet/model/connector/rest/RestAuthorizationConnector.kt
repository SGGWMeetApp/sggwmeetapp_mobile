package pl.sggw.sggwmeet.model.connector.rest

import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.connector.dto.request.UserLoginRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserRegisterRequest
import pl.sggw.sggwmeet.model.connector.dto.response.UserLoginResponse
import pl.sggw.sggwmeet.model.connector.dto.response.UserRegisterResponse

class RestAuthorizationConnector : AuthorizationConnector {

    override suspend fun login(loginRequest: UserLoginRequest): UserLoginResponse {
        //TODO implement when backend is published
        throw NotImplementedError("Not yet implemented!")
    }

    override suspend fun register(registerRequest: UserRegisterRequest): UserRegisterResponse {
        //TODO implement when backend is published
        throw NotImplementedError("Not yet implemented!")
    }
}