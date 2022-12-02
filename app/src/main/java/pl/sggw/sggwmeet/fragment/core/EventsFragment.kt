package pl.sggw.sggwmeet.fragment.core

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentEventsBinding
import pl.sggw.sggwmeet.databinding.FragmentMapBinding

class EventsFragment : Fragment() {

    private lateinit var binding: FragmentEventsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentEventsBinding.inflate(inflater, container, false)
        return this.binding.root
    }
}