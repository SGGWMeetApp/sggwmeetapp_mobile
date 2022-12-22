package pl.sggw.sggwmeet.fragment.welcome

import android.content.Intent
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.CoreActivity
import pl.sggw.sggwmeet.databinding.FragmentLoginBinding
import pl.sggw.sggwmeet.viewmodel.AuthorizationViewModel
import pl.sggw.sggwmeet.domain.UserCredentials
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.util.Resource

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : FragmentLoginBinding
    private val authorizationViewModel by viewModels<AuthorizationViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentLoginBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()
        setEditTextListeners()
        setViewModelListener()
        setAnimations()
        getSavedUserInfo()
    }

    private fun setEditTextListeners() {

        val inputWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                handleLoginButtonLocking()
            }
        }

        binding.emailET.addTextChangedListener(inputWatcher)
        binding.passwordET.addTextChangedListener(inputWatcher)

        binding.passwordET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.loginBT.isEnabled) {
                    loginUser()
                }
            }
            false
        }

    }

    private fun setButtonListeners() {

        binding.loginBT.setOnClickListener {
            loginUser()
        }
        binding.registerLinkTV.setOnClickListener {
            this.findNavController().navigate(R.id.registerFragment)
        }
        binding.resetLinkTV.setOnClickListener {
            this.findNavController().navigate(R.id.resetPasswordFragment)
        }

    }

    private fun setViewModelListener() {

        authorizationViewModel.loginState.observe(viewLifecycleOwner) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    updateUiWithUser(resource.data!!)
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
        when(errorCode) {
            ClientErrorCode.EMAIL_VALIDATION_ERROR -> {
                binding.emailTextInputLayout.error = getString(R.string.invalid_email)
            }
            ClientErrorCode.PASSWORD_VALIDATION_ERROR -> {
                binding.passwordTextInputLayout.error = getString(R.string.password_limit)
            }
            else -> {}
        }
    }

    private fun handleServerErrorCode(errorCode: String) {
        when(errorCode){
            "401" -> {
                binding.emailTextInputLayout.error=getString(R.string.login_wrong)
                binding.passwordTextInputLayout.error=getString(R.string.login_wrong)
            }
        }
    }

    private fun lockUI() {
        binding.loadingPB.visibility = View.VISIBLE
        binding.loginBT.isEnabled = false
        binding.emailET.isEnabled = false
        binding.emailET.error = null
        binding.passwordET.isEnabled = false
        binding.passwordET.error = null
        binding.registerLinkTV.isEnabled = false
        animationDimStart()
    }

    private fun unlockUI() {
        binding.loadingPB.visibility = View.GONE
        binding.loginBT.isEnabled = true
        binding.emailET.isEnabled = true
        binding.passwordET.isEnabled = true
        binding.registerLinkTV.isEnabled = true
        animationLitStart()
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

    fun handleLoginButtonLocking() {
        if(binding.passwordET.text.toString().isBlank() || binding.emailET.text.toString().isBlank()) {
            binding.loginBT.isEnabled = false
            return
        }
        binding.loginBT.isEnabled = true
    }

    private fun resetErrors(){
        binding.emailTextInputLayout.isErrorEnabled = false
        binding.passwordTextInputLayout.isErrorEnabled = false
    }

    private fun loginUser(){
        resetErrors()
        authorizationViewModel.login(
            UserCredentials(
                binding.emailET.text.toString(),
                binding.passwordET.text.toString()
            )
        )
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
    private fun getSavedUserInfo(){
        binding.emailET.setText(Prefs.read().content("email",""))
        binding.passwordET.setText(Prefs.securely().read().content("password",""))

    }
}