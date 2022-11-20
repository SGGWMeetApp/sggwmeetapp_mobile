package pl.sggw.sggwmeet.util.exception

class ClientException(
    override val message : String,
    val errorCode : ClientErrorCode
) : RuntimeException(message) {
}