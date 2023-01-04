package pl.sggw.sggwmeet.fragment.core.placedetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.fragment.core.placedetails.adapters.model.FoodMenuSection

class FoodItemsSectionRecyclerViewAdapter(
    private val context: Context,
    private val picasso: Picasso
) : ListAdapter<FoodMenuSection, RecyclerView.ViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_food_menu_category, parent, false)
        return SectionVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val section = getItem(position)
        val binder = holder as SectionVH

        binder.categoryNameTV.text = section.category

        val itemsAdapter = FoodItemRecyclerViewAdapter(context, picasso)
        binder.foodItemsRV.layoutManager = LinearLayoutManager(context)
        binder.foodItemsRV.adapter = itemsAdapter
        itemsAdapter.submitList(section.items)
    }

    class DiffCallback : DiffUtil.ItemCallback<FoodMenuSection>() {
        override fun areItemsTheSame(oldItem: FoodMenuSection, newItem: FoodMenuSection): Boolean {
            return oldItem.category == newItem.category
        }

        override fun areContentsTheSame(oldItem: FoodMenuSection, newItem: FoodMenuSection): Boolean {
            return oldItem.containsSameDataAs(newItem)
        }
    }

    class SectionVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryNameTV: TextView = itemView.findViewById(R.id.category_name_TV)
        val foodItemsRV: RecyclerView = itemView.findViewById(R.id.food_items_RV)
    }
}