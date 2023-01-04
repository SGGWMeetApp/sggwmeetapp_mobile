package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.domain.PlaceEvent
import pl.sggw.sggwmeet.model.connector.dto.request.EventCreatePublicRequest
import pl.sggw.sggwmeet.model.connector.dto.request.EventEditRequest
import pl.sggw.sggwmeet.model.connector.dto.response.EventAddUserResponse
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData
import pl.sggw.sggwmeet.model.repository.EventRepository
import pl.sggw.sggwmeet.util.Resource
import java.io.InputStream
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

    private val _getPlaceEventsState: MutableLiveData<Resource<List<PlaceEvent>>> = MutableLiveData()
    val getPlaceEventsState: LiveData<Resource<List<PlaceEvent>>>
        get() = _getPlaceEventsState

    fun getPlacePublicEvents(placeId: String) {
        viewModelScope.launch {
            eventRepository.getPlacePublicEvents(placeId).onEach {
                _getPlaceEventsState.value = it
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

    private val _editEventState: MutableLiveData<Resource<EventResponse>> = MutableLiveData()
    val editEventState: LiveData<Resource<EventResponse>>
        get() = _editEventState

    fun editEvent(eventEditRequest: EventEditRequest, eventId: Int) {
        viewModelScope.launch {
            eventRepository.editEvent(eventEditRequest, eventId).onEach {
                _editEventState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _createPublicEventState: MutableLiveData<Resource<EventResponse>> = MutableLiveData()
    val createPublicEventState: LiveData<Resource<EventResponse>>
        get() = _createPublicEventState

    fun createPublicEvent(eventCreateRequest: EventCreatePublicRequest) {
        viewModelScope.launch {
            eventRepository.createPublicEvent(eventCreateRequest).onEach {
                _createPublicEventState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _createGroupEventState: MutableLiveData<Resource<EventResponse>> = MutableLiveData()
    val createGroupEventState: LiveData<Resource<EventResponse>>
        get() = _createGroupEventState

    fun createGroupEvent(eventCreateRequest: EventCreatePublicRequest, group_id: Int) {
        viewModelScope.launch {
            eventRepository.createGroupEvent(eventCreateRequest, group_id).onEach {
                _createGroupEventState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _addUserToEventState: MutableLiveData<Resource<EventAddUserResponse>> = MutableLiveData()
    val addUserToEventState: LiveData<Resource<EventAddUserResponse>>
        get() = _addUserToEventState

    fun addUserToEvent(event_id: Int, user_id: Int) {
        viewModelScope.launch {
            eventRepository.addUserToEvent(event_id, user_id).onEach {
                _addUserToEventState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _deleteUserFromEventState: MutableLiveData<Resource<String>> = MutableLiveData()
    val deleteUserFromEventState: LiveData<Resource<String>>
        get() = _deleteUserFromEventState

    fun deleteUserFromEvent(event_id: Int, user_id: Int) {
        viewModelScope.launch {
            eventRepository.deleteUserFromEvent(event_id, user_id).onEach {
                _deleteUserFromEventState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _getUserEventsState: MutableLiveData<Resource<ArrayList<EventResponse>>> = MutableLiveData()
    val getUserEventsState: LiveData<Resource<ArrayList<EventResponse>>>
        get() = _getUserEventsState

    fun getUserEvents(user_id: Int) {
        viewModelScope.launch {
            eventRepository.getUserEvents(user_id).onEach {
                _getUserEventsState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _joinPublicEventState: MutableLiveData<Resource<Void>> = MutableLiveData()
    val joinPublicEventState: LiveData<Resource<Void>>
        get() = _joinPublicEventState

    fun joinPublicEvent(eventId: Int) {
        viewModelScope.launch {
            eventRepository.joinPublicEvent(eventId).onEach {
                _joinPublicEventState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _leavePublicEventState: MutableLiveData<Resource<Void>> = MutableLiveData()
    val leavePublicEventState: LiveData<Resource<Void>>
        get() = _leavePublicEventState

    fun leavePublicEvent(eventId: Int) {
        viewModelScope.launch {
            eventRepository.leavePublicEvent(eventId).onEach {
                _leavePublicEventState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _deleteEventState: MutableLiveData<Resource<Void>> = MutableLiveData()
    val deleteEventState: LiveData<Resource<Void>>
        get() = _deleteEventState

    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            eventRepository.deleteEvent(eventId).onEach {
                _deleteEventState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

}