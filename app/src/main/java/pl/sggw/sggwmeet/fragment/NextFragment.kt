package pl.sggw.sggwmeet.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import pl.sggw.sggwmeet.databinding.FragmentNextBinding


class NextFragment: Fragment() {
    private lateinit var binding: FragmentNextBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentNextBinding.inflate(inflater, container, false)

        this.binding.buttonBack.setOnClickListener {
            this.requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        return this.binding.root
    }
}