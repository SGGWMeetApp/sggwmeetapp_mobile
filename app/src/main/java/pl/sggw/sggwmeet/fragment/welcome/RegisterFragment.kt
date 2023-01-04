package pl.sggw.sggwmeet.fragment.welcome

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.CoreActivity
import pl.sggw.sggwmeet.databinding.FragmentRegisterBinding
import pl.sggw.sggwmeet.domain.UserCredentials
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.request.UserRegisterRequestData
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.AuthorizationViewModel

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding: FragmentRegisterBinding
    private val authorizationViewModel by viewModels<AuthorizationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //textViewOpenLink(view.findViewById<View>(R.id.reg_linkTos) as TextView)
        //setFormListeners()
        setButtonListeners()
        setViewModelListener()
        setAnimations()
    }

    /**
     * Pozwala otworzyć linki z TextView w przeglądarce.
     */
    private fun textViewOpenLink(textView: TextView) {
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setFormListeners() {
        binding.regCheckboxTos.setOnCheckedChangeListener { _, state ->
            binding.registerButton.isEnabled = state
        }
    }

    private fun setButtonListeners() {
        binding.registerButton.setOnClickListener {
            if(verifyFormFields()){
                registerUser()
            }
        }

        binding.loginLinkTV.setOnClickListener {
            this.findNavController().navigateUp()
            this.findNavController().navigateUp()
            this.findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun verifyFormFields() : Boolean {
        var verified: Boolean = true
        resetErrors()
        trimTextInput(binding.regEmail)
        trimTextInput(binding.regFirstName)
        trimTextInput(binding.regLastName)
        trimTextInput(binding.regPhone)
        trimTextInput(binding.regPhonePrefix)
        verified = checkPassword()

        if(binding.regPhonePrefix.text!!.length>4){
            binding.regPhonePrefixTextInputLayout.error = getString(R.string.prefix_limit)
            verified = false
        }

        if(binding.regPhone.text!!.length>15){
            binding.regPhoneTextInputLayout.error = getString(R.string.phone_limit)
            verified = false
        }

        val requiredInput = arrayOf(
            binding.regFirstName,
            binding.regLastName, binding.regPhone,
            binding.regPhonePrefix
        )
        val requiredTextInputLayout = arrayOf(
            binding.regFirstNameTextInputLayout,
            binding.regLastNameTextInputLayout, binding.regPhoneTextInputLayout,
            binding.regPhonePrefixTextInputLayout
        )
        for (i in 0 until requiredInput.size) {
            if (requiredInput[i].text!!.isBlank()) {
                requiredTextInputLayout[i].error = getString(R.string.registration_required_field)
                verified = false
            }
        }
        if (binding.regEmail.text!!.isBlank()) {
            binding.regEmailTextInputLayout.error = getString(R.string.registration_required_field)
            verified = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.regEmail.text.toString()).matches()) {
            binding.regEmailTextInputLayout.error = getString(R.string.invalid_email)
            verified = false
        }
        Log.i("SGGWMA " + this::class.simpleName, "Można rejestrować: " + verified)
        return verified
    }

    private fun trimTextInput(textInput: TextInputEditText) {
        textInput.setText(textInput.text.toString().trim())
    }

    private fun lockUI() {
        binding.loadingPB.visibility = View.VISIBLE
        binding.registerButton.isEnabled = false
        binding.loginLinkTV.isEnabled = false
        binding.regEmail.isEnabled = false
        binding.regPassword1.isEnabled = false
        binding.regPassword2.isEnabled = false
        binding.regFirstName.isEnabled = false
        binding.regLastName.isEnabled = false
        binding.regPhonePrefix.isEnabled = false
        binding.regPhone.isEnabled = false
        binding.regEmail.error = null
        binding.regPassword1.error = null
        binding.regPassword2.error = null
        binding.regFirstName.error = null
        binding.regLastName.error = null
        binding.regPhonePrefix.error = null
        binding.regPhone.error = null
        animationDimStart()
    }

    private fun unlockUI() {
        binding.loadingPB.visibility = View.GONE
        binding.registerButton.isEnabled = true
        binding.loginLinkTV.isEnabled = true
        binding.regEmail.isEnabled = true
        binding.regPassword1.isEnabled = true
        binding.regPassword2.isEnabled = true
        binding.regFirstName.isEnabled = true
        binding.regLastName.isEnabled = true
        binding.regPhonePrefix.isEnabled = true
        binding.regPhone.isEnabled = true
        animationLitStart()
    }

    private fun resetErrors() {
        binding.regEmailTextInputLayout.isErrorEnabled = false
        binding.regPassword1TextInputLayout.isErrorEnabled = false
        binding.regPassword2TextInputLayout.isErrorEnabled = false
        binding.regFirstNameTextInputLayout.isErrorEnabled = false
        binding.regLastNameTextInputLayout.isErrorEnabled = false
        binding.regPhoneTextInputLayout.isErrorEnabled = false
        binding.regPhonePrefixTextInputLayout.isErrorEnabled = false
    }

    private fun checkPassword(): Boolean {
        var check: Boolean = true
        if (binding.regPassword1.text.toString().length < 8) {
            binding.regPassword1TextInputLayout.error = getString(R.string.password_limit)
            check = false
        }
        if (binding.regPassword1.text.toString() != binding.regPassword2.text.toString()) {
            binding.regPassword2TextInputLayout.error = getString(R.string.password_repeat_fail)
            check = false
        }
        return check
    }
    private fun registerUser(){
        authorizationViewModel.register(
            UserCredentials(
                binding.regEmail.text.toString(),
                binding.regPassword1.text.toString()
            ),
            UserRegisterRequestData(
                firstName = binding.regFirstName.text.toString(),
                lastName = binding.regLastName.text.toString(),
                phoneNumberPrefix = binding.regPhonePrefix.text.toString(),
                phoneNumber = binding.regPhone.text.toString(),
                null
            )
        )
    }
    private fun setViewModelListener() {
        authorizationViewModel.registerState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    startActivity(Intent(context, CoreActivity::class.java))
                    requireActivity().finish()
                }
                is Resource.Error -> {
                    unlockUI()
                    when(resource.exception) {

                        is TechnicalException -> {
                            showTechnicalErrorMessage()
                        }
                        is ServerException -> {
                            showRegisterFailedMessage()
                            handleServerErrorCode(resource.exception.errorCode)
                        }
                        is ClientException -> {
                            showRegisterFailedMessage()
                            //handleClientErrorCode(resource.exception.errorCode)
                        }
                    }
                }
            }
        }
    }

    private fun showRegisterFailedMessage() {
        Toast.makeText(context, "Rejestracja nie powiodła się", Toast.LENGTH_SHORT).show()
    }

    private fun showLoginFailedMessage() {
        Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
    }

    private fun showTechnicalErrorMessage() {
        Toast.makeText(context, getString(R.string.technical_error_message), Toast.LENGTH_SHORT).show()
    }

    private fun handleServerErrorCode(errorCode: String) {
        when(errorCode){
            "409" -> {
                        binding.regEmailTextInputLayout.error=getString(R.string.user_exists)
                        binding.regPhoneTextInputLayout.error=getString(R.string.user_exists)
                        binding.regPhonePrefixTextInputLayout.error=getString(R.string.user_exists)
                }
            "400" -> {
                if(!binding.regPhone.text.toString().contains("[0-9]".toRegex())){
                    binding.regPhoneTextInputLayout.error=getString(R.string.phone_error)
                }
                if(!binding.regPhonePrefix.text.toString().contains("[0-9]".toRegex())){
                    binding.regPhonePrefixTextInputLayout.error=getString(R.string.phone_error)
                }
                }
            }
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

}