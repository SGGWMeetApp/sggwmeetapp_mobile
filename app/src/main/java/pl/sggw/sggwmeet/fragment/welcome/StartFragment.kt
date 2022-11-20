package pl.sggw.sggwmeet.fragment.welcome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.CoreActivity
import pl.sggw.sggwmeet.databinding.FragmentStartBinding

@AndroidEntryPoint
class StartFragment: Fragment(R.layout.fragment_start) {

    private lateinit var binding : FragmentStartBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentStartBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setButtonListeners()
    }

    private fun setButtonListeners() {

        binding.loginBT.setOnClickListener {
            this.findNavController().navigate(R.id.action_startFragment_to_loginFragment)
        }

        binding.skipLoginBT.setOnClickListener {
            val newActivity = Intent(context, CoreActivity::class.java)
            startActivity(newActivity)
        }
    }
}