package pl.sggw.sggwmeet.model.connector.mock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.connector.dto.request.UserLoginRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserRegisterRequest
import pl.sggw.sggwmeet.model.connector.dto.response.UserLoginResponse
import pl.sggw.sggwmeet.model.connector.dto.response.UserLoginResponseData
import pl.sggw.sggwmeet.model.connector.dto.response.UserRegisterResponse

class MockAuthorizationConnector : AuthorizationConnector {

    companion object {
        private val TOKEN = "871e31e7-f068-4fa9-b9a8-ce22a9285c73"
    }

    override suspend fun login(loginRequest: UserLoginRequest): UserLoginResponse {

        //mock network delay
        withContext(Dispatchers.IO) {
            Thread.sleep(2000)
        }

        val responseData = UserLoginResponseData(
            "https://www.prospectmagazine.co.uk/content/uploads/2016/11/Pepedankmeems420blazeitohbabyatripledankaestheticafkeepit100.jpg",
            "Pepe",
            "the Frog"
        )

        return UserLoginResponse(
            TOKEN,
            responseData
        )
    }

    override suspend fun register(registerRequest: UserRegisterRequest): UserRegisterResponse {

        //mock network delay
        withContext(Dispatchers.IO) {
            Thread.sleep(2000)
        }

        return UserRegisterResponse(
            TOKEN
        )
    }
}