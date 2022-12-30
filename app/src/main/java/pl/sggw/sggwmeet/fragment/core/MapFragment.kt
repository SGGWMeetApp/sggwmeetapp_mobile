package pl.sggw.sggwmeet.fragment.core

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentMapBinding
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.fragment.core.placedetails.PlaceDetailsFragment
import pl.sggw.sggwmeet.util.MarkerBitmapGenerator
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.PlacesViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

    companion object {
        private const val DEFAULT_ZOOM = 15.0f

        private const val TAG = "MapFragment"
    }

    private val placesViewModel by viewModels<PlacesViewModel>()
    @Inject
    lateinit var markerBitmapGenerator: MarkerBitmapGenerator
    @Inject
    lateinit var picasso: Picasso

    lateinit var map : GoogleMap
    lateinit var binding : FragmentMapBinding

    private val markerIdsToPlacesData : MutableMap<Marker, PlaceMarkerData> = HashMap()
    lateinit var chosenPlaceId : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentMapBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customizeMap()
        setListeners()
    }

    private fun setListeners() {
        setButtonListeners()
        setViewModelListeners()
        setClosePlaceDetailsButtonPopupListener()
        setPlaceDetailsButtonPopupListener()
    }

    private fun setPlaceDetailsButtonPopupListener() {
        binding.placeDescriptionViewBT.setOnClickListener {

            val bundle = bundleOf(
                Pair(PlaceDetailsFragment.PLACE_ID_BUNDLE_KEY, chosenPlaceId)
            )
            this.findNavController().navigate(R.id.action_mapFragment_to_placeDetailsFragment, bundle)

        }
    }

    private fun setClosePlaceDetailsButtonPopupListener() {
        binding.placeDescriptionCloseBT.setOnClickListener {
            binding.placeDescriptionCV.visibility = View.GONE
        }
    }

    private fun setOnMarkerClickListener() {
        map.setOnMarkerClickListener {

            val data = markerIdsToPlacesData[it]!!
            if(PlaceCategory.ROOT_LOCATION != data.category) {
                chosenPlaceId = data.id

                binding.placeDescriptionNameTV.text = data.name
                loadImageBasedOnPath(data)
                binding.placeDescriptionCV.visibility = View.VISIBLE
            }

            true
        }
    }

    private fun loadImageBasedOnPath(data : PlaceMarkerData) {
        data.photoPath?.let { photoPath ->
            picasso
                .load(photoPath)
                .placeholder(R.drawable.asset_loading)
                .into(binding.placeDescriptionIV)
        } ?: run {
            picasso
                .load(R.drawable.asset_no_image_available)
                .into(binding.placeDescriptionIV)
        }
    }

    private fun customizeMap() {

        val callback = OnMapReadyCallback { map ->

            this.map = map
            showMapButtons()
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
            setOnMarkerClickListener()
            placesViewModel.getPlaceMarkers(null)
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }

    private fun showMapButtons() {
        binding.arrowBT.visibility = View.VISIBLE
        binding.zoomInBT.visibility = View.VISIBLE
        binding.zoomOutBT.visibility = View.VISIBLE
    }

    private fun setButtonListeners() {
        binding.zoomInBT.setOnClickListener {
            map.moveCamera(CameraUpdateFactory.zoomIn())
        }

        binding.zoomOutBT.setOnClickListener {
            map.moveCamera(CameraUpdateFactory.zoomOut())
        }
    }

    private fun setViewModelListeners() {
        placesViewModel.placeMarkerListState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    binding.mapPB.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.mapPB.visibility = View.GONE
                    reloadMarkers(resource.data!!)
                }
                is Resource.Error -> {
                    binding.mapPB.visibility = View.GONE
                    showErrorMessage()
                }
            }
        }
    }

    private fun reloadMarkers(markers: List<PlaceMarkerData>) {
        clearOldMarkers()

        markers.forEach { markerData ->
            if(PlaceCategory.ROOT_LOCATION == markerData.category) {
                zoomToRootLocation(markerData)
            }
            val marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(markerData.geolocation.latitude, markerData.geolocation.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(markerBitmapGenerator.generatePlaceBitmap(markerData)))
            )
            markerIdsToPlacesData[marker!!] = markerData
        }
    }

    private fun clearOldMarkers() {
        markerIdsToPlacesData.forEach { it.key.remove() }
        markerIdsToPlacesData.clear()
    }

    private fun showErrorMessage() {
        Toast.makeText(context, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun zoomToRootLocation(marker : PlaceMarkerData) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(marker.geolocation.latitude, marker.geolocation.longitude), DEFAULT_ZOOM))
    }
}