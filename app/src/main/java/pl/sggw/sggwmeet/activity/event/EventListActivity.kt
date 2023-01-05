package pl.sggw.sggwmeet.activity.event

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.mancj.materialsearchbar.MaterialSearchBar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.ProfileActivity
import pl.sggw.sggwmeet.activity.UserSettingsActivity
import pl.sggw.sggwmeet.activity.group.GroupListActivity
import pl.sggw.sggwmeet.adapter.EventListAdapter
import pl.sggw.sggwmeet.databinding.ActivityEventListBinding
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.util.SearchBarSetupUtil
import pl.sggw.sggwmeet.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class EventListActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private val topSheetTransition = AutoTransition()
    private val timeFormat = SimpleDateFormat("dd.MM.yyyy' 'HH:mm")
    private val timeFormatSimpler = SimpleDateFormat("dd.MM.yyyy")
    @Inject
    lateinit var picasso: Picasso

    private lateinit var binding : ActivityEventListBinding

    private lateinit var adapter: EventListAdapter
    private lateinit var eventList: ArrayList<EventResponse>

    private val eventViewModel by viewModels<EventViewModel>()

    private var locationId = -1
    private var selectedDate=""
    private var selectedCalendar=Calendar.getInstance()

    companion object {
        const val EVENT_EDITED = 104
        const val EVENT_ADDED = 105
        const val LOCATION_SELECTED = 151
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityEventListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setAnimations()
        setViewModelListener()

        setTopSheet()
        setUserData()

        SearchBarSetupUtil.setFontFamily(binding.searchBar,
            ResourcesCompat.getFont(this, R.font.robotoregular))

        setUpSpinner()
        setUpFloatingButton()
        setUpButtons()
        refreshList(binding.spinner.selectedItemPosition)
    }
    private val items = arrayOf<String>("Wszystkie wydarzenia", "Nadchodzące wydarzenia", "Zapisane wydarzenia")
    override fun onItemSelected(parent: AdapterView<*>?,
                                view: View, position: Int,
                                id: Long) {
        refreshList(position)

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    private fun setUpSpinner(){
        val spinner = binding.spinner
        spinner.onItemSelectedListener = this
        val adapter: ArrayAdapter<*> = ArrayAdapter<Any?>(
            this,
            R.layout.spinner_textview,
            items)

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
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

        eventViewModel.getAllEventsState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    eventList= resource.data!!
                    buildRecyclerView()
                    setUpSearch()
                    filter(binding.searchBar.text)
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
        eventViewModel.getUpcomingEventsState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    eventList= resource.data!!
                    buildRecyclerView()
                    setUpSearch()
                    filter(binding.searchBar.text)
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
        eventViewModel.getUserEventsState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    eventList= resource.data!!
                    buildRecyclerView()
                    setUpSearch()
                    filter(binding.searchBar.text)
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

    private fun getAllEvents(){
        eventViewModel.getAllEvents()
    }
    private fun getUpcomingEvents(){
        eventViewModel.getUpcomingEvents()
    }
    private fun getUserEvents(){
        eventViewModel.getUserEvents(Prefs.read().content("userId",0))
    }

    private fun buildRecyclerView(){
        adapter= EventListAdapter(eventList, this)
        val manager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager=manager
        binding.recyclerView.adapter=adapter
    }

    private fun setUpSearch() {
        binding.searchBar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }
        })
        binding.searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                closeKeyboard()
            }

            override fun onButtonClicked(buttonCode: Int) {
            }

        })

    }
    private fun closeKeyboard() {
        val imm: InputMethodManager =
            (getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as InputMethodManager)!!
        imm.hideSoftInputFromWindow(binding.searchBar.getWindowToken(), 0)
    }

    private fun filter(text: String) {
        val filteredlist1 = ArrayList<EventResponse>()
        if(locationId==-1){
            filteredlist1.addAll(eventList)
        }
        else{
            for (item in eventList){
                if (item.locationData.id==locationId){
                    filteredlist1.add(item)
                }
            }
        }

        val filteredlist2 = ArrayList<EventResponse>()
        if(selectedDate==""){
            filteredlist2.addAll(filteredlist1)
        }
        else{
            for (item in filteredlist1){
                if(timeFormatSimpler.format(item.startDate)!!.contains(selectedDate)){
                    filteredlist2.add(item)
                }
            }
        }

        val filteredlist = ArrayList<EventResponse>()
        for (item in filteredlist2) {
            if (item.name.lowercase().contains(text.lowercase())) {
                filteredlist.add(item)
            }
            else if(item.locationData.name.lowercase().contains(text.lowercase())){
                filteredlist.add(item)
            }
            else if("${item.author.firstName} ${item.author.lastName}".lowercase().contains(text.lowercase())){
                filteredlist.add(item)
            }
            else if(!item.description.isNullOrBlank() && item.description!!.lowercase().contains(text.lowercase())){
                filteredlist.add(item)
            }
            else if(timeFormat.format(item.startDate)!!.lowercase().contains(text.lowercase())){
                filteredlist.add(item)
            }

        }
        if (filteredlist.isEmpty()) {
            //
        }
        adapter.filterList(filteredlist)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EVENT_EDITED && resultCode == Activity.RESULT_OK) {
            refreshList(binding.spinner.selectedItemPosition)
        }
        else if (requestCode == EVENT_ADDED && resultCode == Activity.RESULT_OK) {
            refreshList(binding.spinner.selectedItemPosition)
        }
        else if (requestCode == LOCATION_SELECTED && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                locationId=data.getIntExtra("returnedLocationID",-1)
                binding.eventListLocationName.setText(
                    data.getStringExtra("returnedLocationName")
                )
                binding.eventListLocationCancelBT.visibility=View.VISIBLE
                filter(binding.searchBar.text)
            }
        }
    }
    fun refreshList(position: Int){
        when(position){
            0 -> getAllEvents()
            1 -> getUpcomingEvents()
            2 -> getUserEvents()
        }
    }
    fun setUpFloatingButton(){
        binding.floatingBT.setOnClickListener{
            intent=Intent(this,EventCreatePublicActivity::class.java)
            startActivityForResult(intent, EVENT_ADDED)
        }
    }
    private fun animationDimStartNavbar(){
        binding.backgroundDimmer.startAnimation(animationDim)
    }
    private fun animationLitStartNavbar(){
        binding.backgroundDimmer.startAnimation(animationLit)
    }
    private fun setTopSheet(){
        binding.topSheetLayout.searchBar.visibility=View.GONE
        binding.topSheetLayout.searchBarUnderline.visibility=View.GONE
        binding.topSheetLayout.menuEventBT.setOnClickListener {
            topSheetTransition.duration=200
            closeTopSheet()
        }
        binding.navbarActivity.popupButton.setOnClickListener{
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

        binding.topSheetLayout.menuMapBT.setOnClickListener {
            this.finish()
        }

        binding.topSheetLayout.menuGroupsBT.setOnClickListener {
            this.startActivity(Intent(this, GroupListActivity::class.java))
            this.finish()
        }

        binding.topSheetLayout.menuLogoutBT.setOnClickListener {
            this.setResult(Activity.RESULT_OK)
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
        animationLitStartNavbar()
    }
    private fun openTopSheet(){
        TransitionManager.beginDelayedTransition(binding.topSheetBase, topSheetTransition)
        binding.topSheetLayout.hiddenView.visibility=View.VISIBLE
        binding.topSheetHideHitbox.visibility=View.VISIBLE
        animationDimStartNavbar()
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
                .placeholder(R.drawable.asset_loading)
                .into(binding.topSheetLayout.avatarPreviewIV)
        }
        else{
            binding.topSheetLayout.avatarPreviewIV.setImageURI(null)
            binding.topSheetLayout.avatarPreviewIV.setImageResource(R.drawable.avatar_1)
        }
    }
    private fun setUpButtons(){
        binding.eventListLocationSelectBT.setOnClickListener{
            intent=Intent(this,EventLocationListActivity::class.java)
            startActivityForResult(intent, LOCATION_SELECTED)
        }
        binding.eventListLocationCancelBT.setOnClickListener {
            binding.eventListLocationCancelBT.visibility=View.GONE
            binding.eventListLocationName.text=""
            locationId=-1
            filter(binding.searchBar.text)
        }
        binding.eventListDateSelectBT.setOnClickListener{
            selectDate()
        }
        binding.eventListDateCancelBT.setOnClickListener {
            binding.eventListDateCancelBT.visibility=View.GONE
            binding.eventListDateName.text=""
            selectedDate=""
            filter(binding.searchBar.text)
        }
    }
    private fun selectDate(){
        val dateDialog = DatePickerDialog(this, R.style.DatePicker, { _, year, monthOfYear, dayOfMonth ->
            selectedCalendar.set(year,monthOfYear,dayOfMonth)
            selectedDate = timeFormatSimpler.format(selectedCalendar.time)
            binding.eventListDateName.text=selectedDate
            binding.eventListDateCancelBT.visibility=View.VISIBLE
            filter(binding.searchBar.text)
        }, selectedCalendar.get(Calendar.YEAR), selectedCalendar.get(Calendar.MONTH),
            selectedCalendar.get(Calendar.DAY_OF_MONTH))
        dateDialog.show()
    }
}