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
import com.squareup.picasso.Picasso
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.activity.event.EventShowOnMapActivity
import pl.sggw.sggwmeet.model.connector.dto.response.SimplePlaceResponseData

class EventLocationListAdapter(locationList: ArrayList<SimplePlaceResponseData>, activity: Activity,
                               locationCategoryMap : HashMap<String,String>): RecyclerView.Adapter<EventLocationListAdapter.ViewHolder>() {
    private var locationList: ArrayList<SimplePlaceResponseData>
    private lateinit var activity: Activity
    private lateinit var picasso: Picasso
    private lateinit var locationCategoryMap : HashMap<String,String>
    private var visibilityState: HashMap<String, Int> = HashMap<String,Int>()

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
        holder.locationHiddenView.visibility = visibilityState[model.id]!!

        if(holder.locationHiddenView.visibility==View.VISIBLE){
            holder.locationDetailExpand.visibility=View.INVISIBLE
        }
        else holder.locationDetailExpand.visibility=View.VISIBLE

        holder.locationName.setText(model.name)
        holder.locationAddress.setText(model.textLocation)
        holder.locationCategory.setText(locationCategoryMap.get(model.id))
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
        holder.locationDescription.setText(model.description)
        if(model.photoPath.isNullOrBlank()){
            holder.locationImage.visibility=View.GONE
        }
        else{
            holder.locationImage.visibility=View.VISIBLE
            picasso
                .load(model.photoPath)
                .placeholder(R.drawable.asset_loading)
                .into(holder.locationImage)
        }

        holder.locationItemArea.setOnClickListener{
            if(holder.locationHiddenView.isGone){
                TransitionManager.beginDelayedTransition(holder.locationRoot, holder.transition)
                holder.locationHiddenView.visibility=View.VISIBLE
                visibilityState[model.id]=View.VISIBLE
                holder.locationDetailExpand.visibility=View.INVISIBLE
            }
            else{
                TransitionManager.beginDelayedTransition(holder.locationRoot, holder.transition)
                holder.locationHiddenView.visibility=View.GONE
                visibilityState[model.id]=View.GONE
                holder.locationDetailExpand.visibility=View.VISIBLE
            }
        }
        holder.locationDetailExpand.setOnClickListener{
            if(holder.locationHiddenView.isGone){
                TransitionManager.beginDelayedTransition(holder.locationRoot, holder.transition)
                holder.locationHiddenView.visibility=View.VISIBLE
                visibilityState[model.id]=View.VISIBLE
                holder.locationDetailExpand.visibility=View.INVISIBLE
            }
            else{
                TransitionManager.beginDelayedTransition(holder.locationRoot, holder.transition)
                holder.locationHiddenView.visibility=View.GONE
                visibilityState[model.id]=View.GONE
                holder.locationDetailExpand.visibility=View.VISIBLE
            }
        }
        holder.locationSelectBT.setOnClickListener {
            val intent = Intent()
            intent.putExtra("returnedLocationID",model.id.toInt())
                .putExtra("returnedLocationName",model.name)
            activity.setResult(Activity.RESULT_OK,intent)
            activity.finish()
        }
        holder.locationMapButton.setOnClickListener{
            val newActivity = Intent(holder.itemView.context, EventShowOnMapActivity::class.java)
                .putExtra("locationId",model.id.toInt())
            activity.startActivityForResult(newActivity, 123)
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
        lateinit var locationMapButton: ImageButton
        lateinit var locationDetailExpand: TextView
        lateinit var locationCategory: TextView
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
            locationMapButton = itemView.findViewById(R.id.event_loc_show_on_map_BT)
            locationDetailExpand = itemView.findViewById(R.id.event_loc_detail_info)
            locationCategory = itemView.findViewById(R.id.event_loc_category)
            transition.duration=200
        }
    }

    init {
        picasso = Picasso.get()
        picasso.isLoggingEnabled = true
        this.locationList = locationList
        this.activity = activity
        this.locationCategoryMap = locationCategoryMap
        for(item in locationList){
            visibilityState[item.id]=View.GONE
        }
    }
}