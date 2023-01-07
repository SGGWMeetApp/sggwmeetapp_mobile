package pl.sggw.sggwmeet.fragment.core

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.adapter.PlacesAdapter
import pl.sggw.sggwmeet.databinding.FragmentMapBinding
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.fragment.core.placedetails.PlaceDetailsFragment
import pl.sggw.sggwmeet.util.MarkerBitmapGenerator
import pl.sggw.sggwmeet.viewmodel.PlacesViewModel
import java.lang.Long.min
import javax.inject.Inject
import kotlin.math.abs

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

    companion object {
        private const val DEFAULT_ZOOM = 15.0f

        private const val TAG = "MapFragment"

        private const val LOCATION_UPDATE_INTERVAL = 100L
        private const val LOCATION_UPDATE_DISTANCE = 0.01F

        private const val MAX_LOCATION_UPDATE_AGE = 1000L
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
    var shouldFollowUser = true

    private lateinit var locationProvider: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentMapBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customizeMap()
        setListeners()
        this.setupPlacesListCardView()
        this.locationProvider = LocationServices.getFusedLocationProviderClient(this.requireContext())
        with (this.arguments) {
            if (this == null) return
            val fromPlacesList = this.getBoolean(PlaceDetailsFragment.FROM_PLACE_LIST_BUNDLE_KEY, false)
            this@MapFragment.showPlacesListCardView(fromPlacesList)
        }
        this.startLocationUpdates()
    }

    private fun setListeners() {
        setButtonListeners()
        setViewModelListeners()
//        this.setUserLocationListener()
        setClosePlaceDetailsButtonPopupListener()
        setPlaceDetailsButtonPopupListener()
    }

    private fun setPlaceDetailsButtonPopupListener() {
        binding.placeDescriptionViewBT.setOnClickListener {

            val bundle = bundleOf(
                Pair(PlaceDetailsFragment.PLACE_ID_BUNDLE_KEY, chosenPlaceId),
                Pair(PlaceDetailsFragment.FROM_PLACE_LIST_BUNDLE_KEY, false)
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
                binding.placeDescriptionAddressTV.text = data.textLocation
                if(data.reviewsCount > 0){
                    binding.placeDescriptionRatingTV.text = "Ocena: ${String.format("%.0f",data.positiveReviewsPercent)}% (${data.reviewsCount} ocen)"
                }
                else{
                    binding.placeDescriptionRatingTV.text = "Brak ocen"
                }
                loadImageBasedOnPath(data)
                binding.placeDescriptionCV.visibility = View.VISIBLE
            }

            true
        }
    }

    private fun loadImageBasedOnPath(data : PlaceMarkerData) {
        data.photoPath?.let { photoPath ->
            binding.placeDescriptionImageCV.visibility=View.VISIBLE
            picasso
                .load(photoPath)
                .placeholder(R.drawable.asset_loading)
                .into(binding.placeDescriptionIV)
        } ?: run {
            binding.placeDescriptionImageCV.visibility=View.GONE
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

            // TODO -> jeśli mapa została ręcznie przesunięta, powinna przestać śledzić użytkownika
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
        placesViewModel.observeNonFilteredPlaces(viewLifecycleOwner)

        placesViewModel.placeMarkerFilteredListState.observe(viewLifecycleOwner) { markers ->
            reloadMarkers(markers)
            this.adapter.submitItems(markers)

        }

//        with(placesViewModel.placeMarkerFilteredListState) {
//            if (this.value == null) return
//            for (marker in this.value!!)
//                if (marker.category == PlaceCategory.ROOT_LOCATION) this@MapFragment.zoomToRootLocation(marker)
//        }


//        placesViewModel.placeMarkerListState.observe(viewLifecycleOwner) { resource ->
//            when(resource) {
//                is Resource.Loading -> {
//                    binding.mapPB.visibility = View.VISIBLE
//                }
//                is Resource.Success -> {
//                    binding.mapPB.visibility = View.GONE
//                    reloadMarkers(resource.data!!)
//                    this.adapter.submitItems(resource.data)
//                }
//                is Resource.Error -> {
//                    binding.mapPB.visibility = View.GONE
//                    showErrorMessage()
//                }
//            }
//        }
    }

//    private fun setUserLocationListener() {
//        LocationListenerImpl.userLocation.observe(viewLifecycleOwner) { location ->
////            this.reloadUserMarker(location)
//            this.adapter.submitUserLocation(location)
//        }
//    }

    private fun reloadMarkers(markers: List<PlaceMarkerData>) {
        clearOldMarkers()

        markers.forEach { markerData ->
//            if(PlaceCategory.ROOT_LOCATION == markerData.category) {
//                zoomToRootLocation(markerData)
//            }
            val marker = map.addMarker(
                MarkerOptions()
                    .position(LatLng(markerData.geolocation.latitude, markerData.geolocation.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(markerBitmapGenerator.generatePlaceBitmap(markerData)))
            )
            markerIdsToPlacesData[marker!!] = markerData
        }
    }

    private fun getUserData(): UserData {
        val gson = Gson()
        val fetchedData = Prefs.read().content(
            "userData",
            "{\"id\":\"0\",\"firstName\":\"Imie\",\"lastName\":\"Nazwisko\",\"phoneNumberPrefix\":\"12\",\"phoneNumber\":\"123\",\"description\":\"\",\"avatarUrl\":null}"
        )
        return gson.fromJson(fetchedData, UserData::class.java)
    }

    private fun reloadUserMarker(userLocation: Location) {
        map.clear()

        with(placesViewModel.placeMarkerFilteredListState) {
            if (this.value == null) return
            this@MapFragment.reloadMarkers(this.value!!)
        }

        map.addMarker(
            MarkerOptions()
                .position(LatLng(userLocation.latitude, userLocation.longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmapGenerator.generateUserBitmap(this.getUserData())))
        )

        if (this.shouldFollowUser) this.zoomToLocation(userLocation)
    }

    private fun clearOldMarkers() {
        markerIdsToPlacesData.forEach { it.key.remove() }
        markerIdsToPlacesData.clear()
    }

    private fun showErrorMessage() {
        Toast.makeText(context, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun zoomToLocation(location: Location) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), DEFAULT_ZOOM))
    }

//    private fun zoomToRootLocation(marker : PlaceMarkerData) {
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(marker.geolocation.latitude, marker.geolocation.longitude), DEFAULT_ZOOM))
//    }


    private fun setupPlacesListCardView() {
//        this.setupLocationManager()

        this.binding.arrowBT.setOnClickListener {
            this.showPlacesListCardView(true)
        }
        this.binding.placesListBackButton.setOnClickListener {
            this.showPlacesListCardView(false)
        }

        this.adapter = PlacesAdapter(this.picasso, this)
        with(this.binding.placesListRecyclerView) {
            this.layoutManager = LinearLayoutManager(this@MapFragment.requireContext())
            this.adapter = this@MapFragment.adapter
        }
        this.setupPlacesListSpinner()

//        this.locationListener.userLocation.observe(this.viewLifecycleOwner) {
//            this.adapter.submitUserLocation(it)
//        }

        this.setupPlacesListDistanceCardView()
    }

    private fun setupPlacesListDistanceCardView() {
        this.binding.openDistanceFilterButton.setOnClickListener {
            this.showPlacesListDistanceCardView(true)
        }

        this.binding.distanceCloseCardButton.setOnClickListener {
            val text = this.binding.distanceInput.text?.toString()
            var value = if (text.isNullOrEmpty()) Long.MAX_VALUE / 1000
            else min(abs(text.toLong()), Long.MAX_VALUE / 1000)
            val selectedPosition = this.binding.distanceUnitSpinner.selectedItemPosition
            if (selectedPosition == 1) value *= 1000

            this.showPlacesListDistanceCardView(false)
            this.placesViewModel.setMaxDistance(value, arrayOf(this.getPlacesCategoryFilter()))
//            Toast.makeText(this.requireContext(), "Max distance: $value meters", Toast.LENGTH_LONG).show()
        }
        this.setupPlacesListDistanceUnitSpinner()
    }

    private fun getVisibilityModifier(visible: Boolean): Int {
        return if (visible) View.VISIBLE
        else View.GONE
    }

    private fun showPlacesListCardView(show: Boolean) {
        with (this.binding) {
            this.arrowBT.visibility = this@MapFragment.getVisibilityModifier(!show)
            this.zoomInBT.visibility = this@MapFragment.getVisibilityModifier(!show)
            this.zoomOutBT.visibility = this@MapFragment.getVisibilityModifier(!show)
            this.placesListCardView.visibility = this@MapFragment.getVisibilityModifier(show)
        }
    }

    private fun showPlacesListDistanceCardView(show: Boolean) {
        with (this.binding) {
            this.distanceFilterClosedCv.visibility = this@MapFragment.getVisibilityModifier(!show)
            this.distanceFilterOpenCv.visibility = this@MapFragment.getVisibilityModifier(show)
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

    private fun setupPlacesListDistanceUnitSpinner() {
        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this.requireContext(),
            R.layout.category_spinner_text_view,
            arrayOf("M", "KM")
        )
        adapter.setDropDownViewResource(R.layout.category_spinner_text_view)
        this.binding.distanceUnitSpinner.adapter = adapter
    }

    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this.requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

//    private fun setupLocationManager() {
//        try {
//            if (!this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) return
//            if (!this.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return
//            this.locationManager.requestLocationUpdates(
//                LocationManager.GPS_PROVIDER,
//                LOCATION_UPDATE_INTERVAL,
//                LOCATION_UPDATE_DISTANCE,
//                this.locationListener
//            )
//        } catch (ex: SecurityException) {
//            ex.printStackTrace()
//        }
//    }

    // Must be run on separate thread
//    private fun getLocation() {
//        if (!this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) return
//        if (!this.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return
//        val locationRequest = CurrentLocationRequest.Builder().build()
//        this.locationProvider.getCurrentLocation(locationRequest, null).addOnSuccessListener {
//            this.adapter.submitUserLocation(it)
//        }
//        Thread.sleep(LOCATION_UPDATE_INTERVAL)
//        this.getLocation()
//    }

    private fun startLocationUpdates() {
        if (!this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) return
        if (!this.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return
        val locationRequest = LocationRequest.Builder(LOCATION_UPDATE_INTERVAL)
            .setMaxUpdateAgeMillis(MAX_LOCATION_UPDATE_AGE).build()
        this.locationProvider.requestLocationUpdates(locationRequest, {
            this.adapter.submitUserLocation(it)
            this.placesViewModel.setUserLocation(it, arrayOf(this.getPlacesCategoryFilter()))
            this.reloadUserMarker(it)
        }, Looper.getMainLooper())
    }

    private fun getPlacesCategoryFilter(): PlaceCategory? {
        with(this.binding.placesListCategorySpinner) {
            if (this.selectedItemPosition <= 0) return null
            val translation = this.selectedItem as String
            return PlaceCategory.getCategoryByTranslation(translation)
        }
    }

//    private class LocationListenerImpl(private val context: Context): LocationListener {
//        private val _userLocation: MutableLiveData<Location> = MutableLiveData()
//        val userLocation: LiveData<Location> = this._userLocation
//        override fun onLocationChanged(location: Location) {
//            Toast.makeText(this.context, "new location", Toast.LENGTH_SHORT).show()
//            this._userLocation.postValue(location)
//        }
//    }
}