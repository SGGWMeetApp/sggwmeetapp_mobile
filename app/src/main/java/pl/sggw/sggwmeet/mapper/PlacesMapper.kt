package pl.sggw.sggwmeet.mapper

import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData

class PlacesMapper {

    fun mapToMarkers(places : List<SimplePlaceResponseData>) : List<PlaceMarkerData> {
        return places.map { mapToMarker(it) }
    }

    private fun mapToMarker(simplePlaceData : SimplePlaceResponseData) : PlaceMarkerData {
        return PlaceMarkerData(
            simplePlaceData.id,
            simplePlaceData.name,
            simplePlaceData.locationCategoryCodes[0],
            simplePlaceData.geolocation
        )
    }
}