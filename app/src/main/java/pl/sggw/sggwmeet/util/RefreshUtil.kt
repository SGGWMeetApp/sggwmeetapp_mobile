package pl.sggw.sggwmeet.util

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import io.easyprefs.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.instance.RestAuthorizationInstance
import pl.sggw.sggwmeet.model.connector.dto.request.UserLoginRequest

class RefreshUtil {
    companion object {
        fun saveCurrentUserFromJson(json : String){
            Prefs.write()
                .content("userData",json)
                .apply()
        }
        fun saveCurrentUserFromObject(userData : UserData){
            Prefs.write()
                .content("userData", getUserDataJsonFromObject(userData))
                .apply()
        }
        fun getUserDataJsonFromObject(userData : UserData) : String{
            val gson = Gson()
            return gson.toJson(userData)
        }
        fun getUserDataFromJson(json : String) : UserData{
            val gson = Gson()
            return gson.fromJson(json, UserData::class.java)
        }
        fun getCurrentUserData() : UserData{
            return getUserDataFromJson(Prefs.read().content("userData",
            "{\"firstName\":\"Imie\",\"lastName\":\"Nazwisko\",\"phoneNumberPrefix\":\"12\",\"phoneNumber\":\"123\",\"description\":\"\",\"avatarUrl\":null}"
                ))
        }
        suspend fun refreshToken(context: Context){
            val instance = RestAuthorizationInstance
            MainScope().launch {
                try{
                    val response = instance.service.login(
                        UserLoginRequest(
                            Prefs.read().content("email",""),
                            Prefs.securely().read().content("password","")
                        )
                    )
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            var userLoginResponse=response.body()
                            Log.d("token: ", userLoginResponse!!.token)
                            Log.d("userData: ",userLoginResponse!!.userData.toString())

                            Prefs.write()
                                .content("token",userLoginResponse.token)
                                .content("userData", getUserDataJsonFromObject(userLoginResponse.userData))
                                .apply()

                            Toast.makeText(context, "Refreshed", Toast.LENGTH_LONG).show()
                        }
                        else {
                            Toast.makeText(context, Resources.getSystem().getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
                            Log.e("RETROFIT_ERROR", response.code().toString())
                        }
                    }
                }
                catch(e: Exception){
                    Toast.makeText(context, Resources.getSystem().getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
                    Log.e("Exception: ", e.toString())
                }
            }

        }
    }
}