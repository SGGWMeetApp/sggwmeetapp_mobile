package pl.sggw.sggwmeet.activity.group

import android.app.Activity
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
import pl.sggw.sggwmeet.activity.event.EventListActivity
import pl.sggw.sggwmeet.adapter.GroupListAdapter
import pl.sggw.sggwmeet.databinding.ActivityGroupListBinding
import pl.sggw.sggwmeet.domain.UserData
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.response.GroupResponse
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.util.SearchBarSetupUtil
import pl.sggw.sggwmeet.viewmodel.GroupViewModel
import javax.inject.Inject

@AndroidEntryPoint
class GroupListActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private val topSheetTransition = AutoTransition()
    @Inject
    lateinit var picasso: Picasso

    private lateinit var binding : ActivityGroupListBinding

    private lateinit var adapter: GroupListAdapter
    private lateinit var groupList: ArrayList<GroupResponse>

    private val groupViewModel by viewModels<GroupViewModel>()

    companion object {
        const val GROUP_EDITED = 201
        const val GROUP_ADDED = 202
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityGroupListBinding.inflate(layoutInflater)
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
        refreshList(binding.spinner.selectedItemPosition)
    }
    private val items = arrayOf<String>("Twoje grupy", "Wszystkie grupy")
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

        groupViewModel.getAllGroupsState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    sortList(resource.data!!.groups)
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
        groupViewModel.getUserGroupsState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    sortList(resource.data!!.groups)
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

    private fun getAllGroups(){
        groupViewModel.getAllGroups()
    }

    private fun getUserGroups(){
        groupViewModel.getUserGroups(
            Prefs.read().content("userId",0)
        )
    }

    private fun buildRecyclerView(){
        adapter= GroupListAdapter(groupList, this)
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
        val filteredlist = ArrayList<GroupResponse>()

        for (item in groupList) {
            if (item.name.lowercase().contains(text.lowercase())) {
                filteredlist.add(item)
            }
            else if("${item.adminData.firstName} ${item.adminData.lastName}".lowercase().contains(text.lowercase())){
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            //
        }
        adapter.filterList(filteredlist)
    }

    private fun sortList(responseList: ArrayList<GroupResponse>){
        groupList = ArrayList<GroupResponse>()
        val bufferList = ArrayList<GroupResponse>()
        for (item in responseList){
            if(item.adminData.isUserAdmin){
                groupList.add(item)
            }
            else{
                bufferList.add(item)
            }
        }
        groupList.addAll(bufferList)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GROUP_ADDED && resultCode == Activity.RESULT_OK) {
            refreshList(binding.spinner.selectedItemPosition)
        }
        else if (requestCode == GROUP_EDITED && resultCode == Activity.RESULT_OK) {
            refreshList(binding.spinner.selectedItemPosition)
        }
    }
    fun refreshList(position: Int){
        when(position){
            0 -> getUserGroups()
            1 -> getAllGroups()
        }
    }
    fun setUpFloatingButton(){
        binding.floatingBT.setOnClickListener{
            intent= Intent(this,GroupCreateActivity::class.java)
            startActivityForResult(intent, GROUP_ADDED)
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
        binding.topSheetLayout.menuGroupsBT.isEnabled=false
        binding.topSheetLayout.searchBarUnderline.visibility=View.GONE
        binding.topSheetLayout.menuGroupsBT.setOnClickListener {
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

        binding.topSheetLayout.menuEventBT.setOnClickListener {
            this.startActivity(Intent(this, EventListActivity::class.java))
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
}