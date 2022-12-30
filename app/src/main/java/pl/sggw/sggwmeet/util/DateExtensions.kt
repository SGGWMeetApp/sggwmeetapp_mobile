package pl.sggw.sggwmeet.util

import java.util.*

fun Date.getPolishMonthName() : String {
    return when(month) {
        0 -> "styczeń"
        1 -> "luty"
        2 -> "marzec"
        3 -> "kwiecień"
        4 -> "maj"
        5 -> "czerwiec"
        6 -> "lipiec"
        7 -> "sierpień"
        8 -> "wrzesiń"
        9 -> "październik"
        10 -> "listopad"
        else -> "grudzień"
    }
}