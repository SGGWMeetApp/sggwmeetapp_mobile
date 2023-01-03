package pl.sggw.sggwmeet.activity.event

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
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.ActivityEventEditBinding
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.request.EventEditRequest
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.Calendar


@AndroidEntryPoint
class EventEditActivity: AppCompatActivity() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : ActivityEventEditBinding
    private lateinit var eventData : EventResponse
    private val timeFormat = SimpleDateFormat("dd.MM.yyyy' 'HH:mm")
    private val gson = Gson()
    private var selectedCalendar=Calendar.getInstance()
    private var selectedLocationID=0

    private val eventViewModel by viewModels<EventViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityEventEditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButtons()
        setAnimations()
        setViewModelListener()

        val retrievedData: String? = intent.getStringExtra("eventData")
        setUpEvent(retrievedData)
    }

    private fun setUpEvent(data:String?){
        if(data.isNullOrBlank()){
            this.finish()
        }
        try {
            eventData=gson.fromJson(data, EventResponse::class.java)
            binding.eventNameTF.setText(eventData.name)
            selectedCalendar.time=eventData.startDate
            binding.eventStartDateTV.setText(timeFormat.format(selectedCalendar.time))
            binding.eventDescriptionTF.setText(eventData.description)
            binding.eventLocationTV.setText(eventData.locationData.name)
            eventViewModel.getAllPlaces()
        }
        catch (e:Exception){
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
            editEvent()
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

        eventViewModel.getAllPlacesState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    selectedLocationID=findLocation(resource.data!!)
                    if(selectedLocationID == -1){
                        this.finish()
                    }
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
        eventViewModel.editEventState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    //resource.data!!
                    Toast.makeText(this, "Zedytowano wydarzenie", Toast.LENGTH_SHORT).show()
                    intent = Intent()
                        .putExtra("newEventData",gson.toJson(resource.data))
                    this.setResult(Activity.RESULT_OK,intent)
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
        }
    }

    private fun showTechnicalErrorMessage() {
        Toast.makeText(this, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    //Zwraca id lokacji na podstawie nazwy, bo backend nie zwraca id lokacji w odpowiedzi
    private fun findLocation(places:List<SimplePlaceResponseData>):Int{
        val searchTarget = eventData.locationData.name
        for (item in places){
            if(item.name == searchTarget){
                return try {
                    item.id.toInt()
                } catch (e:Exception){
                    -1
                }
            }
        }
        return -1
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==101 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedLocationID=data.getIntExtra("returnedLocationID",selectedLocationID)
                binding.eventLocationTV.setText(
                    data.getStringExtra("returnedLocationName")
                )
            }

        }
    }
    private fun editEvent(){
        binding.eventNameTextInputLayout.isErrorEnabled=false
        trimTextInput(binding.eventNameTF)
        trimTextInput(binding.eventDescriptionTF)
        if(!binding.eventNameTF.text.isNullOrBlank()){
            val dateToIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            eventViewModel.editEvent(
                EventEditRequest(
                    binding.eventNameTF.text.toString(),
                    selectedLocationID,
                    binding.eventDescriptionTF.text.toString(),
                    dateToIso.format(selectedCalendar.time)
                ),
                eventData.id
                )
        }
        else{
            binding.eventNameTextInputLayout.error=getString(R.string.registration_required_field)
        }
    }
    private fun trimTextInput(textInput: TextInputEditText) {
        textInput.setText(textInput.text.toString().trim())
    }
}