package pl.sggw.sggwmeet.model.connector.mock

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.connector.dto.request.ResetPasswordRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserLoginRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserRegisterRequest
import pl.sggw.sggwmeet.model.connector.dto.response.ResetPasswordResponse
import pl.sggw.sggwmeet.model.connector.dto.response.UserLoginResponse
import pl.sggw.sggwmeet.model.connector.dto.response.UserRegisterResponse
import retrofit2.Response

class MockAuthorizationConnector : AuthorizationConnector {

    companion object {
        private val TOKEN = "871e31e7-f068-4fa9-b9a8-ce22a9285c73"
    }

    override suspend fun login(loginRequest: UserLoginRequest): Response<UserLoginResponse> {

        //mock network delay
        withContext(Dispatchers.IO) {
            Thread.sleep(2000)
        }

        val responseData = UserData(
            "Pepe",
            "the Frog",
            "12",
            "4567",
            null,
            "https://www.prospectmagazine.co.uk/content/uploads/2016/11/Pepedankmeems420blazeitohbabyatripledankaestheticafkeepit100.jpg",
            1000
        )

        return Response.success(UserLoginResponse(
            TOKEN,
            responseData
        ))

    }

    override suspend fun register(registerRequest: UserRegisterRequest): Response<UserRegisterResponse> {

        //mock network delay
        withContext(Dispatchers.IO) {
            Thread.sleep(2000)
        }

        return Response.success(
            UserRegisterResponse(
                TOKEN
            )
        )

    }

    override suspend fun resetPassword(resetPasswordRequest: ResetPasswordRequest): Response<ResetPasswordResponse> {
        TODO("Not yet implemented")
    }
}