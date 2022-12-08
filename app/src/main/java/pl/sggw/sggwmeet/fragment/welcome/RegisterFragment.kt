package pl.sggw.sggwmeet.fragment.welcome

import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.CoreActivity
import pl.sggw.sggwmeet.databinding.FragmentRegisterBinding
import pl.sggw.sggwmeet.domain.UserCredentials
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.AuthorizationViewModel

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val authorizationViewModel by viewModels<AuthorizationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textViewOpenLink(view.findViewById<View>(R.id.reg_linkTos) as TextView)
        setFormListeners()
        setButtonListeners()
        setViewModelListener()
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
        for (i in 0..2) {
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
            UserData(
                firstName = binding.regFirstName.text.toString(),
                lastName = binding.regLastName.text.toString(),
                phoneNumberPrefix = binding.regPhonePrefix.text.toString(),
                phoneNumber = binding.regPhone.text.toString(),
                null,
                null
            )
        )
    }
    private fun setViewModelListener() {

        authorizationViewModel.registerState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    //TODO
                    //lockUI()
                }
                is Resource.Success -> {
                    updateUiWithUser(resource.data!!)
                    startActivity(Intent(context, CoreActivity::class.java))
                    requireActivity().finish()
                    //TODO
                    //unlockUI()

                }
                is Resource.Error -> {
                    //TODO
                    //unlockUI()
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
                            //handleClientErrorCode(resource.exception.errorCode)
                        }
                    }
                }
            }
        }
    }
    private fun updateUiWithUser(userData: UserData) {
        val welcome = "${getString(R.string.welcome)} ${userData.firstName} ${userData.lastName}"
        // TODO : initiate successful logged in experience
        Toast.makeText(context, welcome, Toast.LENGTH_LONG).show()
    }
    private fun showLoginFailedMessage() {
        Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
    }

    private fun showTechnicalErrorMessage() {
        Toast.makeText(context, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun handleServerErrorCode(errorCode: String) {
        //TODO Dodać obsługę jak dowiemy się czy backend zwraca jakieś kody błędu
    }

}