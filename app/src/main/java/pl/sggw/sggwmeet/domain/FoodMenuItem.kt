package pl.sggw.sggwmeet.domain

import java.math.BigDecimal

data class FoodMenuItem(
    var name: String,
    var description: String,
    var price: BigDecimal,
    var imagePath: String?,
    var isVegan: Boolean
) {
    fun containsSameDataAs(item: FoodMenuItem) : Boolean{
        return name == item.name &&
                description == item.description &&
                price == item.price &&
                imagePath == item.imagePath &&
                isVegan == item.isVegan

    }
}