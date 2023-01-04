package pl.sggw.sggwmeet.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.domain.Geolocation
import pl.sggw.sggwmeet.domain.PlaceMarkerData
import java.math.RoundingMode
import java.text.DecimalFormat

class PlacesAdapter(private val picasso: Picasso): RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {
    private var items: List<PlaceMarkerData>? = null
    private var userLocation: Location? = null

    fun submitItems(items: List<PlaceMarkerData>) {
        this.items = items
        this.notifyDataSetChanged()
    }

    fun submitUserLocation(userLocation: Location) {
        this.userLocation  = userLocation
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items == null) return
        val item = items!![position]
        holder.nameTV.text = item.name
        holder.categoryTV.text = item.category.polishTranslation
        holder.distanceTV.text = this.measureDistance(item.geolocation)
        holder.positiveReviewsTV.text = this.formatReviews(item.reviewsCount, item.positiveReviewsPercent)
        this.setImage(holder, item)
    }

    override fun getItemCount(): Int {
        return if (this.items == null) 0 else this.items!!.size
    }

    private fun setImage(holder: ViewHolder, item: PlaceMarkerData) {
        val path = item.photoPath
        if (item.photoPath.isNullOrEmpty()) {
            this.picasso
                .load(R.drawable.asset_no_image_available)
                .into(holder.imageIV)
        } else {
            this.picasso
                .load(path)
                .placeholder(R.drawable.asset_loading)
                .into(holder.imageIV)
        }
    }

    private fun formatReviews(opinions: Int, percent: Float?): String {
        return "${this.formatReviewsPercent(percent)} z $opinions"
    }

    private fun formatReviewsPercent(percent: Float?): String {
        if (percent == null) return "0.00%"
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.HALF_UP
        return "${df.format(percent)}%"
    }

    private fun measureDistance(placeGeolocation: Geolocation): String {
        if (this.userLocation == null) return ""
        val placeLocation = placeGeolocation.toLocation()
        val distance = this.userLocation!!.distanceTo(placeLocation)

        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.HALF_UP

        return if (distance >= 1000.00) "${df.format(distance / 1000)}KM"
        else "${df.format(distance)}M"
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById(R.id.place_name_TV)
        val categoryTV: TextView = itemView.findViewById(R.id.place_category_TV)
        val distanceTV: TextView = itemView.findViewById(R.id.place_distance_TV)
        val positiveReviewsTV: TextView = itemView.findViewById(R.id.place_positive_reviews_TV)
        val imageIV: ImageView = itemView.findViewById(R.id.place_image_IV)
    }
}