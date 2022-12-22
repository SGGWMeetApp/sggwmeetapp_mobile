package pl.sggw.sggwmeet.fragment.welcome

import android.os.Bundle
import android.util.Patterns
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
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentResetPasswordBinding
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.request.ResetPasswordRequest
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.AuthorizationViewModel

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding: FragmentResetPasswordBinding
    private val authorizationViewModel by viewModels<AuthorizationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()
        setViewModelListener()
        setAnimations()
    }

    private fun setButtonListeners() {
        binding.resetBT.setOnClickListener {
            if(verifyFormFields()){
                resetPassword()
            }
        }

        binding.loginLinkTV.setOnClickListener {
            this.findNavController().navigateUp()
        }
        binding.loginBT.setOnClickListener {
            this.findNavController().navigateUp()
        }
    }

    private fun verifyFormFields() : Boolean {
        var verified: Boolean = true
        resetErrors()
        trimTextInput(binding.emailET)
        if (binding.emailET.text!!.isBlank()) {
            binding.emailTextInputLayout.error = getString(R.string.registration_required_field)
            verified = false
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailET.text.toString()).matches()) {
            binding.emailTextInputLayout.error = getString(R.string.invalid_email)
            verified = false
        }
        return verified
    }

    private fun trimTextInput(textInput: TextInputEditText) {
        textInput.setText(textInput.text.toString().trim())
    }

    private fun lockUI() {
        binding.resetBT.isEnabled=false
        binding.emailET.isEnabled=false
        binding.loadingPB.visibility = View.VISIBLE
        binding.loadingFL.isClickable=true
        animationDimStart()
    }

    private fun unlockUI() {
        binding.resetBT.isEnabled=true
        binding.emailET.isEnabled=true
        binding.loadingPB.visibility = View.GONE
        binding.loadingFL.isClickable=false
        animationLitStart()
    }

    private fun resetErrors() {
        binding.emailTextInputLayout.isErrorEnabled=false
    }


    private fun resetPassword(){
        authorizationViewModel.resetPassword(
            ResetPasswordRequest(binding.emailET.text.toString())
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
                    binding.resetMainLayout.visibility=View.GONE
                    binding.resetSecondLayout.visibility=View.VISIBLE
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
                            //handleClientErrorCode(resource.exception.errorCode)
                        }
                    }
                }
            }
        }
    }
    private fun showLoginFailedMessage() {
        Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
    }

    private fun showTechnicalErrorMessage() {
        Toast.makeText(context, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun handleServerErrorCode(errorCode: String) {
        when(errorCode){
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