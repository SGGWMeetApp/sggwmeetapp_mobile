package pl.sggw.sggwmeet.mapper

import pl.sggw.sggwmeet.domain.UserCredentials
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.model.connector.dto.request.UserLoginRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserRegisterRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserRegisterRequestData
import pl.sggw.sggwmeet.model.connector.dto.response.UserLoginResponse

class AuthorizationMapper {

    fun map(userCredentials: UserCredentials) : UserLoginRequest {
        return UserLoginRequest(
            userCredentials.email,
            userCredentials.password
        )
    }

    fun map(userLoginResponse: UserLoginResponse) : UserData {
        return UserData(
            userLoginResponse.userData.firstName,
            userLoginResponse.userData.lastName,
            null,
            null,
            userLoginResponse.userData.avatarUrl
        )
    }

    fun map(userCredentials: UserCredentials, userData: UserData) : UserRegisterRequest {
        return UserRegisterRequest(
            userCredentials.email,
            userCredentials.password,
            UserRegisterRequestData(
                userData.firstName,
                userData.lastName,
                userData.phoneNumber,
                userData.description
            )
        )
    }
}