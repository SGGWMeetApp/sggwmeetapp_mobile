package pl.sggw.sggwmeet.fragment.profile

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentProfileBinding
import pl.sggw.sggwmeet.domain.UserData
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding : FragmentProfileBinding
    @Inject
    lateinit var picasso: Picasso

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentProfileBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()
        setUserData()
    }

    override fun onResume() {
        super.onResume()
        //setUserData()
    }

    private fun setButtonListeners() {

        binding.editUserBT.setOnClickListener {
            this.findNavController().navigate(R.id.action_profileFragment_to_userEditFragment)
        }
        binding.uploadAvatarBT.setOnClickListener {
            this.findNavController().navigate(R.id.action_profileFragment_to_uploadAvatarFragment)
        }
        binding.changePasswordBT.setOnClickListener {
            this.findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment)
        }
        binding.quitBT.setOnClickListener {
            requireActivity().finish()
        }

    }

    private fun setUserData(){
        val gson = Gson()
        val fetchedData = Prefs.read().content(
            "userData",
            "{\"id\":\"0\",\"firstName\":\"Imie\",\"lastName\":\"Nazwisko\",\"phoneNumberPrefix\":\"12\",\"phoneNumber\":\"123\",\"description\":\"\",\"avatarUrl\":null}"
        )
        val currentUser = gson.fromJson(fetchedData, UserData::class.java)
        binding.emailTV.setText(
            Prefs.read().content("email","")
        )
        binding.firstNameTV.setText(currentUser.firstName)
        binding.lastNameTV.setText(currentUser.lastName)
        binding.phoneTV.setText("(${currentUser.phoneNumberPrefix}) ${currentUser.phoneNumber}")
        binding.descriptionTV.setText(currentUser.description)

        val avatarUrl=Prefs.read().content("avatarUrl","")
        if(avatarUrl!="") {
            picasso
                .load(avatarUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.asset_loading)
                .into(binding.avatarPreviewIV)
        }
        else{
            binding.avatarPreviewIV.setImageURI(null)
            binding.avatarPreviewIV.setImageResource(R.drawable.avatar_1)
        }
    }
}