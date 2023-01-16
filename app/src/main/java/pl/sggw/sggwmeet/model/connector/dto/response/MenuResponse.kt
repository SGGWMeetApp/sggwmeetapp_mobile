package pl.sggw.sggwmeet.model.connector.dto.response

import java.math.BigDecimal

data class MenuResponse(
    var menu: List<MenuSection>
) {
    data class MenuSection(
        var category: String,
        var menuItems: List<MenuItem>
    ) {
        data class MenuItem(
            var name: String,
            var description: String?,
            var price: BigDecimal,
            var images: List<String>,
            var isVegan: Boolean
        ) {}
    }
}
