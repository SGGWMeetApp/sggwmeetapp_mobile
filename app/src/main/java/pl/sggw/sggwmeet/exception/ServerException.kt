package pl.sggw.sggwmeet.exception

class ServerException(
    val errorCode : String,
    override val message : String
) : RuntimeException(message) {

    companion object {
        const val REVIEW_DUPLICATION_CODE = "REVIEW_DUPLICATED"
        const val REVIEW_DUPLICATION_MESSAGE = "Rating by this user already exists for this place."
    }
}