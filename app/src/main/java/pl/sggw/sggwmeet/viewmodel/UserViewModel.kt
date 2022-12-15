package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.domain.UserChangePasswordData
import pl.sggw.sggwmeet.model.connector.dto.request.UserEditUserDataRequest
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
}