package pl.sggw.sggwmeet.model.repository

import android.util.Log
import android.util.Patterns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.domain.UserCredentials
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.util.exception.ClientErrorCode
import pl.sggw.sggwmeet.util.exception.ClientException
import pl.sggw.sggwmeet.util.exception.TechnicalException
import pl.sggw.sggwmeet.util.exception.ServerException
import pl.sggw.sggwmeet.mapper.AuthorizationMapper
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.connector.dto.response.ErrorResponse
import pl.sggw.sggwmeet.util.Resource

class AuthorizationRepository(
    private val connector: AuthorizationConnector,
    private val mapper: AuthorizationMapper
) {

    companion object {
        private const val TAG = "AuthorizationRepository"
        private const val MIN_PASSWORD_LENGTH = 5
    }

    suspend fun login(userCredentials: UserCredentials) : Flow<Resource<UserData>> = flow {
        emit(Resource.Loading())
        try {
            validateUserCredentials(userCredentials)
            val connectorRequest = mapper.map(userCredentials)
            val response = connector.login(connectorRequest)
            interceptBackendErrors(response)
            //TODO caching token now?
            val result = mapper.map(response)
            Log.i(TAG, "Login successful, response data : $result")
            emit(Resource.Success(result))
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during logging user in, user email : ${userCredentials.email}", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during logging user in, user email : ${userCredentials.email}", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during logging user in, user email : ${userCredentials.email}", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun register(userCredentials: UserCredentials, userData: UserData) : Flow<Resource<Nothing>> = flow {
        emit(Resource.Loading())
        try {
            validateUserCredentials(userCredentials)
            val connectorRequest = mapper.map(userCredentials, userData)
            val response = connector.register(connectorRequest)
            interceptBackendErrors(response)
            //TODO caching token now?
            Log.i(TAG, "Registering for user ${userCredentials.email} was successful")
            emit(Resource.Success())
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during registering user, user email : ${userCredentials.email}")
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "An backend exception occurred during registering user, user email : ${userCredentials.email}")
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during registering user, user email : ${userCredentials.email}", exception)
            emit(Resource.Error(TechnicalException("An exception occurred during registering user")))
        }
    }

    private fun interceptBackendErrors(errorResponse: ErrorResponse) {
        if(errorResponse.errorCode != null) {
            throw ServerException(errorResponse.errorCode!!, errorResponse.message!!)
        }
    }

    private fun validateUserCredentials(userCredentials: UserCredentials) {
        if(!isEmailValid(userCredentials.email)) {
            throw ClientException("Email is invalid", ClientErrorCode.EMAIL_VALIDATION_ERROR)
        }
        if(!isPasswordValid(userCredentials.password)) {
            throw ClientException("Password is invalid", ClientErrorCode.PASSWORD_VALIDATION_ERROR)
        }
    }

    // A placeholder username validation check
    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > MIN_PASSWORD_LENGTH
    }

}