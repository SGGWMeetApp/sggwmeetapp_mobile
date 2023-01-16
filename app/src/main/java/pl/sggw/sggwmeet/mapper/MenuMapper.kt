package pl.sggw.sggwmeet.mapper

import pl.sggw.sggwmeet.domain.FoodMenu
import pl.sggw.sggwmeet.domain.FoodMenuItem
import pl.sggw.sggwmeet.model.connector.dto.response.MenuResponse

class MenuMapper {

    fun mapToDomain(menuResponse: MenuResponse) : FoodMenu {
        return FoodMenu(
            menuResponse.menu.associate { it.category to mapToDomain(it.menuItems) }
        )
    }

    private fun mapToDomain(menuResponseItems: List<MenuResponse.MenuSection.MenuItem>) : List<FoodMenuItem> {
        return menuResponseItems.map { mapToFoodMenuItem(it) }
    }

    private fun mapToFoodMenuItem(menuResponseItem: MenuResponse.MenuSection.MenuItem) : FoodMenuItem {
        return FoodMenuItem(
            menuResponseItem.name,
            menuResponseItem.description ?: "",
            menuResponseItem.price,
            if(menuResponseItem.images.isNotEmpty()) menuResponseItem.images[0] else null,
            menuResponseItem.isVegan
        )
    }
}