package pl.sggw.sggwmeet.fragment.core.placedetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
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

    private var onJoinClick: ((eventId: Int) -> Unit)? = null
    private var onLeaveClick: ((eventId: Int) -> Unit)? = null
    private var onEditClick: ((event: PlaceEvent) -> Unit)? = null

    fun addItemOnTop(event: PlaceEvent) {
        val newList = currentList.toMutableList()
        newList.add(0, event)
        submitList(newList)
    }

    fun markAsJoining(eventId: Int) {
        val position = currentList.indexOf(currentList.first{ it.id.toInt() == eventId })
        getItem(position).isJoining = true
        notifyItemChanged(position)
    }

    fun markAsLeaving(eventId: Int) {
        val position = currentList.indexOf(currentList.first{ it.id.toInt() == eventId })
        getItem(position).isLeaving = true
        notifyItemChanged(position)
    }

    fun markAsEditing(eventId: Int) {
        val position = currentList.indexOf(currentList.first{ it.id.toInt() == eventId })
        getItem(position).isEditing = true
        notifyItemChanged(position)
    }

    fun unmarkJoining() {
        val position = currentList.indexOf(currentList.first{ it.isJoining})
        currentList[position].isJoining = false
        notifyItemChanged(position)
    }

    fun unmarkLeaving() {
        val position = currentList.indexOf(currentList.first{ it.isLeaving})
        currentList[position].isLeaving = false
        notifyItemChanged(position)
    }

    fun unmarkEditing() {
        val position = currentList.indexOf(currentList.first{ it.isEditing})
        currentList[position].isEditing = false
        notifyItemChanged(position)
    }

    fun confirmJoin() {
        val position = currentList.indexOf(currentList.first{ it.isJoining})
        val item = currentList[position]
        item.isJoining = false
        item.attendersCount++
        item.userAttends = true
        notifyItemChanged(position)
    }

    fun confirmLeave() {
        val position = currentList.indexOf(currentList.first{ it.isLeaving})
        val item = currentList[position]
        item.isLeaving = false
        item.attendersCount--
        item.userAttends = false
        notifyItemChanged(position)
    }

    fun confirmEdit(placeEvent: PlaceEvent) {
        val position = currentList.indexOf(currentList.first{ it.isEditing})
        val newList = currentList.toMutableList()
        newList[position] = placeEvent
        submitList(newList)
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

        binder.attendersCountTV.text = context.getString(R.string.event_participant_count_TV, review.attendersCount)

        if(!review.userAttends) {
            binder.userAttendsTV.visibility = View.GONE
        } else {
            binder.userAttendsTV.visibility = View.VISIBLE
        }

        setJoinButton(review, binder.joinBT, position)
        setLeaveButton(review, binder.leaveBT, position)
        setEditButton(review, binder.editBT, position)
    }

    private fun setEditButton(review: PlaceEvent, editBT: AppCompatImageButton, position: Int) {
        if(!review.canEdit) {
            editBT.visibility = View.GONE
        } else {
            editBT.visibility = View.VISIBLE
        }

        if(review.isEditing || review.isJoining || review.isLeaving) {
            editBT.isEnabled = false
        } else {
            editBT.isEnabled = true
        }

        if(review.isEditing) {
            editBT.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_loading))
        } else {
            editBT.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_edit_pen))
        }

        if(onEditClick != null) {
            editBT.setOnClickListener {
                onEditClick!!(review)
            }
        }
    }

    private fun setLeaveButton(review: PlaceEvent, leaveBT: AppCompatImageButton, position: Int) {
        if(review.isLeaving) {
            leaveBT.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_loading))
        } else {
            leaveBT.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_minus))
        }

        if(review.isJoining || !review.userAttends || review.isLeaving || review.isEditing) {
            leaveBT.isEnabled = false
        } else {
            leaveBT.isEnabled = true
        }

        if(!review.userAttends) {
            leaveBT.alpha = .5f
        } else {
            leaveBT.alpha = 1.0f
        }

        if(onLeaveClick != null) {
            leaveBT.setOnClickListener {
                onLeaveClick!!(review.id.toInt())
            }
        }
    }

    private fun setJoinButton(review: PlaceEvent, joinBT: AppCompatImageButton, position: Int) {
        if(review.isJoining) {
            joinBT.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_loading))
        } else {
            joinBT.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.asset_plus))
        }

        if(review.isJoining || review.userAttends || review.isLeaving || review.isEditing) {
            joinBT.isEnabled = false
        } else {
            joinBT.isEnabled = true
        }

        if(review.userAttends) {
            joinBT.alpha = .5f
        } else {
            joinBT.alpha = 1.0f
        }

        if(onJoinClick != null) {
            joinBT.setOnClickListener {
                onJoinClick!!(review.id.toInt())
            }
        }
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

        val attendersCountTV: TextView = itemView.findViewById(R.id.participants_count_TV)
        val userAttendsTV: TextView = itemView.findViewById(R.id.user_participating_TV)
        val editBT: AppCompatImageButton = itemView.findViewById(R.id.edit_BT)
        val joinBT: AppCompatImageButton = itemView.findViewById(R.id.join_BT)
        val leaveBT: AppCompatImageButton = itemView.findViewById(R.id.leave_BT)
    }

    fun setOnJoinClickListener(action: (eventId: Int) -> Unit) {
        onJoinClick = action
    }

    fun setOnLeaveClickListener(action: (eventId: Int) -> Unit) {
        onLeaveClick = action
    }

    fun setOnEditClickListener(action: (event: PlaceEvent) -> Unit) {
        onEditClick = action
    }
}