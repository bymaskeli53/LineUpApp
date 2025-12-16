package com.gundogar.lineupapp.data.model

data class Formation(
    val id: String,
    val name: String,
    val displayName: String,
    val positions: List<Position>,
    val playerCount: Int = 11,
    val isCustomizable: Boolean = false // true for 5-10 player custom layouts
)

enum class FormationType(val id: String, val displayName: String) {
    FOUR_FOUR_TWO("442", "4-4-2"),
    FOUR_THREE_THREE("433", "4-3-3"),
    FOUR_TWO_THREE_ONE("4231", "4-2-3-1"),
    THREE_FIVE_TWO("352", "3-5-2"),
    THREE_FOUR_THREE("343", "3-4-3"),
    FIVE_THREE_TWO("532", "5-3-2"),
    FIVE_FOUR_ONE("541", "5-4-1")
}
