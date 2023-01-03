package pl.sggw.sggwmeet.activity

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.ActivityUserShowBinding
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.response.UserEditResponse
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.UserViewModel
import javax.inject.Inject

@AndroidEntryPoint
class UserShowActivity: AppCompatActivity() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : ActivityUserShowBinding
    private val userViewModel by viewModels<UserViewModel>()
    private var userId : Int = 0
    @Inject
    lateinit var picasso: Picasso


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityUserShowBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButtons()
        setAnimations()
        setViewModelListener()

        userId = intent.getIntExtra("userId",0)
        if(userId==0) this.finish()

        userViewModel.getUserData(userId)
    }

    private fun setUpButtons(){
        binding.navbarActivity.closeBT.setOnClickListener {
            this.finish()
        }
        binding.quitBT.setOnClickListener{
            this.finish()
        }
    }

    private fun setAnimations(){
        animationDim = AnimationUtils.loadAnimation(this, R.anim.background_dim_anim)
        animationDim.fillAfter=true

        animationLit = AnimationUtils.loadAnimation(this, R.anim.background_lit_anim)
        animationLit.fillAfter=true
    }

    private fun animationDimStart(){
        binding.loadingFL.startAnimation(animationDim)
    }

    private fun animationLitStart(){
        binding.loadingFL.startAnimation(animationLit)
    }

    private fun lockUI() {
        binding.loadingPB.visibility = View.VISIBLE
        binding.loadingFL.isClickable=true
        animationDimStart()
    }

    private fun unlockUI() {
        binding.loadingPB.visibility = View.GONE
        binding.loadingFL.isClickable=false
        animationLitStart()
    }

    private fun setViewModelListener() {

        userViewModel.getUserDataGetState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    setUpUserData(resource.data!!)
                }
                is Resource.Error -> {
                    unlockUI()
                    this.finish()
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
            "401" -> {

            }
        }
    }

    private fun showTechnicalErrorMessage() {
        Toast.makeText(this, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun setUpUserData(user: UserEditResponse){
        binding.firstNameTV.setText(user.userData.firstName)
        binding.lastNameTV.setText(user.userData.lastName)
        binding.emailTV.setText(user.email)
        binding.phoneTV.setText("(${user.userData.phoneNumberPrefix}) ${user.userData.phoneNumber}")
        if(!user.userData.description.isNullOrBlank()){
            binding.descriptionTV.setText(user.userData.description)
        }
        if(!user.userData.avatarUrl.isNullOrBlank()) {
            picasso
                .load(user.userData.avatarUrl)
                .placeholder(R.drawable.asset_loading)
                .into(binding.avatarPreviewIV)
        }
        else{
            binding.avatarPreviewIV.setImageURI(null)
            binding.avatarPreviewIV.setImageResource(R.drawable.avatar_1)
        }
    }
}