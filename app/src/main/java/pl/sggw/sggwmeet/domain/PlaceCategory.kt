package pl.sggw.sggwmeet.domain

enum class PlaceCategory(val polishTranslation: String) {
    ALL("wszystkie"),
    RESTAURANT("restauracja"),
    BAR("bar"),
    PUB("pub"),
    GYM("siłownia"),
    CINEMA("kino"),
    ROOT_LOCATION("źródło"),
    OTHER("inne")
}