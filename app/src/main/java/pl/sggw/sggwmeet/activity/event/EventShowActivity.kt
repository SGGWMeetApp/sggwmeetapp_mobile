package pl.sggw.sggwmeet.activity.event

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.databinding.ActivityEventShowBinding
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.request.GroupEventNotificationRequest
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.EventViewModel
import pl.sggw.sggwmeet.viewmodel.GroupViewModel
import java.text.SimpleDateFormat


@AndroidEntryPoint
class EventShowActivity: AppCompatActivity() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : ActivityEventShowBinding
    private lateinit var eventData : EventResponse
    private val timeFormat = SimpleDateFormat("dd.MM.yyyy' 'HH:mm")
    private val gson = Gson()
    private var groupId = -1
    private var groupName: String? = null
    private val groupViewModel by viewModels<GroupViewModel>()
    private val eventViewModel by viewModels<EventViewModel>()
    private lateinit var deleteAlertDialog: AlertDialog
    private var canEdit = false
    private var wasEdited = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityEventShowBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButtons()

        setAnimations()
        setViewModelListener()

        val retrievedData: String? = intent.getStringExtra("eventData")
        groupId = intent.getIntExtra("groupId",-1)
        groupName = intent.getStringExtra("groupName")
        setUpEvent(retrievedData)
    }
    private fun setUpEvent(data:String?){
        if(data.isNullOrBlank()){
            this.finish()
        }
        try {
            eventData=gson.fromJson(data,EventResponse::class.java)
            binding.eventNameTV.setText(eventData.name)
            binding.eventStartDateTV.setText(timeFormat.format(eventData.startDate))
            binding.eventLocationTV.setText(eventData.locationData.name)
            binding.eventDescriptionTV.setText(eventData.description)
            binding.eventAuthorTV.setText("${eventData.author.firstName} ${eventData.author.lastName}")
            binding.eventEmailTV.setText(eventData.author.email)
            canEdit= Prefs.read().content("email","")==eventData.author.email && eventData.canEdit
            binding.editEventBT.isEnabled=canEdit
            binding.editEventBT.isGone=canEdit.not()
            if(groupId!=-1){
                binding.eventGroupNameTV.setText(groupName)

                binding.eventGroupLayout.visibility= View.VISIBLE
                binding.eventGroupNotificationSwitchTV.isClickable=canEdit
                if(canEdit){
                    setUpSwitch()
                    setUpDeleteAlertDialog()
                }
            }
            else{
                if(wasEdited){
                    wasEdited=false
                }
                else showAttendersDetails()
            }
        }
        catch (e:Exception){
            this.finish()
        }
    }
    private fun setUpButtons(){
        binding.navbarActivity.closeBT.setOnClickListener {
            this.finish()
        }
        binding.exitBT.setOnClickListener{
            this.finish()
        }
        binding.editEventBT.setOnClickListener{
            val newActivity = Intent(this, EventEditActivity::class.java)
                .putExtra("eventData",gson.toJson(eventData))
            startActivityForResult(newActivity,103)
        }
    }
    private fun setUpSwitch(){
        binding.eventGroupNotificationSwitchTV.setOnClickListener {
            groupViewModel.switchGroupNotification(
                GroupEventNotificationRequest(
                    binding.eventGroupNotificationSwitchTV.isChecked
                ),
                groupId,
                eventData.id
            )
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

        groupViewModel.switchGroupNotificationGetState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    this.setResult(Activity.RESULT_OK)
                }
                is Resource.Error -> {
                    unlockUI()
                    binding.eventGroupNotificationSwitchTV.isChecked=binding.eventGroupNotificationSwitchTV.isChecked.not()
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
        groupViewModel.deleteGroupEventGetState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    this.setResult(Activity.RESULT_OK)
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
        eventViewModel.addUserToEventState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    this.setResult(Activity.RESULT_OK)
                    eventData.userAttends=true
                    eventData.attendersCount++
                    binding.eventAttendersTV.setText(eventData.attendersCount.toString())
                    binding.eventAttenderJoined.visibility=View.VISIBLE
                    binding.eventAttenderJoinedNot.visibility=View.GONE
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
        eventViewModel.deleteUserFromEventState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    this.setResult(Activity.RESULT_OK)
                    eventData.userAttends=false
                    eventData.attendersCount--
                    binding.eventAttendersTV.setText(eventData.attendersCount.toString())
                    binding.eventAttenderJoinedNot.visibility=View.VISIBLE
                    binding.eventAttenderJoined.visibility=View.GONE
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
            "401" -> {

            }
        }
    }

    private fun showTechnicalErrorMessage() {
        Toast.makeText(this, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun setUpDeleteAlertDialog(){
        binding.deleteBT.visibility=View.VISIBLE
        val builder = AlertDialog.Builder(this)
        builder
            .setCancelable(true)
            .setMessage("Czy usunąć wydarzenie?")
            .setNegativeButton("Nie",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                }
            )
            .setPositiveButton("Tak",
                DialogInterface.OnClickListener { dialog, which ->
                    deleteEvent()
                    dialog.dismiss()
                }
            )
        deleteAlertDialog=builder.create()
        binding.deleteBT.setOnClickListener {
            deleteAlertDialog.show()
        }
    }

    private fun deleteEvent(){
        groupViewModel.deleteGroupEvent(
            groupId,
            eventData.id
        )
    }

    private fun showAttendersDetails(){
        binding.eventAttendersTV.setText(eventData.attendersCount.toString())
        if(eventData.userAttends){
            binding.eventAttenderJoined.visibility=View.VISIBLE
            binding.eventAttenderJoinedNot.visibility=View.GONE
        }
        else{
            binding.eventAttenderJoinedNot.visibility=View.VISIBLE
            binding.eventAttenderJoined.visibility=View.GONE
        }
        binding.eventAttendersLayout.visibility=View.VISIBLE
        setUpAttendersButtons()
    }

    private fun setUpAttendersButtons(){
        binding.eventQuitBT.setOnClickListener {
            eventQuit()
        }
        binding.eventJoinBT.setOnClickListener {
            eventJoin()
        }
    }

    private fun eventQuit(){
        eventViewModel.deleteUserFromEvent(eventData.id,Prefs.read().content("userId",0))
    }

    private fun eventJoin(){
        eventViewModel.addUserToEvent(eventData.id,Prefs.read().content("userId",0))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==103 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                wasEdited=true
                setUpEvent(data.getStringExtra("newEventData"))
                this.setResult(Activity.RESULT_OK)
            }

        }
    }
}