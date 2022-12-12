package pl.sggw.sggwmeet.fragment.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentPlaceDetailsBinding

class PlaceDetailsFragment : Fragment(R.layout.fragment_place_details) {

    companion object {
        const val PLACE_ID_BUNDLE_KEY = "id"
    }

    lateinit var binding : FragmentPlaceDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentPlaceDetailsBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.placeIdTV.text = arguments?.getString(PLACE_ID_BUNDLE_KEY)
    }
}