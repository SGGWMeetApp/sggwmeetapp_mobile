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
import pl.sggw.sggwmeet.activity.event.EventListActivity
import pl.sggw.sggwmeet.activity.event.EventShowActivity
import pl.sggw.sggwmeet.model.connector.dto.response.EventResponse
import java.text.SimpleDateFormat
import kotlin.coroutines.coroutineContext

class EventListAdapter(eventList: ArrayList<EventResponse>, activity: Activity): RecyclerView.Adapter<EventListAdapter.ViewHolder>() {
    private var eventList: ArrayList<EventResponse>
    private val gson = Gson()
    private val timeFormat = SimpleDateFormat("dd.MM.yyyy' 'HH:mm")
    private lateinit var activity: Activity

    fun filterList(filterList: ArrayList<EventResponse>) {
        eventList = filterList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: EventResponse = eventList[position]
        holder.eventDate.setText(timeFormat.format(model.startDate))
        holder.eventName.setText(model.name)
        holder.eventLocation.setText(model.locationData.name)
        holder.eventAuthor.setText(model.author.firstName+" "+model.author.lastName)
        holder.eventDescription.setText(model.description)
        holder.eventAttenders.setText("Uczestnicy: ${model.attendersCount}")
        if(model.description.isNullOrBlank()){
            holder.eventDescription.visibility=View.GONE
        }
        holder.itemView.setOnClickListener{
            val newActivity = Intent(holder.itemView.context, EventShowActivity::class.java)
                .putExtra("eventData",gson.toJson(model))
            activity.startActivityForResult(newActivity, EventListActivity.EVENT_EDITED)
        }
    }

    override fun getItemCount(): Int {
        return eventList.size
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var eventDate: TextView
        lateinit var eventName: TextView
        lateinit var eventLocation: TextView
        lateinit var eventAuthor: TextView
        lateinit var eventDescription: TextView
        lateinit var eventAttenders: TextView


        init {
            // initializing our views with their ids.
            eventDate = itemView.findViewById(R.id.event_date)
            eventName = itemView.findViewById(R.id.event_name)
            eventLocation = itemView.findViewById(R.id.event_location)
            eventAuthor = itemView.findViewById(R.id.event_author)
            eventDescription = itemView.findViewById(R.id.event_description)
            eventAttenders = itemView.findViewById(R.id.event_attenders)
        }
    }

    init {
        this.eventList = eventList
        this.activity = activity
    }
}