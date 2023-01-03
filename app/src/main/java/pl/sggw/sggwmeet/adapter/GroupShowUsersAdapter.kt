package pl.sggw.sggwmeet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.UserShowActivity
import pl.sggw.sggwmeet.activity.event.EventShowActivity
import pl.sggw.sggwmeet.activity.group.GroupShowActivity
import pl.sggw.sggwmeet.model.connector.dto.response.GroupMemberResponse

class GroupShowUsersAdapter(userList: ArrayList<GroupMemberResponse>, activity: GroupShowActivity): RecyclerView.Adapter<GroupShowUsersAdapter.ViewHolder>() {
    private var userList: ArrayList<GroupMemberResponse>
    private lateinit var activity: GroupShowActivity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.group_user_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: GroupMemberResponse = userList[position]
        holder.userName.setText("${model.firstName} ${model.lastName}")
        holder.userEmail.setText(model.email)

        holder.itemView.setOnClickListener{
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
        lateinit var sendButton: ImageButton
        lateinit var userEmail: TextView

        init {
            // initializing our views with their ids.
            userName = itemView.findViewById(R.id.group_user_item_name)
            sendButton = itemView.findViewById(R.id.group_user_item_BT)
            userEmail = itemView.findViewById(R.id.group_user_item_email)
        }
    }

    init {
        this.userList = userList
        this.activity = activity
    }

}