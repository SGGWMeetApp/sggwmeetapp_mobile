package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.model.repository.ReviewRepository
import pl.sggw.sggwmeet.util.Resource
import javax.inject.Inject

@HiltViewModel
class ReviewsViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _reviewLikeStateState: MutableLiveData<Resource<String>> = MutableLiveData()
    val reviewLikeStateListState: LiveData<Resource<String>>
        get() = _reviewLikeStateState

    private val _reviewDislikeStateState: MutableLiveData<Resource<String>> = MutableLiveData()
    val reviewDislikeStateListState: LiveData<Resource<String>>
        get() = _reviewDislikeStateState

    fun like(placeId: String, reviewId: String) {
        viewModelScope.launch {
            reviewRepository.like(placeId, reviewId).onEach {
                _reviewLikeStateState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    fun dislike(placeId: String, reviewId: String) {
        viewModelScope.launch {
            reviewRepository.dislike(placeId, reviewId).onEach {
                _reviewDislikeStateState.value = it
            }
                .launchIn(viewModelScope)
        }
    }
}