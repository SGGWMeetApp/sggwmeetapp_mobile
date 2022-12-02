package pl.sggw.sggwmeet.domain

import android.location.Location

data class Geolocation(
    var latitude : Double,
    var longitude : Double,
) {
    fun toLocation(): Location {
        val location = Location("location")
        location.latitude = this.latitude
        location.longitude = this.longitude
        return location
    }
}