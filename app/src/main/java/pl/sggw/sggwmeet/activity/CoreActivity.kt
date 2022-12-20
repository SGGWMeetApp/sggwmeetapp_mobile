package pl.sggw.sggwmeet.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.event.EventListActivity
import pl.sggw.sggwmeet.activity.group.GroupListActivity
import pl.sggw.sggwmeet.databinding.ActivityCoreBinding
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.util.SearchBarSetupUtil
import javax.inject.Inject

@AndroidEntryPoint
class CoreActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCoreBinding
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private val topSheetTransition = AutoTransition()
    @Inject
    lateinit var picasso: Picasso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        SearchBarSetupUtil.setFontFamily(binding.topSheetLayout.searchBar,
            ResourcesCompat.getFont(this, R.font.robotoregular))
        setTopSheet()
        setAnimations()
        setUserData()
    }

    override fun onResume() {
        super.onResume()
        setUserData()
    }

    private fun setAnimations(){
        animationDim = AnimationUtils.loadAnimation(this,R.anim.background_dim_anim)
        animationDim.fillAfter=true

        animationLit = AnimationUtils.loadAnimation(this,R.anim.background_lit_anim)
        animationLit.fillAfter=true
    }
    private fun animationDimStart(){
        binding.backgroundDimmer.startAnimation(animationDim)
    }
    private fun animationLitStart(){
        binding.backgroundDimmer.startAnimation(animationLit)
    }
    private fun setTopSheet(){
        binding.popupButton.setOnClickListener{
            topSheetTransition.duration=200
            if(binding.topSheetLayout.hiddenView.visibility==View.VISIBLE){
                closeTopSheet()
            }
            else{
                openTopSheet()
            }
        }
        binding.topSheetHideHitbox.setOnClickListener{
            topSheetTransition.duration=200
            closeTopSheet()
        }

        binding.topSheetLayout.menuPlacesBT.setOnClickListener {
            this.navigateToFragment(R.id.placesFragment)
            this.topSheetTransition.duration = 100
            this.closeTopSheet()
        }

        binding.topSheetLayout.menuMapBT.setOnClickListener {
            this.navigateToFragment(R.id.mapFragment)
            this.topSheetTransition.duration = 100
            this.closeTopSheet()
        }

        binding.topSheetLayout.menuEventBT.setOnClickListener {
            //this.navigateToFragment(R.id.eventsFragment)
            this.startActivity(Intent(this, EventListActivity::class.java))
            this.topSheetTransition.duration = 100
            this.closeTopSheet()
        }

        binding.topSheetLayout.menuGroupsBT.setOnClickListener {
            this.startActivity(Intent(this, GroupListActivity::class.java))
            this.topSheetTransition.duration = 100
            this.closeTopSheet()
        }

        binding.topSheetLayout.menuLogoutBT.setOnClickListener {
            Prefs.securely().write().content("password","")
                .apply()
            startActivity(Intent(this, WelcomeActivity::class.java))
            this.finish()
        }

        binding.topSheetLayout.menuProfileBT.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            this.topSheetTransition.duration = 100
            this.closeTopSheet()
        }

        binding.topSheetLayout.menuSettingsBT.setOnClickListener {
            startActivity(Intent(this, UserSettingsActivity::class.java))
            this.topSheetTransition.duration = 100
            this.closeTopSheet()
        }

        onBackPressedDispatcher.addCallback(this , object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.topSheetLayout.hiddenView.visibility == View.VISIBLE) {
                    closeTopSheet()
                }
                else{
                    isEnabled=false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }
    private fun closeTopSheet(){
        TransitionManager.beginDelayedTransition(binding.topSheetBase, topSheetTransition)
        binding.topSheetLayout.hiddenView.visibility=View.GONE
        binding.topSheetHideHitbox.visibility=View.GONE
        animationLitStart()
    }
    private fun openTopSheet(){
        TransitionManager.beginDelayedTransition(binding.topSheetBase, topSheetTransition)
        binding.topSheetLayout.hiddenView.visibility=View.VISIBLE
        binding.topSheetHideHitbox.visibility=View.VISIBLE
        animationDimStart()
    }

    private fun navigateToFragment(fragmentId: Int){
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigateUp()
        navController.navigate(fragmentId)
    }

    private fun setUserData(){
        val gson = Gson()
        val fetchedData = Prefs.read().content(
            "userData",
            "{\"id\":\"0\",\"firstName\":\"Imie\",\"lastName\":\"Nazwisko\",\"phoneNumberPrefix\":\"12\",\"phoneNumber\":\"123\",\"description\":\"\",\"avatarUrl\":null}"
        )
        val currentUser = gson.fromJson(fetchedData, UserData::class.java)

        binding.topSheetLayout.displayNameTV.setText(currentUser.firstName+" "+currentUser.lastName)
        binding.topSheetLayout.displayEmailTV.setText(Prefs.read().content("email","email@testowy.pl"))

        val avatarUrl=Prefs.read().content("avatarUrl","")
        if(avatarUrl!="") {
            picasso
                .load(avatarUrl)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .placeholder(R.drawable.asset_loading)
                .into(binding.topSheetLayout.avatarPreviewIV)
        }
        else{
            binding.topSheetLayout.avatarPreviewIV.setImageURI(null)
            binding.topSheetLayout.avatarPreviewIV.setImageResource(R.drawable.avatar_1)
        }
    }
}