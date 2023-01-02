package pl.sggw.sggwmeet.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.sggw.sggwmeet.domain.Review
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

    private val _reviewDislikeState: MutableLiveData<Resource<String>> = MutableLiveData()
    val reviewDislikeState: LiveData<Resource<String>>
        get() = _reviewDislikeState

    private val _addReviewState: MutableLiveData<Resource<Review>> = MutableLiveData()
    val addReviewState: LiveData<Resource<Review>>
        get() = _addReviewState

    private val _editReviewState: MutableLiveData<Resource<Review>> = MutableLiveData()
    val editReviewState: LiveData<Resource<Review>>
        get() = _editReviewState

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
                _reviewDislikeState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    fun addReview(placeId: String, comment: String, isPositive: Boolean) {
        viewModelScope.launch {
            reviewRepository.addReview(placeId, comment, isPositive).onEach {
                _addReviewState.value = it
            }
                .launchIn(viewModelScope)
        }
    }

    fun editReview(placeId: String, reviewId : String, comment: String, isPositive: Boolean) {
        viewModelScope.launch {
            reviewRepository.editReview(placeId, reviewId, comment, isPositive).onEach {
                _editReviewState.value = it
            }
                .launchIn(viewModelScope)
        }
    }
}