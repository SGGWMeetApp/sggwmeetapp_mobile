package pl.sggw.sggwmeet.model.connector.dto.response

abstract class ErrorResponse() {
    var errorCode : String? = null
    var message : String? = null
}