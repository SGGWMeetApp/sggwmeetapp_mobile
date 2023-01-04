package pl.sggw.sggwmeet.fragment.core.placedetails.adapters.model

import pl.sggw.sggwmeet.domain.FoodMenuItem

data class FoodMenuSection(
    val category: String,
    val items: List<FoodMenuItem>
) {
    fun containsSameDataAs(section : FoodMenuSection) : Boolean {
        val sameCategory = category == section.category
        val sameItems = items == section.items
        return sameCategory && sameItems
    }
}