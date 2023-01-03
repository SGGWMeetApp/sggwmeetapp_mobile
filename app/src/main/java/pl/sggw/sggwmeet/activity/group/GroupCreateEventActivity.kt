package pl.sggw.sggwmeet.activity.group

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.event.EventLocationListActivity
import pl.sggw.sggwmeet.databinding.ActivityGroupEventCreateBinding
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.request.EventCreatePublicRequest
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Calendar


@AndroidEntryPoint
class GroupCreateEventActivity: AppCompatActivity() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : ActivityGroupEventCreateBinding
    private val timeFormat = SimpleDateFormat("dd.MM.yyyy' 'HH:mm")
    private val dateToIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    private var selectedCalendar=Calendar.getInstance()
    private var selectedLocationID=0
    private var groupId=-1

    private val eventViewModel by viewModels<EventViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityGroupEventCreateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButtons()
        setAnimations()
        setViewModelListener()
        binding.eventStartDateTV.setText(timeFormat.format(selectedCalendar.time))
        groupId = intent.getIntExtra("groupId",groupId)
        if(groupId==-1){
            this.finish()
        }
    }

    private fun setUpButtons() {
        binding.cancelBT.setOnClickListener {
            this.finish()
        }
        binding.navbarActivity.closeBT.setOnClickListener{
            this.finish()
        }
        binding.selectDateBT.setOnClickListener {
            selectDate()
        }
        binding.selectTimeBT.setOnClickListener {
            selectTime()
        }
        binding.selectLocationBT.setOnClickListener{

            val newActivity = Intent(this, EventLocationListActivity::class.java)
            startActivityForResult(newActivity,101)
        }
        binding.confirmButton.setOnClickListener {
            createEvent()
        }
    }
    private fun selectDate(){
        val dateDialog = DatePickerDialog(this, R.style.DatePicker, { _, year, monthOfYear, dayOfMonth ->
            selectedCalendar.set(year,monthOfYear,dayOfMonth)
            binding.eventStartDateTV.setText(timeFormat.format(selectedCalendar.time))
        }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH))
        dateDialog.show()
    }

    private fun selectTime(){
        val timeDialog = TimePickerDialog(this, R.style.DatePicker, { _, hourOfDay, minute ->
            selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedCalendar.set(Calendar.MINUTE, minute)
            binding.eventStartDateTV.setText(timeFormat.format(selectedCalendar.time))
        }, selectedCalendar.get(Calendar.HOUR_OF_DAY), selectedCalendar.get(Calendar.MINUTE),
            true)
        timeDialog.show()
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
        eventViewModel.createGroupEventState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    Toast.makeText(this, "Utworzono wydarzenie", Toast.LENGTH_SHORT).show()
                    this.setResult(Activity.RESULT_OK)
                    this.finish()
                    unlockUI()
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
            "400" -> {
                binding.eventCreateDateWarning.visibility=View.VISIBLE
            }
            "401" -> {
                Toast.makeText(this, "Nie masz uprawnień", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    private fun showTechnicalErrorMessage() {
        Toast.makeText(this, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==101 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedLocationID=data.getIntExtra("returnedLocationID",selectedLocationID)
                binding.eventLocationTV.setText(
                    data.getStringExtra("returnedLocationName")
                )
                binding.eventCreateLocationWarning.visibility=View.GONE
            }

        }
    }

    private fun checkForm():Boolean{
        var check = true
        binding.eventNameTextInputLayout.isErrorEnabled=false
        binding.eventCreateLocationWarning.visibility=View.GONE
        binding.eventCreateDateWarning.visibility=View.GONE
        trimTextInput(binding.eventNameTF)
        trimTextInput(binding.eventDescriptionTF)
        if(binding.eventNameTF.text.isNullOrBlank()){
            binding.eventNameTextInputLayout.error=getString(R.string.registration_required_field)
            check = false
        }
        if(selectedLocationID==0){
            binding.eventCreateLocationWarning.visibility=View.VISIBLE
            check = false
        }
        return check
    }

    private fun createEvent(){
        if(checkForm()){
            eventViewModel.createGroupEvent(
                EventCreatePublicRequest(
                    binding.eventNameTF.text.toString(),
                    selectedLocationID,
                    binding.eventDescriptionTF.text.toString(),
                    dateToIso.format(selectedCalendar.time)
                ),
                groupId
                )
        }
    }

    private fun trimTextInput(textInput: TextInputEditText) {
        textInput.setText(textInput.text.toString().trim())
    }
}