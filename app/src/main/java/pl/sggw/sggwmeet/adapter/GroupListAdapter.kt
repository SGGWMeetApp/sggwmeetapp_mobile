package pl.sggw.sggwmeet.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.group.GroupListActivity
import pl.sggw.sggwmeet.activity.group.GroupShowActivity
import pl.sggw.sggwmeet.model.connector.dto.response.GroupResponse

class GroupListAdapter(groupList: ArrayList<GroupResponse>, activity: Activity): RecyclerView.Adapter<GroupListAdapter.ViewHolder>() {
    private var groupList: ArrayList<GroupResponse>
    private val gson = Gson()
    private lateinit var activity: Activity

    fun filterList(filterList: ArrayList<GroupResponse>) {
        groupList = filterList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: GroupResponse = groupList[position]
        holder.groupName.setText(model.name)
        holder.groupAdmin.setText(model.adminData.firstName+" "+model.adminData.lastName)
        holder.groupMemberCount.setText("${activity.getString(R.string.member_count)}: ${model.memberCount}")
        holder.groupIncomingEvents.setText("${activity.getString(R.string.events_incoming)}: ${model.incomingEventsCount}")

        holder.itemView.setOnClickListener{
            val newActivity = Intent(holder.itemView.context, GroupShowActivity::class.java)
                .putExtra("groupData",gson.toJson(model))
            activity.startActivityForResult(newActivity, GroupListActivity.GROUP_EDITED)
        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var groupAdmin: TextView
        lateinit var groupMemberCount: TextView
        lateinit var groupName: TextView
        lateinit var groupIncomingEvents: TextView


        init {
            // initializing our views with their ids.
            groupAdmin = itemView.findViewById(R.id.group_admin)
            groupMemberCount = itemView.findViewById(R.id.group_member_count)
            groupName = itemView.findViewById(R.id.group_name)
            groupIncomingEvents = itemView.findViewById(R.id.group_event_count)
        }
    }

    init {
        this.groupList = groupList
        this.activity = activity
    }
}