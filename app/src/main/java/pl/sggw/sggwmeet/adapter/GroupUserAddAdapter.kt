package pl.sggw.sggwmeet.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pl.sggw.sggwmeet.R
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
    private var visibilityState: ArrayList<Int> = ArrayList<Int>()
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
        holder.sendButton.visibility=visibilityState[position]
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

        holder.sendButton.setOnClickListener{
            setViewModelListener(holder, model)
            groupViewModel.addUserToGroup(GroupAddUserRequest(
                model.id,
                position
            ), activity.groupId)
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

        init {
            // initializing our views with their ids.
            userName = itemView.findViewById(R.id.group_user_add_name)
            email = itemView.findViewById(R.id.group_user_add_email)
            sendButton = itemView.findViewById(R.id.group_user_add_BT)
            profilePicture = itemView.findViewById(R.id.group_add_user_avatar_IV)
        }
    }

    init {
        picasso = Picasso.get()
        picasso.isLoggingEnabled = true
        groupViewModel = activity.groupViewModel
        this.userList = userList
        this.activity = activity
        for(item in userList){
            visibilityState.add(View.VISIBLE)
        }
    }

    private fun setViewModelListener(holder:ViewHolder, model: UserToGroupResponse) {

        groupViewModel.addUserToGroupGetState.observe(activity) { resource ->
            when(resource) {
                is Resource.Loading -> {
                    activity.lockUI()
                }
                is Resource.Success -> {
                    Toast.makeText(activity, "Dodano ${model.firstName} ${model.lastName} do grupy", Toast.LENGTH_SHORT).show()
                    holder.sendButton.visibility=View.INVISIBLE
                    visibilityState[resource.data!!.position]=View.INVISIBLE
                    activity.setResult(Activity.RESULT_OK,activity.intent)
                    activity.unlockUI()
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