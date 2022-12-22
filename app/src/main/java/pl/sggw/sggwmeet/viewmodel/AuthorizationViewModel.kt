package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.domain.UserCredentials
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.model.connector.dto.request.ResetPasswordRequest
import pl.sggw.sggwmeet.model.connector.dto.request.UserRegisterRequestData
import pl.sggw.sggwmeet.model.connector.dto.response.ResetPasswordResponse
import pl.sggw.sggwmeet.model.repository.AuthorizationRepository
import pl.sggw.sggwmeet.util.Resource
import javax.inject.Inject

@HiltViewModel
class AuthorizationViewModel @Inject constructor(
    private val authorizationRepository: AuthorizationRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _loginState: MutableLiveData<Resource<UserData>> = MutableLiveData()
    val loginState: LiveData<Resource<UserData>>
        get() = _loginState

    private val _registerState: MutableLiveData<Resource<UserData>> = MutableLiveData()
    val registerState: LiveData<Resource<UserData>>
        get() = _registerState

    fun login(userCredentials: UserCredentials) {
        viewModelScope.launch {
            authorizationRepository.login(userCredentials).onEach {
                _loginState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    fun register(userCredentials: UserCredentials, userData: UserRegisterRequestData) {
        viewModelScope.launch {
            authorizationRepository.register(userCredentials, userData).onEach {
                _registerState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    private val _resetPasswordState: MutableLiveData<Resource<ResetPasswordResponse>> = MutableLiveData()
    val resetPasswordState: LiveData<Resource<ResetPasswordResponse>>
        get() = _resetPasswordState

    fun resetPassword(resetPasswordRequest: ResetPasswordRequest) {
        viewModelScope.launch {
            authorizationRepository.resetPassword(resetPasswordRequest).onEach {
                _resetPasswordState.value = it
            }
                .launchIn(viewModelScope)
        }
    }
}