package pl.sggw.sggwmeet.fragment.core.placedetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.domain.PlaceEvent
import java.text.SimpleDateFormat
import java.util.*

class EventRecyclerViewAdapter(
    private val context: Context
) : ListAdapter<PlaceEvent, RecyclerView.ViewHolder>(DiffCallback()){

    companion object {
        private val DATE_FORMATTER = SimpleDateFormat("dd.MM.yyyy", Locale.US)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_place_public_event, parent, false)
        return EventVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val review = getItem(position)
        val binder = holder as EventVH

        binder.nameTV.text = review.name
        binder.descriptionTV.text = review.description
        binder.authorTV.text = context.getString(R.string.event_creator_TV, review.authorFullName)
        binder.startDateTV.text = DATE_FORMATTER.format(review.startDate)
    }

    class DiffCallback : DiffUtil.ItemCallback<PlaceEvent>() {
        override fun areItemsTheSame(oldItem: PlaceEvent, newItem: PlaceEvent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PlaceEvent, newItem: PlaceEvent): Boolean {
            return oldItem.containsSameDataAs(newItem)
        }
    }

    class EventVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById(R.id.name_TV)
        val descriptionTV: TextView = itemView.findViewById(R.id.description_TV)
        val authorTV: TextView = itemView.findViewById(R.id.author_TV)
        val startDateTV: TextView = itemView.findViewById(R.id.start_date_TV)
    }
}