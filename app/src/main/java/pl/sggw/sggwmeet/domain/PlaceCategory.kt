package pl.sggw.sggwmeet.domain

enum class PlaceCategory(val polishTranslation: String) {
    ALL("wszystkie"),
    RESTAURANT("restauracja"),
    BAR("bar"),
    PUB("pub"),
    GYM("siłownia"),
    CINEMA("kino"),
    ROOT_LOCATION("źródło"),
    OTHER("inne");

    companion object {
        fun getPolishTranslations(): Array<String> {
            val list = mutableListOf<String>()
            for (category in PlaceCategory.values()) list.add(category.polishTranslation)
            return Array(list.size) { index -> list[index] }
        }

        fun getCategoryByTranslation(translation: String): PlaceCategory? {
            for (category in PlaceCategory.values()) if (category.polishTranslation == translation) return category
            return null
        }
    }
}