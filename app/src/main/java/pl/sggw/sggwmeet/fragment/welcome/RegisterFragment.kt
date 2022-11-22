package pl.sggw.sggwmeet.fragment.welcome

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.FragmentRegisterBinding
import pl.sggw.sggwmeet.util.ImageSupportUtil

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var avatarImageUri: Uri? = null
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return this.binding.root
    }

    override fun onViewCreated(view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState != null) {
            avatarImageUri = savedInstanceState.getParcelable("avatarUri")
            if (avatarImageUri != null) {
                binding.regAvatarPreview.setImageURI(avatarImageUri)
                binding.regAvatarPreview.visibility = View.VISIBLE
            }
        }

        textViewOpenLink(view.findViewById<View>(R.id.reg_linkTos) as TextView)
        setFormListeners()
        setButtonListeners()
        setImageAddListeners()
    }

    // Zapisanie odnośnika do wybranego zdjęcia
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelable("avatarUri", avatarImageUri)
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
            verifyFormFields()
        }
    }

    private fun verifyFormFields() {
        var verified: Boolean = true
        resetErrors()
        trimTextInput(binding.regEmail)
        trimTextInput(binding.regFirstName)
        trimTextInput(binding.regLastName)
        trimTextInput(binding.regPhone)
        trimTextInput(binding.regDescription)
        verified = checkPassword()

        val requiredInput = arrayOf(
            binding.regFirstName,
            binding.regLastName, binding.regPhone
        )
        val requiredTextInputLayout = arrayOf(
            binding.regFirstNameTextInputLayout,
            binding.regLastNameTextInputLayout, binding.regPhoneTextInputLayout
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
        binding.regDescriptionTextInputLayout.isErrorEnabled = false
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

    private fun setImageAddListeners() {
        binding.regAvatarAdd.setOnClickListener {
            chooseImage()
        }
        binding.regAvatarCancel.setOnClickListener {
            avatarImageUri = null
            binding.regAvatarPreview.setImageURI(null)
            binding.regAvatarCancel.visibility = View.GONE
            binding.regAvatarPreview.visibility = View.GONE
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
                binding.regAvatarPreview.setImageURI(null)
                //
                binding.regAvatarPreview.setImageURI(avatarImageUri)
                binding.regAvatarCancel.visibility = View.VISIBLE
                binding.regAvatarPreview.visibility = View.VISIBLE
            }
        }
    }

}