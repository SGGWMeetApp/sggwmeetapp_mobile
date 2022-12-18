package pl.sggw.sggwmeet.adapter

import android.app.Activity
import android.content.Intent
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData

class EventLocationListAdapter(locationList: ArrayList<SimplePlaceResponseData>, activity: Activity): RecyclerView.Adapter<EventLocationListAdapter.ViewHolder>() {
    private var locationList: ArrayList<SimplePlaceResponseData>
    private lateinit var activity: Activity

    fun filterList(filterList: ArrayList<SimplePlaceResponseData>) {
        locationList = filterList
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.event_location_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model: SimplePlaceResponseData = locationList[position]
        holder.locationName.setText(model.name)
        holder.locationAddress.setText(model.textLocation)
        if(model.reviewSummary.reviewsCount!! > 0){
            holder.locationRating.setText(
                "Ocena: ${String.format("%.0f",model.reviewSummary.positivePercent)}% (${model.reviewSummary.reviewsCount} ocen)"
            )
        }
        else{
            holder.locationRating.setText(
                "Brak ocen"
            )
        }
        holder.locationItemArea.setOnClickListener{
            if(holder.locationHiddenView.isGone){
                holder.locationDescription.setText(model.description)
                if(model.photoPath.isNullOrBlank()){
                    holder.locationImage.visibility=View.GONE
                }
                else{
                    val picasso = Picasso.get()
                    picasso.isLoggingEnabled = true
                    picasso
                        .load(model.photoPath)
                        .placeholder(R.drawable.asset_loading)
                        .into(holder.locationImage)
                }
                TransitionManager.beginDelayedTransition(holder.locationRoot, holder.transition)
                holder.locationHiddenView.visibility=View.VISIBLE
            }
            else{
                TransitionManager.beginDelayedTransition(holder.locationRoot, holder.transition)
                holder.locationHiddenView.visibility=View.GONE
            }
        }
        holder.locationSelectBT.setOnClickListener {
            val intent = Intent()
            intent.putExtra("returnedLocationID",model.id.toInt())
                .putExtra("returnedLocationName",model.name)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }
    }

    override fun getItemCount(): Int {
        return locationList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var locationName: TextView
        lateinit var locationAddress: TextView
        lateinit var locationRating: TextView
        lateinit var locationDescription: TextView
        lateinit var locationSelectBT: ImageButton
        lateinit var locationImage: ImageView
        lateinit var locationItemArea: LinearLayout
        lateinit var locationHiddenView: LinearLayout
        lateinit var locationRoot: ConstraintLayout
        val transition = AutoTransition()


        init {
            // initializing our views with their ids.
            locationName = itemView.findViewById(R.id.event_loc_name)
            locationAddress = itemView.findViewById(R.id.event_loc_location)
            locationRating = itemView.findViewById(R.id.event_loc_rating)
            locationDescription = itemView.findViewById(R.id.event_loc_description)
            locationSelectBT = itemView.findViewById(R.id.event_loc_select_BT)
            locationImage = itemView.findViewById(R.id.event_loc_image_IV)
            locationItemArea = itemView.findViewById(R.id.event_loc_info_area)
            locationHiddenView = itemView.findViewById(R.id.event_loc_hidden)
            locationRoot = itemView.findViewById(R.id.event_loc_root)
            transition.duration=200
        }
    }

    init {
        this.locationList = locationList
        this.activity = activity
    }
}