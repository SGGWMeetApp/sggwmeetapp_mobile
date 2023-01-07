package pl.sggw.sggwmeet.viewmodel

import android.location.Location
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceDetails
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.model.repository.PlacesRepository
import pl.sggw.sggwmeet.util.Resource
import javax.inject.Inject

@HiltViewModel
class PlacesViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _placeMarkerListState: MutableLiveData<Resource<List<PlaceMarkerData>>> = MutableLiveData()
    val placeMarkerListState: LiveData<Resource<List<PlaceMarkerData>>>
        get() = _placeMarkerListState

    private val _placeMarkerFilteredListState: MutableLiveData<List<PlaceMarkerData>> = MutableLiveData()
    val placeMarkerFilteredListState: LiveData<List<PlaceMarkerData>> = this._placeMarkerFilteredListState

    private val _placeDetailsState: MutableLiveData<Resource<PlaceDetails>> = MutableLiveData()
    val placeDetailsState: LiveData<Resource<PlaceDetails>>
        get() = _placeDetailsState

    private var maxDistance = Long.MAX_VALUE
    private var userLocation: Location? = null

    fun observeNonFilteredPlaces(owner: LifecycleOwner) {
        _placeMarkerListState.observe(owner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    val markers = resource.data!!
                    if (this.userLocation == null) {
                        this._placeMarkerFilteredListState.value = markers
                        return@observe
                    }
                    val filteredMarkers = mutableListOf<PlaceMarkerData>()
                    for (marker in markers)
                        if (marker.geolocation.toLocation().distanceTo(userLocation!!) <= maxDistance) filteredMarkers.add(marker)
                    this._placeMarkerFilteredListState.value = filteredMarkers
                }
                else -> {

                }
            }
        }
    }

    fun setMaxDistance(distance: Long, categoryCodes: Array<PlaceCategory?>) {
        this.maxDistance = distance
        this.getPlaceMarkers(categoryCodes)
    }

    fun setUserLocation(userLocation: Location, categoryCodes: Array<PlaceCategory?>) {
        this.userLocation = userLocation
        this.getPlaceMarkers(categoryCodes)
    }

    fun getPlaceMarkers(categoryCodes: Array<PlaceCategory?>) {
        viewModelScope.launch {
            placesRepository.getSimplePlaceListForMarkers(categoryCodes).onEach {
                _placeMarkerListState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    fun getPlaceDetails(id: String) {
        viewModelScope.launch {
            placesRepository.getPlaceDetails(id).onEach {
                _placeDetailsState.value = it
            }
                .launchIn(viewModelScope)
        }
    }
}