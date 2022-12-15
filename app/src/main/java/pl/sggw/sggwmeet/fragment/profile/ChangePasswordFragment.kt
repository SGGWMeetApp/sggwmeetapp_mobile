package pl.sggw.sggwmeet.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentChangePasswordBinding
import pl.sggw.sggwmeet.domain.UserChangePasswordData
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.UserViewModel

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : FragmentChangePasswordBinding
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return this.binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()
        setEditTextListeners()
        setViewModelListener()
        setAnimations()
    }
    private fun setEditTextListeners() {

        binding.passwordNewRepeatET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                changePassword()
            }
            false
        }
        binding.cancelBT.setOnClickListener {
            findNavController().navigateUp()
        }

    }

    private fun setButtonListeners() {

        binding.confirmChangePasswordBT.setOnClickListener {
            changePassword()
        }

    }
    private fun setViewModelListener() {

        userViewModel.changePasswordState.observe(viewLifecycleOwner) { resource ->
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
        when(errorCode) {
            ClientErrorCode.PASSWORDS_DO_NOT_MATCH -> {
                binding.passwordNewRepeatTextInputLayout.error = getString(R.string.password_repeat_fail)
            }
            ClientErrorCode.PASSWORD_VALIDATION_ERROR -> {
                binding.passwordNewTextInputLayout.error = getString(R.string.password_limit)
            }
            else -> {}
        }
    }

    private fun handleServerErrorCode(errorCode: String) {
        when(errorCode){
            "409" -> {
                binding.passwordCurrentTextInputLayout.error=getString(R.string.password_wrong)
            }
            "400" -> {
                binding.passwordNewTextInputLayout.error=getString(R.string.password_error_same_password)
            }
        }
    }

    private fun lockUI() {
        binding.loadingPB.visibility = View.VISIBLE
        binding.passwordCurrentET.isEnabled = false
        binding.passwordNewET.isEnabled = false
        binding.passwordNewRepeatET.isEnabled = false
        binding.confirmChangePasswordBT.isEnabled = false
        binding.passwordCurrentET.error = null
        binding.passwordNewET.error = null
        binding.passwordNewRepeatET.error = null
        binding.cancelBT.isEnabled = false
        animationDimStart()
    }

    private fun unlockUI() {
        binding.loadingPB.visibility = View.GONE
        binding.passwordCurrentET.isEnabled = true
        binding.passwordNewET.isEnabled = true
        binding.passwordNewRepeatET.isEnabled = true
        binding.confirmChangePasswordBT.isEnabled = true
        binding.cancelBT.isEnabled = true
        animationLitStart()
    }


    private fun showLoginFailedMessage() {
        Toast.makeText(context, getString(R.string.login_failed), Toast.LENGTH_LONG).show()
    }

    private fun showTechnicalErrorMessage() {
        Toast.makeText(context, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun resetErrors(){
        binding.passwordCurrentTextInputLayout.isErrorEnabled = false
        binding.passwordNewTextInputLayout.isErrorEnabled = false
        binding.passwordNewRepeatTextInputLayout.isErrorEnabled = false
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
    private fun changePassword(){
        resetErrors()
        userViewModel.changePassword(
            UserChangePasswordData(
                binding.passwordCurrentET.text.toString(),
                binding.passwordNewET.text.toString(),
                binding.passwordNewRepeatET.text.toString()
            )
        )
    }
}