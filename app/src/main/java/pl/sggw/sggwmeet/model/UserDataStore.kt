package pl.sggw.sggwmeet.model

import android.util.Log
import com.google.gson.Gson
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.domain.UserChangePasswordData
import pl.sggw.sggwmeet.domain.UserCredentials
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.mapper.AuthorizationMapper
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.connector.dto.response.*

class UserDataStore(
    private val gson: Gson,
    private val authorizationConnector: AuthorizationConnector,
    private val mapper: AuthorizationMapper
) {

    companion object {
        private const val TOKEN_KEY = "token"
        private const val TOKEN_GENERATION_TIMESTAMP_KEY = "tokenGenerationTimestamp"
        private const val USER_DATA_KEY = "userData"
        private const val EMAIL_KEY = "email"
        private const val PASSWORD_KEY = "password"
        private const val USER_ID_KEY = "userId"
        private const val AVATAR_KEY = "avatarUrl"

        //How much time token is valid (in miliseconds), default : 9 mins
        private const val TOKEN_EXPIRATION_TIME = 540000L

        private const val TAG = "UserDataStore"
    }

    /**
     * Stores user data in 'SharedPreferences' storage
     */
    fun store(response: UserLoginResponse, userCredentials: UserCredentials) {
        Log.d("token: ", response.token)
        Log.d("userData: ", response.toString())
        var avatarUrl=""
        if (response.userData.avatarUrl!=null){
            avatarUrl=response.userData.avatarUrl.toString()
        }
        Prefs.write()
            .content(TOKEN_KEY, response.token)
            .content(TOKEN_GENERATION_TIMESTAMP_KEY, System.currentTimeMillis())
            .content(EMAIL_KEY, userCredentials.username)
            .content(USER_DATA_KEY, gson.toJson(response.userData))
            .content(USER_ID_KEY, response.userData.id)
            .content(AVATAR_KEY, avatarUrl)
            .apply()

        Prefs.securely().write()
            .content(PASSWORD_KEY, userCredentials.password)
            .apply()
    }

    /**
     * Stores user data in 'SharedPreferences' storage
     */
    fun store(response: UserRegisterResponse, userCredentials: UserCredentials) {
        Log.d("token: ", response.token)
        Log.d("userData: ", response.toString())
        Prefs.write()
            .content(TOKEN_KEY, response.token)
            .content(TOKEN_GENERATION_TIMESTAMP_KEY, System.currentTimeMillis())
            .content(EMAIL_KEY, userCredentials.username)
            .apply()

        Prefs.securely().write()
            .content(PASSWORD_KEY, userCredentials.password)
            .apply()
    }

    /**
     * Stores user data in 'SharedPreferences' storage on password change.
     */
    fun store(response: UserChangePasswordResponse, passwordData: UserChangePasswordData) {
        Prefs.securely().write()
            .content(PASSWORD_KEY, passwordData.newPassword)
            .apply()
    }

    /**
     * Stores user data in 'SharedPreferences' storage on user edit.
     */
    fun store(response: UserEditResponse) {
        Log.d("userData: ", response.toString())
        Prefs.write()
            .content(USER_DATA_KEY, gson.toJson(response.userData))
            .apply()

    }

    /**
     * Stores avatar Uri in 'SharedPreferences' storage on avatar upload.
     */
    fun store(response: UploadAvatarResponse) {
        Log.d("Upload Avatar Response: ", response.toString())
        Prefs.write()
            .content(AVATAR_KEY, response.avatarUrl)
            .apply()

    }

    /**
     * Gets token from the storage. If token is not valid anymore then login request will be sent with stored user data
     * to renew the token
     */
    suspend fun getToken() : String {
        val token = Prefs.read().content(TOKEN_KEY, "")
        if(token.isBlank()) {
            throw TechnicalException("Could not find user token while it should have been saved after login.")
        }
        if(isTokenExpired()) {
            Log.i(TAG, "Renewing expired token by sending login request...")

            val userCredentials = UserCredentials(
                Prefs.read().content(EMAIL_KEY,""),
                Prefs.securely().read().content(PASSWORD_KEY,"")
            )
            val loginRequest = mapper.map(userCredentials)
            val response = authorizationConnector.login(loginRequest)
            store(response.body()!!, userCredentials)
            return Prefs.read().content(TOKEN_KEY, "")
        }
        return token
    }

    private fun isTokenExpired() : Boolean {
        val tokenGenerationTime = Prefs.read().content(TOKEN_GENERATION_TIMESTAMP_KEY, 0L)
        if(tokenGenerationTime == 0L) {
            throw TechnicalException("Could not find user token generation time while it should have been saved after login.")
        }
        return System.currentTimeMillis() > tokenGenerationTime + TOKEN_EXPIRATION_TIME
    }

}