package pl.sggw.sggwmeet.activity.event

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mancj.materialsearchbar.MaterialSearchBar
import dagger.hilt.android.AndroidEntryPoint
import io.easyprefs.Prefs
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.adapter.EventSelectGroupAdapter
import pl.sggw.sggwmeet.databinding.ActivityEventGroupListBinding
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.response.GroupResponse
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.util.SearchBarSetupUtil
import pl.sggw.sggwmeet.viewmodel.GroupViewModel
import kotlin.collections.ArrayList

@AndroidEntryPoint
class EventSelectGroupActivity : AppCompatActivity() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : ActivityEventGroupListBinding

    private lateinit var adapter: EventSelectGroupAdapter
    private var groupList = ArrayList<GroupResponse>()

    private val groupViewModel by viewModels<GroupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityEventGroupListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButton()
        setAnimations()
        setViewModelListener()

        SearchBarSetupUtil.setFontFamily(binding.searchBar,
            ResourcesCompat.getFont(this, R.font.robotoregular))

        getUserGroups()
    }

    private fun setUpButton(){
        binding.navbarActivity.closeBT.setOnClickListener {
            this.finish()
        }
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

        groupViewModel.getUserGroupsState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    filterList(resource.data!!.groups)
                    //resource.data!!.groups.toCollection(groupList)
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
                    this.finish()
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

    private fun getUserGroups(){
        groupViewModel.getUserGroups(Prefs.read().content("userId",0))
    }

    private fun buildRecyclerView(){
        adapter= EventSelectGroupAdapter(groupList,this)
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
        }
        if (filteredlist.isEmpty()) {
            //
        }
        adapter.filterList(filteredlist)
    }
    private fun filterList(list: ArrayList<GroupResponse>){
        for (item in list){
            if (item.adminData.isUserAdmin){
                groupList.add(item)
            }
        }
    }
}