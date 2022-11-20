package pl.sggw.sggwmeet.model.connector.rest

import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.model.connector.PlacesConnector
import pl.sggw.sggwmeet.model.connector.dto.response.PlaceListResponse

class RestPlacesConnector : PlacesConnector {

    override suspend fun getPlaces(category: PlaceCategory?): PlaceListResponse {
        //TODO implement when backend is published
        throw NotImplementedError("Not yet implemented!")
    }
}