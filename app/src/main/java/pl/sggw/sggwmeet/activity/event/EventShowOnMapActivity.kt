package pl.sggw.sggwmeet.activity.event


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.ActivityEventShowOnMapBinding
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.fragment.core.placedetails.PlaceDetailsFragment
import pl.sggw.sggwmeet.util.MarkerBitmapGenerator
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.PlacesViewModel
import javax.inject.Inject


@AndroidEntryPoint
class EventShowOnMapActivity: AppCompatActivity() {
    private lateinit var binding: ActivityEventShowOnMapBinding

    companion object {
        private const val DEFAULT_ZOOM = 15.0f
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
        setViewModelListeners()
        customizeMap()
    }

    private fun setClosePlaceDetailsButtonPopupListener() {
        binding.showOnMapCloseBT.setOnClickListener {
            binding.placeDescriptionCV.visibility = View.GONE
        }
    }

    private fun setPlaceDetailsButtonPopupListener() {
        if(disablePicking){
            binding.showOnMapSelectBT.visibility=View.INVISIBLE
            binding.showOnMapSelectBT.isClickable=false
        }
        else{
            binding.showOnMapSelectBT.visibility=View.VISIBLE
            binding.showOnMapSelectBT.isClickable=true
            binding.showOnMapSelectBT.setOnClickListener {

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

            val data = markerIdsToPlacesData[it]!!
            markerClick(data)

            true
        }
    }

    private fun markerClick(data: PlaceMarkerData){
        if(PlaceCategory.ROOT_LOCATION != data.category) {
            chosenPlaceId = data.id
            chosenPlaceName = data.name

            binding.showOnMapName.text = data.name
            binding.showOnMapLocation.text = data.textLocation
            if(data.reviewsCount > 0){
                binding.showOnMapRating.setText(
                    "Ocena: ${String.format("%.0f",data.positiveReviewsPercent)}% (${data.reviewsCount} ocen)"
                )
            }
            else{
                binding.showOnMapRating.setText(
                    "Brak ocen"
                )
            }
            binding.placeDescriptionCV.visibility = View.VISIBLE
        }
    }

    private fun setUpButtons() {
        binding.navbarActivity.closeBT.setOnClickListener {
            this.finish()
        }
    }

    private fun customizeMap() {

        val callback = OnMapReadyCallback { map ->

            this.map = map
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            setOnMarkerClickListener()
            placesViewModel.getPlaceMarkers(null)
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }


    private fun reloadMarkers(markers: List<PlaceMarkerData>) {
        clearOldMarkers()

        markers.forEach { markerData ->
            if(locationId==-2){
                if(PlaceCategory.ROOT_LOCATION == markerData.category) {
                    zoomToRootLocation(markerData)
                }
            }
            else if(!markerData.id.isNullOrBlank()) {
                if (markerData.id.toInt() == locationId) {
                    zoomToRootLocation(markerData)
                    markerClick(markerData)
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
        placesViewModel.placeMarkerListState.observe(this) { resource ->
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
}