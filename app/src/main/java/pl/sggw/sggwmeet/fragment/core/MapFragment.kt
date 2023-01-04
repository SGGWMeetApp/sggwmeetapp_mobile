package pl.sggw.sggwmeet.fragment.core

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.adapter.PlacesAdapter
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

        private const val LOCATION_UPDATE_INTERVAL = 100L
        private const val LOCATION_UPDATE_DISTANCE = 0.01F
    }

    lateinit var placesDialog: AlertDialog
    lateinit var adapter: PlacesAdapter
    private val placesViewModel by viewModels<PlacesViewModel>()
    @Inject
    lateinit var locationManager: LocationManager


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
        this.setupPlacesListCardView()
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
            placesViewModel.getPlaceMarkers(arrayOf(null))
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

        binding.arrowBT.setOnClickListener {
            this.placesDialog.show()
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
                    this.adapter.submitItems(resource.data)
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


    private fun setupPlacesListCardView() {
        this.setupLocationManager()

        this.binding.arrowBT.setOnClickListener {
            this.showPlacesListCardView(true)
        }
        this.binding.placesListArrowButton.setOnClickListener {
            this.showPlacesListCardView(false)
        }

        this.adapter = PlacesAdapter(this.picasso)
        with(this.binding.placesListRecyclerView) {
            this.layoutManager = LinearLayoutManager(this@MapFragment.requireContext())
            this.adapter = this@MapFragment.adapter
        }
        this.setupPlacesListSpinner()

        LocationListenerImpl.userLocation.observe(this.viewLifecycleOwner) {
            this.adapter.submitUserLocation(it)
        }
    }

    private fun showPlacesListCardView(show: Boolean) {
        fun visible(visible: Boolean): Int {
            return if (visible) View.VISIBLE
            else View.GONE
        }

        with (this.binding) {
            this.arrowBT.visibility = visible(!show)
            this.zoomInBT.visibility = visible(!show)
            this.zoomOutBT.visibility = visible(!show)
            this.placesListCardView.visibility = visible(show)
        }
    }

    private fun setupPlacesListSpinner() {
        val spinner = this.binding.placesListCategorySpinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            private fun changePlacesCategory(category: PlaceCategory?) {
                this@MapFragment.placesViewModel.getPlaceMarkers(arrayOf(category))
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val translation = spinner.selectedItem as String
                val category = PlaceCategory.getCategoryByTranslation(translation)
                if (category == PlaceCategory.ALL) this.changePlacesCategory(null)
                else this.changePlacesCategory(category)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this.requireContext(),
            R.layout.category_spinner_text_view,
            PlaceCategory.getPolishTranslations()
        )
        adapter.setDropDownViewResource(R.layout.category_spinner_text_view)
        spinner.adapter = adapter
    }

    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this.requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupLocationManager() {
        try {
            if (!this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) return
            if (!this.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return
            this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_UPDATE_INTERVAL,
                LOCATION_UPDATE_DISTANCE,
                LocationListenerImpl
            )
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    private object LocationListenerImpl: LocationListener {
        private val _userLocation: MutableLiveData<Location> = MutableLiveData()
        val userLocation: LiveData<Location> = this._userLocation
        override fun onLocationChanged(location: Location) {
            this._userLocation.postValue(location)
        }
    }
}