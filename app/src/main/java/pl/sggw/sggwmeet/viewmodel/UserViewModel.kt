package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.domain.UserChangePasswordData
import pl.sggw.sggwmeet.model.connector.dto.request.UserEditUserDataRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserNotificationSettingsRequest
import pl.sggw.sggwmeet.model.connector.dto.response.UserNotificationSettingsResponse
import pl.sggw.sggwmeet.model.repository.UserRepository
import pl.sggw.sggwmeet.util.Resource
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _changePasswordState: MutableLiveData<Resource<Nothing>> = MutableLiveData()
    val changePasswordState: LiveData<Resource<Nothing>>
        get() = _changePasswordState

    fun changePassword(userChangePasswordData: UserChangePasswordData) {
        viewModelScope.launch {
            userRepository.changePassword(userChangePasswordData).onEach {
                _changePasswordState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _userEditState: MutableLiveData<Resource<Nothing>> = MutableLiveData()
    val userEditState: LiveData<Resource<Nothing>>
        get() = _userEditState

    fun userEdit(userEditRequest: UserEditUserDataRequest, userId: Int) {
        viewModelScope.launch {
            userRepository.userEdit(userEditRequest, userId).onEach {
                _userEditState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _uploadAvatarState: MutableLiveData<Resource<Nothing>> = MutableLiveData()
    val uploadAvatarState: LiveData<Resource<Nothing>>
        get() = _uploadAvatarState

    fun uploadAvatar(inputStream: InputStream, userId: Int) {
        viewModelScope.launch {
            userRepository.uploadAvatar(inputStream, userId).onEach {
                _uploadAvatarState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _getUserNotificationGetState: MutableLiveData<Resource<UserNotificationSettingsResponse>> = MutableLiveData()
    val getUserNotificationGetState: LiveData<Resource<UserNotificationSettingsResponse>>
        get() = _getUserNotificationGetState

    fun getNotificationSettings(userId: Int) {
        viewModelScope.launch {
            userRepository.getNotificationSettings(userId).onEach {
                _getUserNotificationGetState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _setUserNotificationGetState: MutableLiveData<Resource<UserNotificationSettingsResponse>> = MutableLiveData()
    val setUserNotificationGetState: LiveData<Resource<UserNotificationSettingsResponse>>
        get() = _setUserNotificationGetState

    fun setNotificationSettings(userSettings: UserNotificationSettingsRequest, userId: Int) {
        viewModelScope.launch {
            userRepository.setNotificationSettings(userSettings, userId).onEach {
                _setUserNotificationGetState.value = it
            }
                .launchIn(viewModelScope)
        }
    }
}