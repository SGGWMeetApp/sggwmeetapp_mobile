package pl.sggw.sggwmeet.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.UserShowActivity
import pl.sggw.sggwmeet.activity.group.GroupAddUserListActivity
import pl.sggw.sggwmeet.exception.ClientException
import pl.sggw.sggwmeet.exception.ServerException
import pl.sggw.sggwmeet.exception.TechnicalException
import pl.sggw.sggwmeet.model.connector.dto.request.GroupAddUserRequest
import pl.sggw.sggwmeet.model.connector.dto.response.UserToGroupResponse
import pl.sggw.sggwmeet.util.Resource
import pl.sggw.sggwmeet.viewmodel.GroupViewModel

class GroupUserAddAdapter(userList: ArrayList<UserToGroupResponse>, activity: GroupAddUserListActivity): RecyclerView.Adapter<GroupUserAddAdapter.ViewHolder>() {
    private var userList: ArrayList<UserToGroupResponse>
    private lateinit var activity: GroupAddUserListActivity
    private lateinit var picasso: Picasso
    private var visibilityState: ArrayList<Boolean> = ArrayList<Boolean>()
    private lateinit var groupViewModel: GroupViewModel

    fun filterList(filterList: ArrayList<UserToGroupResponse>) {
        userList = filterList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.group_add_user_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: UserToGroupResponse = userList[position]
        holder.internalPosition=position
        if(visibilityState[position]){
            holder.sendButton.isClickable=true
            holder.sendButton.setImageResource(R.drawable.asset_plus)
            holder.sendButton.setOnClickListener{
                setViewModelListener(holder, model)
                groupViewModel.addUserToGroup(GroupAddUserRequest(
                    model.id,
                    position
                ), activity.groupId)
            }
        }
        else{
            holder.sendButton.isClickable=false
            holder.sendButton.setImageResource(R.drawable.asset_tick)
        }
        holder.userName.setText("${model.firstName} ${model.lastName}")
        holder.email.setText(model.email)
        if(model.avatarUrl.isNullOrBlank()){
            holder.profilePicture.setImageResource(R.drawable.avatar_1)
        }
        else{
            picasso
                .load(model.avatarUrl)
                .placeholder(R.drawable.avatar_1)
                .into(holder.profilePicture)
        }
        holder.infoArea.setOnClickListener{
            val newActivity = Intent(holder.itemView.context, UserShowActivity::class.java)
                .putExtra("userId",model.id)
            activity.startActivity(newActivity)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var userName: TextView
        lateinit var email: TextView
        lateinit var sendButton: ImageButton
        lateinit var profilePicture: ImageView
        lateinit var infoArea: LinearLayout
        var internalPosition=-1

        init {
            // initializing our views with their ids.
            userName = itemView.findViewById(R.id.group_user_add_name)
            email = itemView.findViewById(R.id.group_user_add_email)
            sendButton = itemView.findViewById(R.id.group_user_add_BT)
            profilePicture = itemView.findViewById(R.id.group_add_user_avatar_IV)
            infoArea = itemView.findViewById(R.id.group_add_user_info_area)
        }
    }

    init {
        picasso = Picasso.get()
        picasso.isLoggingEnabled = true
        groupViewModel = activity.groupViewModel
        this.userList = userList
        this.activity = activity
        for(item in userList){
            visibilityState.add(true)
        }
    }

    private fun setViewModelListener(holder:ViewHolder, model: UserToGroupResponse) {

        groupViewModel.addUserToGroupGetState.observe(activity) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    activity.lockUI()
                }
                is Resource.Success -> {
                    if(holder.internalPosition == resource.data!!.position) {
                        holder.sendButton.setImageResource(R.drawable.asset_tick)
                        holder.sendButton.isClickable=false
                        visibilityState[resource.data!!.position] = false
                        activity.setResult(Activity.RESULT_OK, activity.intent)
                        activity.unlockUI()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(activity, "Nie dodano ${model.firstName} ${model.lastName} do grupy", Toast.LENGTH_SHORT).show()
                    activity.unlockUI()
                    when(resource.exception) {

                        is TechnicalException -> {
                            activity.showTechnicalErrorMessage()
                        }
                        is ServerException -> {
                            activity.handleServerErrorCode(resource.exception.errorCode)
                        }
                        is ClientException -> {
                            activity.handleClientErrorCode(resource.exception.errorCode)
                        }
                    }
                    activity.finish()
                }
            }
        }
    }

}