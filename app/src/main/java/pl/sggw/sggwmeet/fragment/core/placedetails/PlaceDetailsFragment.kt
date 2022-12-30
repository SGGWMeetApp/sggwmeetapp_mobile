package pl.sggw.sggwmeet.fragment.core.placedetails

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentPlaceDetailsBinding
import pl.sggw.sggwmeet.domain.PlaceDetails
import pl.sggw.sggwmeet.domain.Review
import pl.sggw.sggwmeet.fragment.core.placedetails.adapters.ReviewRecyclerViewAdapter
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.PlacesViewModel
import pl.sggw.sggwmeet.viewmodel.ReviewsViewModel
import javax.inject.Inject

@AndroidEntryPoint
class PlaceDetailsFragment : Fragment(R.layout.fragment_place_details) {

    companion object {
        const val PLACE_ID_BUNDLE_KEY = "id"
    }

    lateinit var binding : FragmentPlaceDetailsBinding
    lateinit var placeId : String
    private val placesViewModel by viewModels<PlacesViewModel>()
    private val reviewsViewModel by viewModels<ReviewsViewModel>()
    @Inject
    lateinit var picasso: Picasso

    private lateinit var reviewAdapter: ReviewRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        initReviewRecyclerView()
        placeId = requireArguments().getString(PLACE_ID_BUNDLE_KEY, "")
        placesViewModel.getPlaceDetails(placeId)
    }

    private fun initReviewRecyclerView() {
        reviewAdapter = ReviewRecyclerViewAdapter(requireContext(), picasso)
        binding.reviewsRV.layoutManager = LinearLayoutManager(requireContext())

        binding.reviewsRV.adapter = reviewAdapter

        reviewAdapter.setOnAllowedLikeClickListener { reviewId ->
            reviewsViewModel.like(placeId, reviewId)
        }

        reviewAdapter.setOnAllowedDislikeClickListener { reviewId ->
            reviewsViewModel.dislike(placeId, reviewId)
        }
    }

    private fun setListeners() {
        setViewModelListeners()
        setButtonListeners()
    }

    private fun setButtonListeners() {
        binding.descriptionSectionBT.setOnClickListener{
            showDescriptionPanel()
        }

        binding.reviewsSectionBT.setOnClickListener {
            showReviewsPanel()
        }

        binding.eventsSectionBT.setOnClickListener {
            showEventsPanel()
        }

        binding.menuSectionBT.setOnClickListener {
            showMenuPanel()
        }
    }

    private fun setViewModelListeners() {
        placesViewModel.placeDetailsState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    //TODO
                }
                is Resource.Success -> {
                    injectDataToView(resource.data!!)
                }
                is Resource.Error -> {
                    //TODO
                }
            }
        }

        reviewsViewModel.reviewLikeStateListState.observe(viewLifecycleOwner) {resource ->
            when(resource) {
                is Resource.Loading -> {
                    //TODO
                }
                is Resource.Success -> {
                    reviewAdapter.confirmUserLike()
                }
                is Resource.Error -> {
                    reviewAdapter.cancelLikeOrDislikeProcessing()
                }
            }
        }

        reviewsViewModel.reviewDislikeStateListState.observe(viewLifecycleOwner) {resource ->
            when(resource) {
                is Resource.Loading -> {
                    //TODO
                }
                is Resource.Success -> {
                    reviewAdapter.confirmUserDislike()
                }
                is Resource.Error -> {
                    reviewAdapter.cancelLikeOrDislikeProcessing()
                }
            }
        }
    }

    private fun injectDataToView(data: PlaceDetails) {
        hideLoadingBar()
        injectTextValues(data)
        loadPlaceImage(data)
        injectDescription(data.description)
        injectReviews(data.reviews)
        showMainLayout()
        showReviewsPanel()
    }

    private fun injectReviews(reviews : List<Review>) {
        reviewAdapter.submitList(reviews)
    }

    private fun injectDescription(description: String?) {
        description?.let {
            binding.descriptionTV.text = it
        } ?: run {
            binding.descriptionTV.text = getString(R.string.no_description_info)
        }
    }

    private fun loadPlaceImage(data: PlaceDetails) {
        data.photoPath?.let { photoPath ->
            picasso
                .load(photoPath)
                .placeholder(R.drawable.asset_loading)
                .into(binding.placeImageIV)
        } ?: run {
            picasso
                .load(R.drawable.asset_no_image_available)
                .into(binding.placeImageIV)
        }
    }

    private fun injectTextValues(data: PlaceDetails) {
        binding.placeNameTV.text = data.name
        binding.placeReviewPositivePercentTV.text = data.positivePercent.toString()
        binding.placeReviewCountTV.text = getString(R.string.place_review_count_TV, data.reviewsCount)
        binding.placeReviewCountTV.paintFlags = binding.placeReviewCountTV.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        binding.placeAddressTV.text = data.textLocation
    }

    private fun hideLoadingBar() {
        binding.loadingPB.visibility = View.GONE
    }

    private fun showMainLayout() {
        binding.mainLayout.visibility = View.VISIBLE
    }

    private fun showDescriptionPanel() {
        hideReviewsPanel()
        hideEventsPanel()
        hideMenuPanel()

        binding.descriptionSectionBTUnderline.visibility = View.VISIBLE
        binding.descriptionPanel.visibility = View.VISIBLE
    }

    private fun showReviewsPanel() {
        hideDescriptionPanel()
        hideEventsPanel()
        hideMenuPanel()

        binding.reviewsSectionBTUnderline.visibility = View.VISIBLE
        binding.reviewsPanel.visibility = View.VISIBLE
    }

    private fun showEventsPanel() {
        hideDescriptionPanel()
        hideReviewsPanel()
        hideMenuPanel()

        binding.eventsSectionBTUnderline.visibility = View.VISIBLE
        binding.eventsPanel.visibility = View.VISIBLE
    }

    private fun showMenuPanel() {
        hideDescriptionPanel()
        hideReviewsPanel()
        hideEventsPanel()

        binding.menuSectionBTUnderline.visibility = View.VISIBLE
        binding.menuPanel.visibility = View.VISIBLE
    }

    private fun hideDescriptionPanel() {
        binding.descriptionSectionBTUnderline.visibility = View.GONE
        binding.descriptionPanel.visibility = View.GONE
    }

    private fun hideReviewsPanel() {
        binding.reviewsSectionBTUnderline.visibility = View.GONE
        binding.reviewsPanel.visibility = View.GONE
    }

    private fun hideEventsPanel() {
        binding.eventsSectionBTUnderline.visibility = View.GONE
        binding.eventsPanel.visibility = View.GONE
    }

    private fun hideMenuPanel() {
        binding.menuSectionBTUnderline.visibility = View.GONE
        binding.menuPanel.visibility = View.GONE
    }
}