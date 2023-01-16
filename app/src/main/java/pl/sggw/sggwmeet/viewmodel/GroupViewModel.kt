package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.model.connector.dto.request.GroupAddUserRequest
import pl.sggw.sggwmeet.model.connector.dto.request.GroupCreateRequest
import pl.sggw.sggwmeet.model.connector.dto.request.GroupEventNotificationRequest
import pl.sggw.sggwmeet.model.connector.dto.response.*
import pl.sggw.sggwmeet.model.repository.GroupRepository
import pl.sggw.sggwmeet.util.Resource
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _getAllGroupsState: MutableLiveData<Resource<GetGroupsResponse>> = MutableLiveData()
    val getAllGroupsState: LiveData<Resource<GetGroupsResponse>>
        get() = _getAllGroupsState

    fun getAllGroups() {
        viewModelScope.launch {
            groupRepository.getAllGroups().onEach {
                _getAllGroupsState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _getUserGroupsState: MutableLiveData<Resource<GetGroupsResponse>> = MutableLiveData()
    val getUserGroupsState: LiveData<Resource<GetGroupsResponse>>
        get() = _getUserGroupsState

    fun getUserGroups(user_id:Int) {
        viewModelScope.launch {
            groupRepository.getUserGroups(user_id).onEach {
                _getUserGroupsState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _createNewGroupGetState: MutableLiveData<Resource<GroupCreateResponse>> = MutableLiveData()
    val createNewGroupGetState: LiveData<Resource<GroupCreateResponse>>
        get() = _createNewGroupGetState

    fun createNewGroup(groupCreateRequest: GroupCreateRequest) {
        viewModelScope.launch {
            groupRepository.createNewGroup(groupCreateRequest).onEach {
                _createNewGroupGetState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _addUserToGroupGetState: MutableLiveData<Resource<GroupAddUserResponse>> = MutableLiveData()
    val addUserToGroupGetState: LiveData<Resource<GroupAddUserResponse>>
        get() = _addUserToGroupGetState

    fun addUserToGroup(addUserRequest: GroupAddUserRequest, group_id:Int) {
        viewModelScope.launch {
            groupRepository.addUserToGroup(addUserRequest, group_id).onEach {
                _addUserToGroupGetState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _getGroupMembersGetState: MutableLiveData<Resource<GroupGetMembersResponse>> = MutableLiveData()
    val getGroupMembersGetState: LiveData<Resource<GroupGetMembersResponse>>
        get() = _getGroupMembersGetState

    fun getGroupMembers(group_id:Int) {
        viewModelScope.launch {
            groupRepository.getGroupMembers(group_id).onEach {
                _getGroupMembersGetState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _getGroupEventsGetState: MutableLiveData<Resource<GetEventResponse>> = MutableLiveData()
    val getGroupEventsGetState: LiveData<Resource<GetEventResponse>>
        get() = _getGroupEventsGetState

    fun getGroupEvents(group_id:Int) {
        viewModelScope.launch {
            groupRepository.getGroupEvents(group_id).onEach {
                _getGroupEventsGetState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _switchGroupNotificationGetState: MutableLiveData<Resource<EventResponse>> = MutableLiveData()
    val switchGroupNotificationGetState: LiveData<Resource<EventResponse>>
        get() = _switchGroupNotificationGetState

    fun switchGroupNotification(notificationRequest: GroupEventNotificationRequest, group_id:Int, event_id:Int) {
        viewModelScope.launch {
            groupRepository.switchGroupNotification(notificationRequest, group_id, event_id).onEach {
                _switchGroupNotificationGetState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _deleteGroupEventGetState: MutableLiveData<Resource<String>> = MutableLiveData()
    val deleteGroupEventGetState: LiveData<Resource<String>>
        get() = _deleteGroupEventGetState

    fun deleteGroupEvent(group_id:Int, event_id:Int) {
        viewModelScope.launch {
            groupRepository.deleteGroupEvent(group_id, event_id).onEach {
                _deleteGroupEventGetState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _deleteGroupState: MutableLiveData<Resource<String>> = MutableLiveData()
    val deleteGroupState: LiveData<Resource<String>>
        get() = _deleteGroupState

    fun deleteGroup(group_id:Int) {
        viewModelScope.launch {
            groupRepository.deleteGroup(group_id).onEach {
                _deleteGroupState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _deleteUserFromGroupState: MutableLiveData<Resource<GetGroupsResponse>> = MutableLiveData()
    val deleteUserFromGroupState: LiveData<Resource<GetGroupsResponse>>
        get() = _deleteUserFromGroupState

    fun deleteUser(group_id:Int, user_id:Int) {
        viewModelScope.launch {
            groupRepository.deleteUser(group_id, user_id).onEach {
                _deleteUserFromGroupState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _leaveGroupNewState: MutableLiveData<Resource<GetGroupsResponse>> = MutableLiveData()
    val leaveGroupNewState: LiveData<Resource<GetGroupsResponse>>
        get() = _leaveGroupNewState

    fun leaveGroupNew(group_id:Int, user_id:Int) {
        viewModelScope.launch {
            groupRepository.deleteUser(group_id, user_id).onEach {
                _leaveGroupNewState.value = it
            }
                .launchIn(viewModelScope)
        }
    }
}