package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.model.connector.dto.request.GroupAddUserRequest
import pl.sggw.sggwmeet.model.connector.dto.request.GroupCreateRequest
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

}