package pl.sggw.sggwmeet.fragment.core

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentPlaceDetailsBinding
import pl.sggw.sggwmeet.domain.PlaceDetails
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.PlacesViewModel
import javax.inject.Inject

@AndroidEntryPoint
class PlaceDetailsFragment : Fragment(R.layout.fragment_place_details) {

    companion object {
        const val PLACE_ID_BUNDLE_KEY = "id"
    }

    lateinit var binding : FragmentPlaceDetailsBinding
    private val placesViewModel by viewModels<PlacesViewModel>()
    @Inject
    lateinit var picasso: Picasso

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        placesViewModel.getPlaceDetails(requireArguments().getString(PLACE_ID_BUNDLE_KEY, ""))
    }

    private fun setListeners() {
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
    }

    private fun injectDataToView(data: PlaceDetails) {
        hideLoadingBar()
        injectTextValues(data)
        loadPlaceImage(data)
        showMainLayout()
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
        binding.placeReviewPositivePercentTV.text = (data.positivePercent * 100).toString()
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
}