package pl.sggw.sggwmeet.mapper

import pl.sggw.sggwmeet.domain.PlaceCategory
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
            resolveCategoryCode(simplePlaceData.locationCategoryCodes),
            simplePlaceData.geolocation
        )
    }

    private fun resolveCategoryCode(categories : List<PlaceCategory>) : PlaceCategory {
        if(categories.isEmpty()) {
            return PlaceCategory.OTHER
        }
        return categories[0]
    }
}