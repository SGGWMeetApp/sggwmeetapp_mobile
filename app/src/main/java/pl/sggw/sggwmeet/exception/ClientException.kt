package pl.sggw.sggwmeet.exception

class ClientException(
    override val message : String,
    val errorCode : ClientErrorCode
) : RuntimeException(message) {
}