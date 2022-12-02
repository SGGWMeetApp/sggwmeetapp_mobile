package pl.sggw.sggwmeet.activity

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
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.ActivityCoreBinding
import pl.sggw.sggwmeet.util.SearchBarSetupUtil

@AndroidEntryPoint
class CoreActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCoreBinding
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private val topSheetTransition = AutoTransition()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        SearchBarSetupUtil.setFontFamily(binding.topSheetLayout.searchBar,
            ResourcesCompat.getFont(this, R.font.robotoregular))
        setTopSheet()
        setAnimations()
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
            this.navigateToFragment(R.id.eventsFragment)
            this.topSheetTransition.duration = 100
            this.closeTopSheet()
        }

        binding.topSheetLayout.menuGroupsBT.setOnClickListener {
            this.navigateToFragment(R.id.groupsFragment)
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
}