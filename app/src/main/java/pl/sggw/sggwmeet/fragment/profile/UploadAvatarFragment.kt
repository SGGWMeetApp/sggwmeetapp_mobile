package pl.sggw.sggwmeet.fragment.profile

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentUploadAvatarBinding
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.util.ImageSupportUtil
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.UserViewModel

@AndroidEntryPoint
class UploadAvatarFragment : Fragment() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : FragmentUploadAvatarBinding
    private val userViewModel by viewModels<UserViewModel>()
    private var avatarImageUri: Uri? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        this.binding = FragmentUploadAvatarBinding.inflate(inflater, container, false)
        return this.binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtonListeners()
        setViewModelListener()
        setAnimations()
    }

    private fun setButtonListeners() {

        binding.uploadAvatarConfirmBT.setOnClickListener {
            if(avatarImageUri!=null) {
                uploadAvatar()
            }
        }
        binding.cancelBT.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.uploadAvatarSelectAvatarBT.setOnClickListener {
            chooseImage()
        }

    }
    private fun setViewModelListener() {

        userViewModel.uploadAvatarState.observe(viewLifecycleOwner) { resource ->
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
            else -> {}
        }
    }

    private fun handleServerErrorCode(errorCode: String) {
        when(errorCode){

        }
    }

    private fun lockUI() {
        binding.loadingPB.visibility = View.VISIBLE
        binding.cancelBT.isEnabled = false
        binding.uploadAvatarSelectAvatarBT.isEnabled = false
        binding.uploadAvatarConfirmBT.isEnabled = false
        animationDimStart()
    }

    private fun unlockUI() {
        binding.loadingPB.visibility = View.GONE
        binding.cancelBT.isEnabled = true
        binding.uploadAvatarSelectAvatarBT.isEnabled = true
        binding.uploadAvatarConfirmBT.isEnabled = true
        animationLitStart()
    }


    private fun showLoginFailedMessage() {
        Toast.makeText(context, "Wysyłanie nie powiodło się", Toast.LENGTH_SHORT).show()
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
    private fun uploadAvatar(){
        val inputStream = requireActivity().contentResolver.openInputStream(avatarImageUri!!)
        if (inputStream != null) {
            userViewModel.uploadAvatar(
                inputStream, Prefs.read().content("userId",0)
            )
        }
    }

    private val imagePickedCode = 1001

    private fun chooseImage() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(
            galleryIntent,
            imagePickedCode
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == imagePickedCode) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                val source =
                    ImageDecoder.createSource(requireActivity().contentResolver, selectedImageUri!!)
                val avatarImageBitmap =
                    ImageSupportUtil.resizeBitmap(ImageDecoder.decodeBitmap(source), 512)
                avatarImageUri =
                    ImageSupportUtil.bitmapToJpeg(avatarImageBitmap, "avatarTemp", requireContext())
                        ?.toUri()

                binding.uploadAvatarAvatarPreviewIV.setImageURI(null)
                binding.uploadAvatarAvatarPreviewIV.setImageURI(avatarImageUri)
                binding.uploadAvatarConfirmBT.isEnabled = true
            }
        }
    }
}