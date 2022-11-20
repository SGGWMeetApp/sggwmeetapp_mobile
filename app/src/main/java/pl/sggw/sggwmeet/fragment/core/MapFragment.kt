package pl.sggw.sggwmeet.fragment.core

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentLoginBinding
import pl.sggw.sggwmeet.databinding.FragmentMapBinding
import pl.sggw.sggwmeet.domain.PlaceCategory
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.PlacesViewModel

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {

    companion object {
        private const val DEFAULT_ZOOM = 15.0f
    }

    lateinit var map : GoogleMap
    lateinit var binding : FragmentMapBinding

    private val placesViewModel by viewModels<PlacesViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentMapBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        customizeMap()
        setButtonListeners()
        setViewModelListeners()
    }

    private fun customizeMap() {

        val callback = OnMapReadyCallback { map ->

            this.map = map
            showMapButtons()
            map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
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
                    loadMarkers(resource.data!!)
                }
                is Resource.Error -> {
                    binding.mapPB.visibility = View.GONE
                    showErrorMessage()
                }
            }
        }
    }

    private fun loadMarkers(markers: List<PlaceMarkerData>) {
        markers.forEach { marker ->
            if(PlaceCategory.ROOT_LOCATION == marker.category) {
                zoomToRootLocation(marker)
            }
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(marker.geolocation.latitude, marker.geolocation.longitude))
                    .title(marker.name)
            )
        }
    }

    private fun showErrorMessage() {
        Toast.makeText(context, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun zoomToRootLocation(marker : PlaceMarkerData) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(marker.geolocation.latitude, marker.geolocation.longitude), DEFAULT_ZOOM))
    }
}