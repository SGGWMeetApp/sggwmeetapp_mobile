package pl.sggw.sggwmeet.fragment.core

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.adapter.PlacesAdapter
import pl.sggw.sggwmeet.databinding.FragmentPlacesBinding
import pl.sggw.sggwmeet.domain.Geolocation
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import pl.sggw.sggwmeet.model.repository.PlacesRepository
import pl.sggw.sggwmeet.provider.RootMarkerProvider
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.PlacesViewModel
import javax.inject.Inject

@AndroidEntryPoint
class PlacesFragment : Fragment(R.layout.fragment_places) {

    private val placesViewModel by viewModels<PlacesViewModel>()
    lateinit var binding: FragmentPlacesBinding

    @Inject
    lateinit var lm: LocationManager


    private val lastPlacesData: MutableLiveData<List<PlaceMarkerData>> = MutableLiveData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentPlacesBinding.inflate(inflater, container, false)
        this.binding.placesView.layoutManager = LinearLayoutManager(this.requireContext())
        this.binding.placesView.adapter = PlacesAdapter(listOf(RootMarkerProvider.ROOT_MARKER))
        this.setupListeners()
        this.setupLocationManager()
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.placesViewModel.placeMarkerListState.observe(viewLifecycleOwner) { resource ->
            if (resource is Resource.Success) {
                val items = resource.data!!
                val adapter = PlacesAdapter(items)
//                this.lastPlacesData.postValue(items)
                this.binding.placesView.adapter = adapter
            }
        }
    }

    private fun setupLocationManager() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, LocationListenerImpl)
        } catch (ex: SecurityException) {
            ex.printStackTrace()
        }
    }

    private fun setupListeners() {
        LocationListenerImpl.lastLocation.observe(viewLifecycleOwner) {
            if (this.lastPlacesData.value == null) return@observe
            this.binding.placesView.adapter = PlacesAdapter(this.lastPlacesData.value!!, it)
        }

        this.lastPlacesData.observe(viewLifecycleOwner) {
            if (LocationListenerImpl.lastLocation.value == null) return@observe
            this.binding.placesView.adapter = PlacesAdapter(it, LocationListenerImpl.lastLocation.value!!)
        }

        this.lastPlacesData.postValue(listOf(RootMarkerProvider.ROOT_MARKER))
    }

    private object LocationListenerImpl: LocationListener {
        val lastLocation: MutableLiveData<Geolocation> = MutableLiveData()
        override fun onLocationChanged(location: Location) {
            this.lastLocation.postValue(Geolocation(location.latitude, location.longitude))
        }
    }
}