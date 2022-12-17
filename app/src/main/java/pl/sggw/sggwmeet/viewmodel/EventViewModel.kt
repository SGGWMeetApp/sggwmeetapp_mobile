package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData
import pl.sggw.sggwmeet.model.repository.EventRepository
import pl.sggw.sggwmeet.util.Resource
import javax.inject.Inject

@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _getAllEventsState: MutableLiveData<Resource<ArrayList<EventResponse>>> = MutableLiveData()
    val getAllEventsState: LiveData<Resource<ArrayList<EventResponse>>>
        get() = _getAllEventsState

    fun getAllEvents() {
        viewModelScope.launch {
            eventRepository.getAllEvents().onEach {
                _getAllEventsState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _getUpcomingEventsState: MutableLiveData<Resource<ArrayList<EventResponse>>> = MutableLiveData()
    val getUpcomingEventsState: LiveData<Resource<ArrayList<EventResponse>>>
        get() = _getUpcomingEventsState

    fun getUpcomingEvents() {
        viewModelScope.launch {
            eventRepository.getUpcomingEvents().onEach {
                _getUpcomingEventsState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _getAllPlacesState: MutableLiveData<Resource<List<SimplePlaceResponseData>>> = MutableLiveData()
    val getAllPlacesState: LiveData<Resource<List<SimplePlaceResponseData>>>
        get() = _getAllPlacesState

    fun getAllPlaces() {
        viewModelScope.launch {
            eventRepository.getAllPlaces().onEach {
                _getAllPlacesState.value = it
            }
                .launchIn(viewModelScope)
        }
    }
}