package pl.sggw.sggwmeet.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import dagger.hilt.android.AndroidEntryPoint
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.ActivityMainBinding


@AndroidEntryPoint
class WelcomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private val topSheetTransition = AutoTransition()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setAnimations()
        setTopSheet()
    }
    private fun navigateToFragment(fragmentId: Int){
        val navController = findNavController(R.id.nav_host_fragment)
        navController.navigateUp()
        navController.navigate(fragmentId)
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
        binding.topSheetLayout.menuLoginBT.setOnClickListener{
            navigateToFragment(R.id.loginFragment)
            topSheetTransition.duration=100
            closeTopSheet()
        }
        binding.topSheetLayout.menuRegBT.setOnClickListener{
            navigateToFragment(R.id.registerFragment)
            topSheetTransition.duration=100
            closeTopSheet()
        }
        binding.topSheetLayout.menuStartBT.setOnClickListener{
            navigateToFragment(R.id.startFragment)
            topSheetTransition.duration=100
            closeTopSheet()
        }
        binding.topSheetLayout.menuEditBT.setOnClickListener{
            navigateToFragment(R.id.userEditFragment)
            topSheetTransition.duration=100
            closeTopSheet()
        }
        binding.topSheetHideHitbox.setOnClickListener{
            topSheetTransition.duration=200
            closeTopSheet()
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
}