package pl.sggw.sggwmeet.activity.group

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.adapter.GroupEventListAdapter
import pl.sggw.sggwmeet.adapter.GroupShowUsersAdapter
import pl.sggw.sggwmeet.databinding.ActivityGroupShowBinding
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.model.connector.dto.response.GroupMemberResponse
import pl.sggw.sggwmeet.model.connector.dto.response.GroupResponse
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.GroupViewModel

@AndroidEntryPoint
class GroupShowActivity: AppCompatActivity() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : ActivityGroupShowBinding
    internal lateinit var groupData : GroupResponse
    private lateinit var memberResponse: ArrayList<GroupMemberResponse>
    private lateinit var eventResponse: ArrayList<EventResponse>
    private var areEventsInit = false
    private val gson = Gson()
    private var userIsAdmin=false
    private lateinit var leaveAlertDialog: AlertDialog
    internal lateinit var deleteUserAlertDialog: AlertDialog
    internal lateinit var userHolder: GroupShowUsersAdapter.ViewHolder
    internal var userPosition = 0
    internal var userToDeleteId = 0

    private lateinit var adapter: GroupShowUsersAdapter
    private lateinit var adapterEvent: GroupEventListAdapter
    private val groupViewModel by viewModels<GroupViewModel>()

    companion object {
        const val ADD_USER = 301
        const val ADD_EVENT = 302
        const val EDIT_EVENT = 303
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityGroupShowBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButtons()
        setAnimations()
        setViewModelListener()
        setUpDeleteUserAlertDialog()

        val retrievedData: String? = intent.getStringExtra("groupData")
        setUpGroup(retrievedData)
        getMembers()
    }
    private fun setUpGroup(data:String?){
        if(data.isNullOrBlank()){
            this.finish()
        }
        try {
            groupData=gson.fromJson(data,GroupResponse::class.java)
            binding.groupInfoNameTV.setText(groupData.name)
            binding.groupInfoAdminTV.setText("${groupData.adminData.firstName} ${groupData.adminData.lastName}")
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
        binding.groupAddUserBT.setOnClickListener {
            addUser()
        }
        binding.groupAddEventBT.setOnClickListener {
            addEvent()
        }
        binding.groupUserSectionBT.setOnClickListener{
            membersClick()
        }
        binding.groupEventSectionBT.setOnClickListener {
            eventsClick()
        }
//        binding.editEventBT.setOnClickListener{
//            val newActivity = Intent(this, EventEditActivity::class.java)
//                .putExtra("groupData",gson.toJson(groupData))
//            startActivityForResult(newActivity,103)
//        }
    }
    private fun addUser(){
        val newActivity = Intent(this, GroupAddUserListActivity::class.java)
            .putExtra("groupId",groupData.id)
        startActivityForResult(newActivity, ADD_USER)
    }

    private fun addEvent(){
        val newActivity = Intent(this, GroupCreateEventActivity::class.java)
            .putExtra("groupId",groupData.id)
        startActivityForResult(newActivity, ADD_EVENT)
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

        groupViewModel.getGroupMembersGetState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    if(resource.data!!.isUserAdmin){
                        setAdminPermission()
                    }
                    else{
                        setGroupMemberPermission()
                    }
                    if(areEventsInit){
                        unlockUI()
                    }
                    else {
                        getEvents()
                    }
                    memberResponse=resource.data.users
                    buildRecyclerView()
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

        groupViewModel.getGroupEventsGetState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    areEventsInit=true
                    unlockUI()
                    eventResponse= resource.data!!.events
                    buildEventRecyclerView()
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

        groupViewModel.deleteGroupState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    Toast.makeText(this, "Usunięto grupę", Toast.LENGTH_SHORT).show()
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

        groupViewModel.deleteUserFromGroupState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    adapter.visibilityState[userPosition]=View.GONE
                    val params=userHolder.itemView.layoutParams
                    params.height=0
                    params.width=0
                    userHolder.itemView.layoutParams=params
                    userHolder.itemView.visibility=View.GONE
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

        groupViewModel.leaveGroupNewState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    Toast.makeText(this, "Opuszczono grupę", Toast.LENGTH_SHORT).show()
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

    private fun setAdminPermission(){
        //binding.editGroupBT.visibility=View.VISIBLE
        //binding.groupAddUserBT.visibility=View.VISIBLE
        binding.groupAddEventBT.visibility=View.VISIBLE
        binding.leaveGroupBT.visibility=View.VISIBLE
        setUpDeleteAlertDialog()
        userIsAdmin=true
        setGroupMemberPermission()
    }

    private fun setGroupMemberPermission(){
        binding.groupShowUserView.visibility=View.VISIBLE
        binding.groupShowSelection.visibility=View.VISIBLE
        binding.groupAddUserBT.visibility=View.VISIBLE
        //binding.groupAddEventBT.visibility=View.VISIBLE
        binding.groupShowEventView.visibility=View.GONE
        if(!userIsAdmin){
            binding.leaveGroupBT.setText(R.string.group_leave)
            binding.leaveGroupBT.visibility=View.VISIBLE
            setUpLeaveAlertDialog()
        }
    }

    private fun buildRecyclerView(){
        adapter= GroupShowUsersAdapter(memberResponse, this,userIsAdmin)
        val manager = LinearLayoutManager(this)
        binding.groupShowUserList.layoutManager=manager
        binding.groupShowUserList.adapter=adapter
    }

    private fun buildEventRecyclerView(){
        adapterEvent= GroupEventListAdapter(eventResponse, this)
        val managerEvent = LinearLayoutManager(this)
        binding.groupShowEventList.layoutManager=managerEvent
        binding.groupShowEventList.adapter=adapterEvent
    }

    private fun getMembers(){
        groupViewModel.getGroupMembers(groupData.id)
    }

    private fun getEvents(){
        groupViewModel.getGroupEvents(groupData.id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_USER && resultCode == Activity.RESULT_OK) {
            getMembers()
            this.setResult(Activity.RESULT_OK)
        }
        else if (requestCode == ADD_EVENT && resultCode == Activity.RESULT_OK) {
            getEvents()
            this.setResult(Activity.RESULT_OK)
        }
        else if (requestCode == EDIT_EVENT && resultCode == Activity.RESULT_OK) {
            getEvents()
            this.setResult(Activity.RESULT_OK)
        }
    }

    private fun membersClick(){
        binding.groupUserSectionBAR.visibility=View.VISIBLE
        binding.groupEventSectionBAR.visibility=View.INVISIBLE
        binding.groupShowUserView.visibility=View.VISIBLE
        binding.groupShowEventView.visibility=View.GONE
    }

    private fun eventsClick(){
        binding.groupEventSectionBAR.visibility=View.VISIBLE
        binding.groupUserSectionBAR.visibility=View.INVISIBLE
        binding.groupShowEventView.visibility=View.VISIBLE
        binding.groupShowUserView.visibility=View.GONE
    }

    private fun setUpLeaveAlertDialog(){
        val builder = AlertDialog.Builder(this)
        builder
            .setCancelable(true)
            .setMessage("Czy opuścić grupę?")
            .setNegativeButton("Nie",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                }
            )
            .setPositiveButton("Tak",
                DialogInterface.OnClickListener { dialog, which ->
                    leaveGroup()
                    dialog.dismiss()
                }
            )
        leaveAlertDialog=builder.create()
        setUpLeaveButton()
    }
    private fun setUpDeleteAlertDialog(){
        val builder = AlertDialog.Builder(this)
        builder
            .setCancelable(true)
            .setMessage("Czy usunąć grupę?")
            .setNegativeButton("Nie",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                }
            )
            .setPositiveButton("Tak",
                DialogInterface.OnClickListener { dialog, which ->
                    deleteGroup()
                    dialog.dismiss()
                }
            )
        leaveAlertDialog=builder.create()
        setUpLeaveButton()
    }
    private fun setUpDeleteUserAlertDialog(){
        val builder = AlertDialog.Builder(this)
        builder
            .setCancelable(true)
            .setMessage("Czy usunąć użytkownika z grupy?")
            .setNegativeButton("Nie",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                }
            )
            .setPositiveButton("Tak",
                DialogInterface.OnClickListener { dialog, which ->
                    deleteUser()
                    dialog.dismiss()
                }
            )
        deleteUserAlertDialog=builder.create()
        setUpLeaveButton()
    }
    private fun setUpLeaveButton(){
        binding.leaveGroupBT.setOnClickListener {
            leaveAlertDialog.show()
        }
    }
    private fun leaveGroup(){
        val userId=Prefs.read().content("userId",0)
        groupViewModel.leaveGroupNew(groupData.id,userId)
    }

    private fun deleteGroup(){
        groupViewModel.deleteGroup(groupData.id)
    }

    private fun deleteUser(){
        groupViewModel.deleteUser(groupData.id,userToDeleteId)
    }
}