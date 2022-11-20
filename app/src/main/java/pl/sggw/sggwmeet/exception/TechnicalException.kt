package pl.sggw.sggwmeet.exception

class TechnicalException(
    override val message : String
) : RuntimeException(message)