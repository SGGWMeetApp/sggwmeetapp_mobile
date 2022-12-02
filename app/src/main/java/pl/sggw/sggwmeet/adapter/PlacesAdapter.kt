package pl.sggw.sggwmeet.adapter

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.domain.Geolocation
import pl.sggw.sggwmeet.domain.PlaceMarkerData

class PlacesAdapter(private val items: List<PlaceMarkerData>, private val currentLocation: Geolocation? = null): RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById(R.id.name_TV)
        val optionTV: TextView = view.findViewById(R.id.opinion_TV)
        val locationTV: TextView = view.findViewById(R.id.location_TV)
        val typeTV: TextView = view.findViewById(R.id.type_TV)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.nameTV.text = item.name
        holder.optionTV.text = "100 [100]"
        if (currentLocation != null )holder.locationTV.text = this.measureDistance(item.geolocation, this.currentLocation)
        holder.typeTV.text = item.category.toString()
    }

    override fun getItemCount(): Int {
        return  items.size
    }

    private fun measureDistance(g1: Geolocation, g2: Geolocation): String {
        val l1: Location = g1.toLocation()
        val l2: Location = g2.toLocation()
        return "${l1.distanceTo(l2)}m"
    }
}