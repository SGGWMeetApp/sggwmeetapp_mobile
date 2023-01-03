package pl.sggw.sggwmeet.fragment.core.placedetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pl.sggw.sggwmeet.R
import pl.sggw.sggwmeet.domain.FoodMenuItem
import java.text.DecimalFormat

class FoodItemRecyclerViewAdapter(
    private val context: Context,
    private val picasso: Picasso
) : ListAdapter<FoodMenuItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_food_menu_item, parent, false)
        return FoodItemVH(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        val binder = holder as FoodItemVH

        binder.foodItemNameTV.text = item.name
        if(!item.isVegan) binder.veganTV.visibility = View.GONE

        val price = DecimalFormat("#.##").format(item.price.toFloat())
        binder.foodItemPriceTV.text = context.getString(R.string.food_item_price_TV, price)

        setImage(item, binder.foodItemImageIV, binder.foodItemImageWrapper)
        binder.foodItemDescriptionTV.text = item.description
    }

    private fun setImage(
        item: FoodMenuItem,
        foodItemImageIV: AppCompatImageView,
        foodItemImageWrapper: CardView
    ) {
        if(item.imagePath == null) {
            foodItemImageWrapper.visibility = View.GONE
            return
        }

        picasso
            .load(item.imagePath)
            .placeholder(R.drawable.asset_loading)
            .into(foodItemImageIV)
    }

    class DiffCallback : DiffUtil.ItemCallback<FoodMenuItem>() {
        override fun areItemsTheSame(oldItem: FoodMenuItem, newItem: FoodMenuItem): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: FoodMenuItem, newItem: FoodMenuItem): Boolean {
            return oldItem.containsSameDataAs(newItem)
        }
    }

    class FoodItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val foodItemNameTV: TextView = itemView.findViewById(R.id.food_item_name_TV)
        val veganTV: TextView = itemView.findViewById(R.id.vegan_TV)
        val foodItemPriceTV: TextView = itemView.findViewById(R.id.food_item_price_TV)
        val foodItemImageIV: AppCompatImageView = itemView.findViewById(R.id.food_item_image_IV)
        val foodItemImageWrapper: CardView = itemView.findViewById(R.id.food_item_image_wrapper)
        val foodItemDescriptionTV: TextView = itemView.findViewById(R.id.food_item_description_TV)
    }
}