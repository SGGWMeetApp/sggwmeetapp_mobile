package pl.sggw.sggwmeet.util.exception

class TechnicalException(
    override val message : String
) : RuntimeException(message)