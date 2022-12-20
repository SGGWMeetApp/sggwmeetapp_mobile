package pl.sggw.sggwmeet.activity.group

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.ActivityGroupCreateBinding
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.request.GroupCreateRequest
import pl.sggw.sggwmeet.model.connector.dto.response.AdminDataResponse
import pl.sggw.sggwmeet.model.connector.dto.response.GroupResponse
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.GroupViewModel



@AndroidEntryPoint
class GroupCreateActivity: AppCompatActivity() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : ActivityGroupCreateBinding
    private val gson = Gson()

    private val groupViewModel by viewModels<GroupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityGroupCreateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButtons()
        setAnimations()
        setViewModelListener()
    }

    private fun setUpButtons() {
        binding.cancelBT.setOnClickListener {
            this.finish()
        }
        binding.navbarActivity.closeBT.setOnClickListener{
            this.finish()
        }
        binding.confirmButton.setOnClickListener {
            createGroup()
        }
    }

    private fun setAnimations(){
        animationDim = AnimationUtils.loadAnimation(this,R.anim.background_dim_anim)
        animationDim.fillAfter=true

        animationLit = AnimationUtils.loadAnimation(this,R.anim.background_lit_anim)
        animationLit.fillAfter=true
    }
    private fun animationDimStart(){
        binding.loadingFL.startAnimation(animationDim)
    }
    private fun animationLitStart(){
        binding.loadingFL.startAnimation(animationLit)
    }
    private fun lockUI() {
        binding.confirmButton.isEnabled=false
        binding.loadingPB.visibility = View.VISIBLE
        binding.loadingFL.isClickable=true
        animationDimStart()
    }

    private fun unlockUI() {
        binding.confirmButton.isEnabled=true
        binding.loadingPB.visibility = View.GONE
        binding.loadingFL.isClickable=false
        animationLitStart()
    }

    private fun setViewModelListener() {
        groupViewModel.createNewGroupGetState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    Toast.makeText(this, "Utworzono grupę", Toast.LENGTH_SHORT).show()
                    unlockUI()
                    val fetchedData = Prefs.read().content(
                        "userData",
                        "{\"id\":\"0\",\"firstName\":\"Imie\",\"lastName\":\"Nazwisko\",\"phoneNumberPrefix\":\"12\",\"phoneNumber\":\"123\",\"description\":\"\",\"avatarUrl\":null}"
                    )
                    val currentUser = gson.fromJson(fetchedData, UserData::class.java)
                    val newActivity = Intent(this, GroupShowActivity::class.java)
                        .putExtra("groupData",gson.toJson(
                            GroupResponse(
                                resource.data!!.id,
                                resource.data.name,
                                0,
                                AdminDataResponse(currentUser.firstName,currentUser.lastName,true),
                                0
                            )
                        ))
                    startActivity(newActivity)
                    this.finish()
                }
                is Resource.Error -> {
                    unlockUI()
                    when(resource.exception) {

                        is TechnicalException -> {
                            showTechnicalErrorMessage()
                        }
                        is ServerException -> {
                            handleServerErrorCode(resource.exception.errorCode)
                        }
                        is ClientException -> {
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

    private fun showTechnicalErrorMessage() {
        Toast.makeText(this, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun checkForm():Boolean{
        binding.groupNameTextInputLayout.isErrorEnabled=false
        trimTextInput(binding.groupNameTF)
        var check = binding.groupNameTF.text.isNullOrBlank().not()
        if(!check){
            binding.groupNameTextInputLayout.error=getString(R.string.registration_required_field)
        }
        return check
    }

    private fun createGroup(){
        if(checkForm()){
            groupViewModel.createNewGroup(
                GroupCreateRequest(
                    binding.groupNameTF.text.toString()
                )
            )
        }
    }

    private fun trimTextInput(textInput: TextInputEditText) {
        textInput.setText(textInput.text.toString().trim())
    }
}