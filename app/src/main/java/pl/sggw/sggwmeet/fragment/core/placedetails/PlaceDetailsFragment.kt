package pl.sggw.sggwmeet.fragment.core.placedetails

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentPlaceDetailsBinding
import pl.sggw.sggwmeet.domain.PlaceDetails
import pl.sggw.sggwmeet.domain.Review
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.fragment.core.placedetails.adapters.EventRecyclerViewAdapter
import pl.sggw.sggwmeet.fragment.core.placedetails.adapters.FoodItemRecyclerViewAdapter
import pl.sggw.sggwmeet.fragment.core.placedetails.adapters.FoodItemsSectionRecyclerViewAdapter
import pl.sggw.sggwmeet.fragment.core.placedetails.adapters.ReviewRecyclerViewAdapter
import pl.sggw.sggwmeet.fragment.core.placedetails.adapters.model.FoodMenuSection
import pl.sggw.sggwmeet.ui.dialog.ReviewDialog
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.EventViewModel
import pl.sggw.sggwmeet.viewmodel.FoodMenuViewModel
import pl.sggw.sggwmeet.viewmodel.PlacesViewModel
import pl.sggw.sggwmeet.viewmodel.ReviewsViewModel
import java.text.DecimalFormat
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
    private val eventViewModel by viewModels<EventViewModel>()
    private val foodMenuViewModel by viewModels<FoodMenuViewModel>()
    @Inject
    lateinit var picasso: Picasso

    private lateinit var reviewAdapter: ReviewRecyclerViewAdapter
    private lateinit var eventsAdapter: EventRecyclerViewAdapter
    private lateinit var foodMenuAdapter: FoodItemsSectionRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        initReviewRecyclerView()
        initEventsRecyclerView()
        initFoodMenuAdapter()
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

        reviewAdapter.setOnEditClickListener { review ->
            promptEditReviewDialog(review)
        }
    }

    private fun initEventsRecyclerView() {
        eventsAdapter = EventRecyclerViewAdapter(requireContext())
        binding.eventsRV.layoutManager = LinearLayoutManager(requireContext())

        binding.eventsRV.adapter = eventsAdapter
    }

    private fun initFoodMenuAdapter() {
        foodMenuAdapter = FoodItemsSectionRecyclerViewAdapter(requireContext(), picasso)
        binding.foodMenuRV.layoutManager = LinearLayoutManager(requireContext())

        binding.foodMenuRV.adapter = foodMenuAdapter
    }

    private fun promptEditReviewDialog(review: Review) {
        ReviewDialog(requireActivity(), review)
            .onAddButtonClick { actions, comment ->
                if(comment.text.toString().isNotBlank()) {
                    actions.dismissAlertDialog()
                    val data = actions.getReviewData()
                    reviewsViewModel.editReview(placeId, review.id, data.comment, data.isPositive)
                    reviewAdapter.markAsEditing(review.id)
                } }
            .onBackButtonClick { actions ->
                actions.dismissAlertDialog()
            }
            .startAlertDialog()
    }

    private fun setListeners() {
        setViewModelListeners()
        setButtonListeners()
    }

    private fun setButtonListeners() {
        binding.descriptionSectionBT.setOnClickListener{
            if(binding.descriptionPanel.visibility != View.VISIBLE) {
                showDescriptionPanel()
            }
        }

        binding.reviewsSectionBT.setOnClickListener {
            if(binding.reviewsPanel.visibility != View.VISIBLE) {
                showReviewsPanel()
            }
        }

        binding.eventsSectionBT.setOnClickListener {
            if(binding.eventsPanel.visibility != View.VISIBLE) {
                showEventsPanel()
            }
        }

        binding.menuSectionBT.setOnClickListener {
            if(binding.menuPanel.visibility != View.VISIBLE) {
                showMenuPanel()
            }
        }

        binding.addReviewBT.setOnClickListener {
            promptAddReviewDialog()
        }

        binding.backBT.setOnClickListener {
            this.findNavController().navigate(R.id.action_placeDetailsFragment_to_mapFragment)
        }
    }

    private fun promptAddReviewDialog() {
        ReviewDialog(requireActivity())
            .onAddButtonClick { actions, comment ->
                if(comment.text.toString().isNotBlank()) {
                    actions.dismissAlertDialog()
                    val data = actions.getReviewData()
                    reviewsViewModel.addReview(placeId, data.comment, data.isPositive)

            } }
            .onBackButtonClick { actions ->
                actions.dismissAlertDialog()
            }
            .startAlertDialog()
    }

    private fun setViewModelListeners() {
        placesViewModel.placeDetailsState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    //TODO
                }
                is Resource.Success -> {
                    injectDataToView(resource.data!!)
                    fetchMenuIfPossible(resource.data.menuPath)
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

        reviewsViewModel.reviewDislikeState.observe(viewLifecycleOwner) { resource ->
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

        reviewsViewModel.addReviewState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockAddReviewButton()
                }
                is Resource.Success -> {
                    unlockAddReviewButton()
                    reviewAdapter.addItemOnTop(resource.data!!)
                    recalculateReviewSummaryData(reviewAdapter.currentList)
                    Toast.makeText(requireContext(), "Pomyślnie dodano recenzje", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    unlockAddReviewButton()
                    if(resource.exception is ServerException ) {
                        if(ServerException.REVIEW_DUPLICATION_CODE == resource.exception.errorCode) {
                            Toast.makeText(requireContext(), "Utworzyłeś/aś już recenzję dla tego miejsca.", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(requireContext(), "Nie udało się dodać recenzji", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Nie udało się dodać recenzji", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        reviewsViewModel.editReviewState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    //TODO
                }
                is Resource.Success -> {
                    reviewAdapter.confirmEdit(resource.data!!)
                    recalculateReviewSummaryData(reviewAdapter.currentList)
                    Toast.makeText(requireContext(), "Pomyślnie edytowano recenzje", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    reviewAdapter.cancelReviewEditing()
                    Toast.makeText(requireContext(), "Nie udało się edytować recenzji", Toast.LENGTH_SHORT).show()
                }
            }
        }

        eventViewModel.getPlaceEventsState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    triggerEventsPanelLoading()
                }
                is Resource.Success -> {
                    eventsAdapter.submitList(resource.data)
                    showEventList()
                    binding.eventsCountTV.text = getString(R.string.events_count_TV, resource.data!!.size)
                }
                is Resource.Error -> {
                    turnOffEventsPanelLoading()
                    Toast.makeText(requireContext(), "Nie udało się pobrać wydarzeń", Toast.LENGTH_SHORT).show()
                }
            }
        }

        foodMenuViewModel.getFoodMenuState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    showMenuLoading()
                }
                is Resource.Success -> {
                    foodMenuAdapter.submitList(
                        resource.data!!.itemsByCategory.entries.map { FoodMenuSection(it.key, it.value) }
                    )
                    showMenuLoaded()
                }
                is Resource.Error -> {
                    showMenuLoaded()
                    Toast.makeText(requireContext(), "Błąd podczas ładowania menu!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showMenuLoading() {
        binding.foodMenuLoadingPB.visibility = View.VISIBLE
        binding.foodMenuRV.visibility = View.GONE
    }

    private fun showMenuLoaded() {
        binding.foodMenuLoadingPB.visibility = View.GONE
        binding.foodMenuRV.visibility = View.VISIBLE
    }

    private fun fetchMenuIfPossible(menuPath: String?) {
        if(menuPath != null) {
            foodMenuViewModel.getFoodMenu(menuPath)
        }
    }

    private fun recalculateReviewSummaryData(currentList: List<Review>) {
        val reviewsCount = currentList.size
        val positivePercent = currentList.filter { it.isPositive }.size.toFloat() * 100 / reviewsCount

        binding.placeReviewPositivePercentReviewPanelTV.text = getString(R.string.place_review_count_reviews_panel_TV, positivePercent.toInt(), reviewsCount)
        binding.placeReviewCountTV.text = getString(R.string.place_review_count_TV, reviewsCount)
        binding.placeReviewPositivePercentTV.text = DecimalFormat("#.00").format(positivePercent)

    }

    private fun unlockAddReviewButton() {
        binding.addReviewBT.isEnabled = true
        binding.addReviewBT.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.asset_plus))
    }

    private fun lockAddReviewButton() {
        binding.addReviewBT.isEnabled = false
        binding.addReviewBT.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.asset_loading))
    }

    private fun injectDataToView(data: PlaceDetails) {
        hideLoadingBar()
        injectTextValues(data)
        loadPlaceImage(data)
        injectDescription(data.description)
        injectReviews(data)
        showMainLayout()
        showReviewsPanel()
    }

    private fun injectReviews(data: PlaceDetails) {
        reviewAdapter.submitList(data.reviews)
        binding.placeReviewPositivePercentReviewPanelTV.text = getString(R.string.place_review_count_reviews_panel_TV, data.positivePercent.toInt(), data.reviewsCount)
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
        eventViewModel.getPlacePublicEvents(placeId)
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

    private fun triggerEventsPanelLoading() {
        binding.eventsLoadingPB.visibility = View.VISIBLE
        binding.eventsContentPanel.visibility = View.GONE
        eventsAdapter.submitList(arrayListOf())
    }

    private fun showEventList() {
        binding.eventsLoadingPB.visibility = View.GONE
        binding.eventsContentPanel.visibility = View.VISIBLE
    }

    private fun turnOffEventsPanelLoading() {
        binding.eventsLoadingPB.visibility = View.GONE
    }
}