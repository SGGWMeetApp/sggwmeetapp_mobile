package pl.sggw.sggwmeet.activity.event


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
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
import pl.sggw.sggwmeet.databinding.ActivityEventShowOnMapBinding
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.util.MarkerBitmapGenerator
import pl.sggw.sggwmeet.viewmodel.PlacesViewModel
import javax.inject.Inject


@AndroidEntryPoint
class EventShowOnMapActivity: AppCompatActivity() {
    private lateinit var binding: ActivityEventShowOnMapBinding

    companion object {
        private const val DEFAULT_ZOOM = 15.0f

        private const val LOCATION_UPDATE_INTERVAL = 100L
        private const val MAX_LOCATION_UPDATE_AGE = 1000L
    }
    private val placesViewModel by viewModels<PlacesViewModel>()

    @Inject
    lateinit var markerBitmapGenerator: MarkerBitmapGenerator
    @Inject
    lateinit var picasso: Picasso
    lateinit var map : GoogleMap
    private val markerIdsToPlacesData : MutableMap<Marker, PlaceMarkerData> = HashMap()
    private var locationId = -1

    lateinit var chosenPlaceId : String
    private var chosenPlaceName = ""
    private var disablePicking = false

    private var shouldFollowUser = false
    private var cameraMovedManually = false

    private lateinit var locationProvider: FusedLocationProviderClient
    private val lastKnownUserLocation = MutableLiveData<Location>()
    private var userMarker: Marker? = null
    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<Array<String>>
    @Inject
    lateinit var locationManager: LocationManager

    private var isInit=false
    private lateinit var userData: UserData



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityEventShowOnMapBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButtons()
        locationId = intent.getIntExtra("locationId",-1)
        if(locationId == -1){
            this.finish()
        }
        disablePicking = intent.getBooleanExtra("disablePicking",false)
        setClosePlaceDetailsButtonPopupListener()
        setPlaceDetailsButtonPopupListener()
        setButtonListeners()
        setViewModelListeners()
        customizeMap()
        this.locationProvider = LocationServices.getFusedLocationProviderClient(this)
        this.startLocationUpdates()
        this.requestLocationPermissionLauncher = this.initializeLocationPermissionLauncher()
        this.requestLocationPermissions()
    }

    private fun startLocationUpdates() {
        if (!this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) return
        if (!this.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return
        val locationRequest = LocationRequest.Builder(LOCATION_UPDATE_INTERVAL)
            .setMaxUpdateAgeMillis(MAX_LOCATION_UPDATE_AGE).build()
        this.locationProvider.requestLocationUpdates(locationRequest, {
            this.lastKnownUserLocation.value = it
        }, Looper.getMainLooper())
        this.setupLocationListener()
    }

    private fun setupLocationListener() {
        userData = getUserData()
        this.lastKnownUserLocation.observe(this) {
            this.reloadUserMarker(it)
        }
    }

    private fun reloadUserMarker(userLocation: Location) {
        map.clear()

        with(placesViewModel.placeMarkerFilteredListState) {
            if (this.value == null) return
            reloadMarkers(this.value!!)
        }

        this.userMarker = map.addMarker(
            MarkerOptions()
                .position(LatLng(userLocation.latitude, userLocation.longitude))
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmapGenerator.generateUserBitmap(userData)))
        )

        if (!isInit) {
            isInit=true
            this.cameraMovedManually = false
            this.zoomToLocation(userLocation)
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

    private fun zoomToLocation(location: Location) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude),
            DEFAULT_ZOOM
        ))
    }

    private fun initializeLocationPermissionLauncher(): ActivityResultLauncher<Array<String>> {
        return this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                && this.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                this.startLocationUpdates()
            }
        }
    }

    private fun requestLocationPermissions() {
        if (this.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            && this.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) return
        this.requestLocationPermissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        ))
    }

    private fun checkPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun setClosePlaceDetailsButtonPopupListener() {
        binding.placeDescriptionCloseBT.setOnClickListener {
            binding.placeDescriptionCV.visibility = View.GONE
        }
    }

    private fun setPlaceDetailsButtonPopupListener() {
        if(disablePicking){
            binding.placeDescriptionSelectBT.visibility=View.INVISIBLE
            binding.placeDescriptionSelectBT.isClickable=false
        }
        else{
            binding.placeDescriptionSelectBT.visibility=View.VISIBLE
            binding.placeDescriptionSelectBT.isClickable=true
            binding.placeDescriptionSelectBT.setOnClickListener {

                val intent = Intent()
                intent.putExtra("returnedLocationID",chosenPlaceId.toInt())
                    .putExtra("returnedLocationName",chosenPlaceName)
                this.setResult(Activity.RESULT_OK,intent)
                this.finish()
            }
        }
    }

    private fun setOnMarkerClickListener() {
        map.setOnMarkerClickListener {
            if (it == this.userMarker) return@setOnMarkerClickListener true

            val data = markerIdsToPlacesData[it]!!
            markerClick(data)

            true
        }
    }

    private fun markerClick(data: PlaceMarkerData){
        if(PlaceCategory.ROOT_LOCATION != data.category) {
            chosenPlaceId = data.id
            chosenPlaceName = data.name

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
    }

    private fun setUpButtons() {
        binding.navbarActivity.closeBT.setOnClickListener {
            this.finish()
        }
    }

    private fun showMapButtons() {
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

    private fun customizeMap() {

        val callback = OnMapReadyCallback { map ->

            this.map = map
            showMapButtons()
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            setOnMarkerClickListener()
            placesViewModel.getPlaceMarkers(arrayOf(null))
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }


    private fun reloadMarkers(markers: List<PlaceMarkerData>) {
        clearOldMarkers()

        markers.forEach { markerData ->
            if(!isInit) {
                if (locationId == -2) {
                    if (PlaceCategory.ROOT_LOCATION == markerData.category) {
                        zoomToRootLocation(markerData)
                    }
                } else if (!markerData.id.isNullOrBlank()) {
                    if (markerData.id.toInt() == locationId) {
                        zoomToRootLocation(markerData)
                        markerClick(markerData)
                        isInit=true
                    }
                }
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
        Toast.makeText(this, getString(R.string.technical_error_message), Toast.LENGTH_SHORT).show()
    }

    private fun zoomToRootLocation(marker : PlaceMarkerData) {
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(marker.geolocation.latitude, marker.geolocation.longitude),
                DEFAULT_ZOOM
            ))
    }

    private fun setViewModelListeners() {
        placesViewModel.observeNonFilteredPlaces(this)

        placesViewModel.placeMarkerFilteredListState.observe(this) { markers ->
            this.reloadMarkers(markers)
        }

//        placesViewModel.placeMarkerListState.observe(this) { resource ->
//            when(resource) {
//                is Resource.Loading -> {
//                    binding.mapPB.visibility = View.VISIBLE
//                }
//                is Resource.Success -> {
//                    binding.mapPB.visibility = View.GONE
//                    reloadMarkers(resource.data!!)
//                }
//                is Resource.Error -> {
//                    binding.mapPB.visibility = View.GONE
//                    showErrorMessage()
//                }
//            }
//        }
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
}