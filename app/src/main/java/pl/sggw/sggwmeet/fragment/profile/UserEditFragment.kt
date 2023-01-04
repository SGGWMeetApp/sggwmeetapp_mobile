package pl.sggw.sggwmeet.fragment.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentUserEditBinding
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.request.UserEditUserDataRequest
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.UserViewModel

@AndroidEntryPoint
class UserEditFragment : Fragment() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : FragmentUserEditBinding
    private val userViewModel by viewModels<UserViewModel>()
    private var userId : Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.binding = FragmentUserEditBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModelListener()
        setButtonListeners()
        setAnimations()
        setUserData()
    }



    private fun setButtonListeners() {
        binding.userEditButton.setOnClickListener {
            if(verifyFormFields()){
                editUser()
            }
        }
        binding.cancelBT.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun verifyFormFields():Boolean {
        var verified: Boolean = true
        resetErrors()
        trimTextInput(binding.userEditFirstName)
        trimTextInput(binding.userEditLastName)
        trimTextInput(binding.userEditPhonePrefix)
        trimTextInput(binding.userEditPhone)
        trimTextInput(binding.userEditDescription)

        if(binding.userEditPhonePrefix.text!!.length>4){
            binding.userEditPhonePrefixTextInputLayout.error = getString(R.string.prefix_limit)
            verified = false
        }

        if(binding.userEditPhone.text!!.length>15){
            binding.userEditPhoneTextInputLayout.error = getString(R.string.phone_limit)
            verified = false
        }

        val requiredInput = arrayOf(
            binding.userEditFirstName,
            binding.userEditLastName, binding.userEditPhone,
            binding.userEditPhonePrefix
        )
        val requiredTextInputLayout = arrayOf(
            binding.userEditFirstNameTextInputLayout,
            binding.userEditLastNameTextInputLayout, binding.userEditPhoneTextInputLayout,
            binding.userEditPhonePrefixTextInputLayout
        )
//        for (i in 0 until requiredInput.size) {
//            if (requiredInput[i].text!!.isBlank()) {
//                requiredTextInputLayout[i].error = getString(R.string.registration_required_field)
//                verified = false
//            }
//        }
        Log.i("SGGWMA " + this::class.simpleName, "Można edytować: " + verified)
        return verified
    }

    private fun trimTextInput(textInput: TextInputEditText) {
        textInput.setText(textInput.text.toString().trim())
    }

    private fun resetErrors() {
        binding.userEditFirstNameTextInputLayout.isErrorEnabled = false
        binding.userEditLastNameTextInputLayout.isErrorEnabled = false
        binding.userEditPhoneTextInputLayout.isErrorEnabled = false
        binding.userEditDescriptionTextInputLayout.isErrorEnabled = false
        binding.userEditPhonePrefixTextInputLayout.isErrorEnabled = false
    }

    private fun setViewModelListener() {

        userViewModel.userEditState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    findNavController().navigateUp()
                }
                is Resource.Error -> {
                    unlockUI()
                    when(resource.exception) {

                        is TechnicalException -> {
                            showTechnicalErrorMessage()
                        }
                        is ServerException -> {
                            showLoginFailedMessage()
                            handleServerErrorCode(resource.exception.errorCode)
                        }
                        is ClientException -> {
                            showLoginFailedMessage()
                            handleClientErrorCode(resource.exception.errorCode)
                        }
                    }
                }
            }
        }
    }

    private fun handleClientErrorCode(errorCode: ClientErrorCode) {
        //TODO
    }

    private fun handleServerErrorCode(errorCode: String) {
        when(errorCode){
            "500" -> {
                binding.userEditPhoneTextInputLayout.error=getString(R.string.phone_taken)
                binding.userEditPhonePrefixTextInputLayout.error=getString(R.string.phone_taken)
            }
            "400" -> {
                if(!binding.userEditPhone.text.toString().contains("[0-9]".toRegex())){
                    binding.userEditPhoneTextInputLayout.error=getString(R.string.phone_error)
                }
                if(!binding.userEditPhonePrefix.text.toString().contains("[0-9]".toRegex())){
                    binding.userEditPhonePrefixTextInputLayout.error=getString(R.string.phone_error)
                }
            }
        }
    }

    private fun lockUI() {
        binding.loadingPB.visibility = View.VISIBLE
        binding.userEditFirstName.isEnabled = false
        binding.userEditLastName.isEnabled = false
        binding.userEditPhonePrefix.isEnabled = false
        binding.userEditPhone.isEnabled = false
        binding.userEditDescription.isEnabled = false
        binding.userEditButton.isEnabled = false
        binding.userEditFirstName.error = null
        binding.userEditLastName.error = null
        binding.userEditPhonePrefix.error = null
        binding.userEditPhone.error = null
        binding.userEditDescription.error = null
        binding.cancelBT.isEnabled = false
        animationDimStart()
    }

    private fun unlockUI() {
        binding.loadingPB.visibility = View.GONE
        binding.userEditFirstName.isEnabled = true
        binding.userEditLastName.isEnabled = true
        binding.userEditPhonePrefix.isEnabled = true
        binding.userEditPhone.isEnabled = true
        binding.userEditDescription.isEnabled = true
        binding.userEditButton.isEnabled = true
        binding.cancelBT.isEnabled = true
        animationLitStart()
    }


    private fun showLoginFailedMessage() {
        Toast.makeText(context, "Zmiana danych się nie powiodła", Toast.LENGTH_SHORT).show()
    }

    private fun showTechnicalErrorMessage() {
        Toast.makeText(context, getString(R.string.technical_error_message), Toast.LENGTH_SHORT).show()
    }

    private fun setAnimations(){
        animationDim = AnimationUtils.loadAnimation(context,R.anim.background_dim_anim)
        animationDim.fillAfter=true

        animationLit = AnimationUtils.loadAnimation(context,R.anim.background_lit_anim)
        animationLit.fillAfter=true
    }
    private fun animationDimStart(){
        binding.loadingFL.startAnimation(animationDim)
    }
    private fun animationLitStart(){
        binding.loadingFL.startAnimation(animationLit)
    }
    private fun editUser(){
        resetErrors()
        if(userId!=0) {
            userViewModel.userEdit(
                UserEditUserDataRequest(
                    binding.userEditFirstName.text.toString(),
                    binding.userEditLastName.text.toString(),
                    binding.userEditPhonePrefix.text.toString(),
                    binding.userEditPhone.text.toString(),
                    binding.userEditDescription.text.toString()
                ),
                userId
            )
        }
        else {
            showLoginFailedMessage()
        }
    }
    private fun setUserData(){
        val gson = Gson()
        val fetchedData = Prefs.read().content(
            "userData",
            "{\"id\":\"0\",\"firstName\":\"Imie\",\"lastName\":\"Nazwisko\",\"phoneNumberPrefix\":\"12\",\"phoneNumber\":\"123\",\"description\":\"\",\"avatarUrl\":null}"
        )
        val currentUser = gson.fromJson(fetchedData, UserData::class.java)

        binding.userEditFirstName.setText(currentUser.firstName)
        binding.userEditFirstNameTextInputLayout.placeholderText=currentUser.firstName
        binding.userEditLastName.setText(currentUser.lastName)
        binding.userEditLastNameTextInputLayout.placeholderText=currentUser.lastName
        binding.userEditPhonePrefix.setText(currentUser.phoneNumberPrefix)
        binding.userEditPhonePrefixTextInputLayout.placeholderText=currentUser.phoneNumberPrefix
        binding.userEditPhone.setText(currentUser.phoneNumber)
        binding.userEditPhoneTextInputLayout.placeholderText=currentUser.phoneNumber
        binding.userEditDescription.setText(currentUser.description)
        userId=Prefs.read().content("userId",0)
    }
}