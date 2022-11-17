package pl.sggw.sggwmeet.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.MainActivity
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentNextBinding


@AndroidEntryPoint
class NextFragment: Fragment() {
    private lateinit var binding: FragmentNextBinding

//    @Inject
//    lateinit var injectedString : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentNextBinding.inflate(inflater, container, false)

        this.binding.buttonBack.setOnClickListener {
            Log.i("SGGWMA " + this::class.simpleName, "moving to StartFragment...")
            this.findNavController().navigate(R.id.action_nextFragment_to_startFragment)
        }
        (activity as AppCompatActivity?)!!.supportActionBar!!.subtitle = "Next"

//        this.binding.labelInjected.text = injectedString

        return this.binding.root
    }
}