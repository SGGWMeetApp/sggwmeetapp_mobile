package pl.sggw.sggwmeet.exception

class ServerException(
    val errorCode : String,
    override val message : String
) : RuntimeException(message){
}