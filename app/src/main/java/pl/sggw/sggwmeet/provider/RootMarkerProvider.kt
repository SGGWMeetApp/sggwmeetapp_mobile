package pl.sggw.sggwmeet.provider

import pl.sggw.sggwmeet.domain.Geolocation
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceMarkerData

class RootMarkerProvider {

    companion object {
        public val ROOT_MARKER = PlaceMarkerData(
            "",
            "SGGW",
            PlaceCategory.ROOT_LOCATION,
            Geolocation(
                52.163692,
                21.044748
            )
        )
    }

    /**
     * Returns root marker (SGGW location)
     */
    fun getRootMarker() : PlaceMarkerData {
        return ROOT_MARKER
    }
}