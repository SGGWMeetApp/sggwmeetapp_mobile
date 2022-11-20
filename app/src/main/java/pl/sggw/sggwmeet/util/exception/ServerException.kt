package pl.sggw.sggwmeet.util.exception

class ServerException(
    val errorCode : String,
    override val message : String
) : RuntimeException(message){
}