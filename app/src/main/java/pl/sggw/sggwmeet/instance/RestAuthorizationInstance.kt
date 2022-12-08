package pl.sggw.sggwmeet.instance

import pl.sggw.sggwmeet.model.connector.AuthorizationConnector

object RestAuthorizationInstance {
    private val retrofitInstance = RetrofitInstance
    val service = retrofitInstance.retrofit.create(AuthorizationConnector::class.java)
}