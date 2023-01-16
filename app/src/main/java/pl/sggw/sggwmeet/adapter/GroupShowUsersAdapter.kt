package pl.sggw.sggwmeet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.UserShowActivity
import pl.sggw.sggwmeet.activity.event.EventShowActivity
import pl.sggw.sggwmeet.activity.group.GroupShowActivity
import pl.sggw.sggwmeet.model.connector.dto.response.GroupMemberResponse

class GroupShowUsersAdapter(userList: ArrayList<GroupMemberResponse>, activity: GroupShowActivity, userIsAdmin: Boolean): RecyclerView.Adapter<GroupShowUsersAdapter.ViewHolder>() {
    private var userList: ArrayList<GroupMemberResponse>
    private lateinit var activity: GroupShowActivity
    internal var visibilityState: ArrayList<Int> = ArrayList<Int>()
    private var userIsAdmin=false
    private var defaultButtonVisibility = View.GONE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.group_user_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.visibility=visibilityState[position]
        if(visibilityState[position]==View.VISIBLE) {
            val model: GroupMemberResponse = userList[position]
            if(model.isAdmin){
                holder.removeButton.visibility=View.GONE
                holder.removeButton.setOnClickListener {}
            }
            else{
                holder.removeButton.visibility=defaultButtonVisibility
                holder.removeButton.setOnClickListener {
                    activity.userHolder=holder
                    activity.userPosition=position
                    activity.userToDeleteId=model.id
                    activity.deleteUserAlertDialog.show()
                }
            }
            holder.userName.setText("${model.firstName} ${model.lastName}")
            holder.userEmail.setText(model.email)

            holder.itemView.setOnClickListener {
                val newActivity = Intent(holder.itemView.context, UserShowActivity::class.java)
                    .putExtra("userId", model.id)
                activity.startActivity(newActivity)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var userName: TextView
        lateinit var removeButton: ImageButton
        lateinit var userEmail: TextView

        init {
            // initializing our views with their ids.
            userName = itemView.findViewById(R.id.group_user_item_name)
            removeButton = itemView.findViewById(R.id.group_user_item_BT)
            userEmail = itemView.findViewById(R.id.group_user_item_email)
        }
    }

    init {
        this.userList = userList
        this.activity = activity
        this.userIsAdmin = userIsAdmin
        if(userIsAdmin){
            defaultButtonVisibility=View.VISIBLE
        }
        for(item in userList){
            visibilityState.add(View.VISIBLE)
        }
    }

}