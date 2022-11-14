package pl.sggw.sggwmeet.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentStartBinding

class StartFragment: Fragment() {
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentStartBinding.inflate(inflater, container, false)

        this.binding.buttonNext.setOnClickListener {
            Log.i("SGGWMA " + this::class.simpleName, "moving to NextFragment...")
            this.findNavController().navigate(R.id.action_startFragment_to_nextFragment)
        }

        return this.binding.root
    }
}