package pl.sggw.sggwmeet.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentNextBinding
import javax.inject.Inject

class NextFragment: Fragment() {
    private lateinit var binding: FragmentNextBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentNextBinding.inflate(inflater, container, false)

        this.binding.buttonBack.setOnClickListener {
            Log.i("SGGWMA " + this::class.simpleName, "moving to StartFragment...")
            this.findNavController().navigate(R.id.action_nextFragment_to_startFragment)
        }

        return this.binding.root
    }
}