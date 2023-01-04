package pl.sggw.sggwmeet.domain

data class FoodMenu(
    val itemsByCategory: Map<String, List<FoodMenuItem>>
)
