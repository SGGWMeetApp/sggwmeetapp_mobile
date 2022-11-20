package pl.sggw.sggwmeet.model.connector.dto.response

data class PlaceListResponse(
    var places : List<SimplePlaceResponseData>
) : ErrorResponse()