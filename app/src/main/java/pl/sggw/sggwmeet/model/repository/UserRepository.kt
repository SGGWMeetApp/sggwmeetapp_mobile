package pl.sggw.sggwmeet.model.repository

import android.util.Base64
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.sggw.sggwmeet.domain.UserChangePasswordData
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.mapper.UserMapper
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.UserConnector
import pl.sggw.sggwmeet.model.connector.dto.request.UploadImageRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserEditRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserEditUserDataRequest
import pl.sggw.sggwmeet.model.connector.dto.response.ErrorResponse
import pl.sggw.sggwmeet.util.Resource
import java.io.ByteArrayOutputStream
import java.io.InputStream

class UserRepository(
    private val connector: UserConnector,
    private val mapper: UserMapper,
    private val userDataStore: UserDataStore
) {

    companion object {
        private const val TAG = "UserRepository"
        private const val MIN_PASSWORD_LENGTH = 8
    }

    suspend fun changePassword(userChangePasswordData: UserChangePasswordData) : Flow<Resource<Nothing>> = flow {
        emit(Resource.Loading())
        try {
            validateUserPassword(userChangePasswordData)
            val connectorRequest = mapper.map(userChangePasswordData)
            val response = connector.changePassword(connectorRequest)
            if(response.isSuccessful) {
                val responseBody = response.body()
                userDataStore.store(responseBody!!, userChangePasswordData)
                Log.i(TAG, "Password change successful, response data : $response")
                emit(Resource.Success())
            }
            else{
                Log.e(TAG, "An exception occurred during changing the password, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during changing the password", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during changing the password", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during changing the password", exception)
            emit(Resource.Error(TechnicalException("An error occurred during authorization")))
        }
    }

    suspend fun userEdit(userEditUserDataRequest: UserEditUserDataRequest, userId: Int) : Flow<Resource<Nothing>> = flow {
        emit(Resource.Loading())
        try {
            val response = connector.editUser(UserEditRequest(userEditUserDataRequest),userId)
            if(response.isSuccessful) {
                val responseBody = response.body()
                userDataStore.store(responseBody!!)
                Log.i(TAG, "User edit successful, response data : $response")
                emit(Resource.Success())
            }
            else{
                Log.e(TAG, "An exception occurred during user editing, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during user editing", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during user editing", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during user editing", exception)
            emit(Resource.Error(TechnicalException("An error occurred during user editing")))
        }
    }

    suspend fun uploadAvatar(inputStream: InputStream, userId: Int) : Flow<Resource<Nothing>> = flow {
        emit(Resource.Loading())
        try {
            var byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            var buffer = ByteArray(bufferSize)
            var len = 0
            while (inputStream!!.read(buffer).also { len = it } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            var encodedBase64 = ""
            encodedBase64 = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT)
            val response = connector.uploadAvatar(
                UploadImageRequest(encodedBase64),
                userId
            )
            if(response.isSuccessful) {
                val responseBody = response.body()
                userDataStore.store(responseBody!!)
                Log.i(TAG, "Avatar upload successful, response data : $response")
                emit(Resource.Success())
            }
            else{
                Log.e(TAG, "An exception occurred during avatar upload, CODE: "+response.code()+": "+ response.message())
                emit(Resource.Error(ServerException(response.code().toString(),response.message())))
            }
        } catch (exception : ClientException) {
            Log.e(TAG, "Client exception occurred during avatar upload", exception)
            emit(Resource.Error(exception))
        } catch (exception : ServerException) {
            Log.e(TAG, "Backend exception occurred during avatar upload", exception)
            emit(Resource.Error(exception))
        } catch (exception : Exception) {
            Log.e(TAG, "An exception occurred during avatar upload", exception)
            emit(Resource.Error(TechnicalException("An error occurred during avatar upload")))
        }
    }

    private fun interceptBackendErrors(errorResponse: ErrorResponse) {
        if(errorResponse.errorCode != null) {
            throw ServerException(errorResponse.errorCode!!, errorResponse.message!!)
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > MIN_PASSWORD_LENGTH
    }

    private fun validateUserPassword(passwordData: UserChangePasswordData) {
        if(!isPasswordValid(passwordData.newPassword)) {
            throw ClientException("New password is invalid", ClientErrorCode.PASSWORD_VALIDATION_ERROR)
        }
        if(passwordData.newPassword!=passwordData.newPasswordRepeat){
            throw ClientException("New passwords do not match", ClientErrorCode.PASSWORDS_DO_NOT_MATCH)
        }
    }

}