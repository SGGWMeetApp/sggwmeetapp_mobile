package pl.sggw.sggwmeet.fragment.welcome

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentUserEditBinding
import pl.sggw.sggwmeet.util.ImageSupportUtil

@AndroidEntryPoint
class UserEditFragment : Fragment() {

    private var avatarImageUri: Uri? = null
    private lateinit var binding: FragmentUserEditBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.binding = FragmentUserEditBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (savedInstanceState != null) {
//            avatarImageUri = savedInstanceState.getParcelable("avatarUri")
//            if (avatarImageUri != null) {
//                binding.regAvatarPreview.setImageURI(avatarImageUri)
//                binding.regAvatarPreview.visibility = View.VISIBLE
//            }
//        }

        setButtonListeners()
        setImageAddListeners()
    }

    // Zapisanie odnośnika do wybranego zdjęcia
//    override fun onSaveInstanceState(savedInstanceState: Bundle) {
//        super.onSaveInstanceState(savedInstanceState)
//        savedInstanceState.putParcelable("avatarUri", avatarImageUri)
//    }



    private fun setButtonListeners() {
        binding.userEditButton.setOnClickListener {
            verifyFormFields()
        }
    }

    private fun verifyFormFields() {
        var verified: Boolean = true
        resetErrors()
        trimTextInput(binding.userEditFirstName)
        trimTextInput(binding.userEditLastName)
        trimTextInput(binding.userEditPhone)
        trimTextInput(binding.userEditDescription)

        val requiredInput = arrayOf(
            binding.userEditFirstName,
            binding.userEditLastName, binding.userEditPhone
        )
        val requiredTextInputLayout = arrayOf(
            binding.userEditFirstNameTextInputLayout,
            binding.userEditLastNameTextInputLayout, binding.userEditPhoneTextInputLayout
        )
        for (i in 0..2) {
            if (requiredInput[i].text!!.isBlank()) {
                requiredTextInputLayout[i].error = getString(R.string.registration_required_field)
                verified = false
            }
        }
        Log.i("SGGWMA " + this::class.simpleName, "Można edytować: " + verified)
    }

    private fun trimTextInput(textInput: TextInputEditText) {
        textInput.setText(textInput.text.toString().trim())
    }

    private fun resetErrors() {
        binding.userEditFirstNameTextInputLayout.isErrorEnabled = false
        binding.userEditLastNameTextInputLayout.isErrorEnabled = false
        binding.userEditPhoneTextInputLayout.isErrorEnabled = false
        binding.userEditDescriptionTextInputLayout.isErrorEnabled = false
    }


    private fun setImageAddListeners() {
        binding.userEditAvatarAdd.setOnClickListener {
            chooseImage()
        }
        binding.userEditAvatarCancel.setOnClickListener {
            avatarImageUri = null
            binding.userEditAvatarPreview.setImageURI(null)
            binding.userEditAvatarCancel.visibility = View.GONE
            binding.userEditAvatarPreview.visibility = View.GONE
        }
    }

    // Zwracany kod do onActivityResult
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

                // Hack do odświeżenia ImageView przy zachowaniu tego samego URI
                binding.userEditAvatarPreview.setImageURI(null)
                //
                binding.userEditAvatarPreview.setImageURI(avatarImageUri)
                binding.userEditAvatarCancel.visibility = View.VISIBLE
                binding.userEditAvatarPreview.visibility = View.VISIBLE
            }
        }
    }

}