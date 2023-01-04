package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.domain.FoodMenu
import pl.sggw.sggwmeet.model.repository.MenuRepository
import pl.sggw.sggwmeet.util.Resource
import javax.inject.Inject

@HiltViewModel
class FoodMenuViewModel @Inject constructor(
    private val repository: MenuRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _getFoodMenuState: MutableLiveData<Resource<FoodMenu>> = MutableLiveData()
    val getFoodMenuState: LiveData<Resource<FoodMenu>>
        get() = _getFoodMenuState

    fun getFoodMenu(menuPath: String) {
        viewModelScope.launch {
            repository.getFoodMenu(menuPath).onEach {
                _getFoodMenuState.value = it
            }
                .launchIn(viewModelScope)
        }
    }
}