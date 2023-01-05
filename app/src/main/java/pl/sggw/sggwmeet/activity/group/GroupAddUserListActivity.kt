package pl.sggw.sggwmeet.activity.group

import android.app.Activity
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
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.adapter.GroupUserAddAdapter
import pl.sggw.sggwmeet.databinding.ActivityGroupAddUserListBinding
import pl.sggw.sggwmeet.exception.ClientErrorCode
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.request.GroupAddUserRequest
import pl.sggw.sggwmeet.model.connector.dto.response.UserToGroupResponse
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.util.SearchBarSetupUtil
import pl.sggw.sggwmeet.viewmodel.GroupViewModel
import pl.sggw.sggwmeet.viewmodel.UserViewModel
import kotlin.collections.ArrayList

@AndroidEntryPoint
class GroupAddUserListActivity : AppCompatActivity() {
    private lateinit var animationDim : Animation
    private lateinit var animationLit : Animation
    private lateinit var binding : ActivityGroupAddUserListBinding
    internal var groupId = -1
    internal var userToAddId = 0
    internal lateinit var holder :GroupUserAddAdapter.ViewHolder

    private lateinit var adapter: GroupUserAddAdapter
    private var userList = ArrayList<UserToGroupResponse>()

    private val userViewModel by viewModels<UserViewModel>()
    internal val groupViewModel by viewModels<GroupViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.binding = ActivityGroupAddUserListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpButton()
        setAnimations()
        setViewModelListener()
        groupId = intent.getIntExtra("groupId",-1)
        if(groupId == -1){
            this.finish()
        }
        SearchBarSetupUtil.setFontFamily(binding.searchBar,
            ResourcesCompat.getFont(this, R.font.robotoregular))

        getEligibleUsers()
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

    internal fun lockUI() {
        binding.loadingPB.visibility = View.VISIBLE
        binding.loadingFL.isClickable=true
        animationDimStart()
    }

    internal fun unlockUI() {
        binding.loadingPB.visibility = View.GONE
        binding.loadingFL.isClickable=false
        animationLitStart()
    }

    private fun setViewModelListener() {

        userViewModel.getUsersEligibleToGroupGetState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    unlockUI()
                    userList=resource.data!!.users
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

        groupViewModel.addUserToGroupGetState.observe(this) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    lockUI()
                }
                is Resource.Success -> {
                    holder.sendButton.setImageResource(R.drawable.asset_tick)
                    holder.sendButton.isClickable=false
                    adapter.visibilityState[userToAddId] = false
                    setResult(Activity.RESULT_OK, intent)
                    unlockUI()
                }
                is Resource.Error -> {
                    Toast.makeText(this, "Nie dodano użytkownika do grupy", Toast.LENGTH_SHORT).show()
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


    internal fun handleClientErrorCode(errorCode: ClientErrorCode) {
        when(errorCode) {
            else -> {}
        }
    }

    internal fun handleServerErrorCode(errorCode: String) {
        when(errorCode){
        }
    }

    internal fun showTechnicalErrorMessage() {
        Toast.makeText(this, getString(R.string.technical_error_message), Toast.LENGTH_LONG).show()
    }

    private fun getEligibleUsers(){
        userViewModel.getUsersEligibleToGroup(groupId)
    }

    private fun buildRecyclerView(){
        adapter= GroupUserAddAdapter(userList,this)
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
        val filteredlist = ArrayList<UserToGroupResponse>()

        for (item in userList) {
            if ("${item.firstName} ${item.lastName}".lowercase().contains(text.lowercase())) {
                filteredlist.add(item)
            }
            else if(item.email.lowercase().contains(text.lowercase())){
                filteredlist.add(item)
            }
        }
        if (filteredlist.isEmpty()) {
            //
        }
        adapter.filterList(filteredlist)
    }

    internal fun addUser(viewHolder: GroupUserAddAdapter.ViewHolder, userId:Int){
        holder=viewHolder
        userToAddId=userId
        groupViewModel.addUserToGroup(
            GroupAddUserRequest(
            userId
        ), groupId)
    }
}