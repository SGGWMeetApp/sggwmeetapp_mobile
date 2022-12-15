package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.domain.*
import pl.sggw.sggwmeet.model.repository.AuthorizationRepository
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

    private val _placeDetailsState: MutableLiveData<Resource<PlaceDetails>> = MutableLiveData()
    val placeDetailsState: LiveData<Resource<PlaceDetails>>
        get() = _placeDetailsState

    fun getPlaceMarkers(category: PlaceCategory?) {
        viewModelScope.launch {
            placesRepository.getSimplePlaceListForMarkers(category).onEach {
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