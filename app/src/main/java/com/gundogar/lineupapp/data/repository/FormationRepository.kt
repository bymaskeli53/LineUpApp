package com.gundogar.lineupapp.data.repository

import com.gundogar.lineupapp.data.model.Formation
import com.gundogar.lineupapp.data.model.Position
import com.gundogar.lineupapp.data.model.PositionRole

object FormationRepository {

    private val formations = listOf(
        create442(),
        create433(),
        create4231(),
        create352(),
        create343(),
        create532(),
        create541(),
        create451()
    )

    fun getAllFormations(): List<Formation> = formations

    fun getFormationById(id: String): Formation? {
        // Check standard formations first
        formations.find { it.id == id }?.let { return it }

        // Check if it's a custom layout ID
        if (id.startsWith("custom_")) {
            val playerCount = id.removePrefix("custom_").toIntOrNull()
            if (playerCount != null && playerCount in 5..10) {
                return getDefaultCustomLayout(playerCount)
            }
        }
        return null
    }

    fun getCustomLayoutPlayerCounts(): List<Int> = listOf(5, 6, 7, 8, 9, 10)

    fun getDefaultCustomLayout(playerCount: Int): Formation {
        require(playerCount in 5..10) { "Player count must be between 5 and 10" }

        return Formation(
            id = "custom_$playerCount",
            name = "$playerCount-a-side",
            displayName = "$playerCount-a-side Custom",
            positions = generateDefaultPositions(playerCount),
            playerCount = playerCount,
            isCustomizable = true
        )
    }

    private fun generateDefaultPositions(playerCount: Int): List<Position> {
        return when (playerCount) {
            5 -> listOf(
                Position(1, PositionRole.GOALKEEPER, 0.5f, 0.10f),
                Position(2, PositionRole.DEFENDER, 0.25f, 0.30f),
                Position(3, PositionRole.DEFENDER, 0.75f, 0.30f),
                Position(4, PositionRole.MIDFIELDER, 0.5f, 0.55f),
                Position(5, PositionRole.FORWARD, 0.5f, 0.80f)
            )
            6 -> listOf(
                Position(1, PositionRole.GOALKEEPER, 0.5f, 0.10f),
                Position(2, PositionRole.DEFENDER, 0.25f, 0.28f),
                Position(3, PositionRole.DEFENDER, 0.75f, 0.28f),
                Position(4, PositionRole.MIDFIELDER, 0.30f, 0.55f),
                Position(5, PositionRole.MIDFIELDER, 0.70f, 0.55f),
                Position(6, PositionRole.FORWARD, 0.5f, 0.80f)
            )
            7 -> listOf(
                Position(1, PositionRole.GOALKEEPER, 0.5f, 0.10f),
                Position(2, PositionRole.DEFENDER, 0.20f, 0.28f),
                Position(3, PositionRole.DEFENDER, 0.5f, 0.25f),
                Position(4, PositionRole.DEFENDER, 0.80f, 0.28f),
                Position(5, PositionRole.MIDFIELDER, 0.35f, 0.52f),
                Position(6, PositionRole.MIDFIELDER, 0.65f, 0.52f),
                Position(7, PositionRole.FORWARD, 0.5f, 0.80f)
            )
            8 -> listOf(
                Position(1, PositionRole.GOALKEEPER, 0.5f, 0.10f),
                Position(2, PositionRole.DEFENDER, 0.20f, 0.28f),
                Position(3, PositionRole.DEFENDER, 0.5f, 0.25f),
                Position(4, PositionRole.DEFENDER, 0.80f, 0.28f),
                Position(5, PositionRole.MIDFIELDER, 0.25f, 0.52f),
                Position(6, PositionRole.MIDFIELDER, 0.75f, 0.52f),
                Position(7, PositionRole.FORWARD, 0.35f, 0.78f),
                Position(8, PositionRole.FORWARD, 0.65f, 0.78f)
            )
            9 -> listOf(
                Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),
                Position(2, PositionRole.DEFENDER, 0.18f, 0.25f),
                Position(3, PositionRole.DEFENDER, 0.5f, 0.22f),
                Position(4, PositionRole.DEFENDER, 0.82f, 0.25f),
                Position(5, PositionRole.MIDFIELDER, 0.20f, 0.50f),
                Position(6, PositionRole.MIDFIELDER, 0.5f, 0.47f),
                Position(7, PositionRole.MIDFIELDER, 0.80f, 0.50f),
                Position(8, PositionRole.FORWARD, 0.35f, 0.77f),
                Position(9, PositionRole.FORWARD, 0.65f, 0.77f)
            )
            10 -> listOf(
                Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),
                Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),
                Position(3, PositionRole.DEFENDER, 0.40f, 0.22f),
                Position(4, PositionRole.DEFENDER, 0.60f, 0.22f),
                Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),
                Position(6, PositionRole.MIDFIELDER, 0.25f, 0.50f),
                Position(7, PositionRole.MIDFIELDER, 0.5f, 0.47f),
                Position(8, PositionRole.MIDFIELDER, 0.75f, 0.50f),
                Position(9, PositionRole.FORWARD, 0.35f, 0.77f),
                Position(10, PositionRole.FORWARD, 0.65f, 0.77f)
            )
            else -> emptyList()
        }
    }

    private fun create442(): Formation = Formation(
        id = "442",
        name = "4-4-2",
        displayName = "4-4-2 Classic",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.48f, 0.08f),
            Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),
            Position(3, PositionRole.DEFENDER, 0.38f, 0.22f),
            Position(4, PositionRole.DEFENDER, 0.62f, 0.22f),
            Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),
            Position(6, PositionRole.MIDFIELDER, 0.12f, 0.50f),
            Position(7, PositionRole.MIDFIELDER, 0.37f, 0.47f),
            Position(8, PositionRole.MIDFIELDER, 0.63f, 0.47f),
            Position(9, PositionRole.MIDFIELDER, 0.88f, 0.50f),
            Position(10, PositionRole.FORWARD, 0.35f, 0.75f),
            Position(11, PositionRole.FORWARD, 0.65f, 0.75f)
        )
    )

    private fun create433(): Formation = Formation(
        id = "433",
        name = "4-3-3",
        displayName = "4-3-3 Attack",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.47f, 0.08f),
            Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),
            Position(3, PositionRole.DEFENDER, 0.38f, 0.22f),
            Position(4, PositionRole.DEFENDER, 0.62f, 0.22f),
            Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),
            Position(6, PositionRole.MIDFIELDER, 0.25f, 0.48f),
            Position(7, PositionRole.MIDFIELDER, 0.47f, 0.45f),
            Position(8, PositionRole.MIDFIELDER, 0.69f, 0.48f),
            Position(9, PositionRole.FORWARD, 0.18f, 0.75f),
            Position(10, PositionRole.FORWARD, 0.47f, 0.78f),
            Position(11, PositionRole.FORWARD, 0.76f, 0.75f)
        )
    )

    private fun create4231(): Formation = Formation(
        id = "4231",
        name = "4-2-3-1",
        displayName = "4-2-3-1 Modern",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.47f, 0.08f),
            Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),
            Position(3, PositionRole.DEFENDER, 0.38f, 0.22f),
            Position(4, PositionRole.DEFENDER, 0.62f, 0.22f),
            Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),
            Position(6, PositionRole.MIDFIELDER, 0.35f, 0.42f),
            Position(7, PositionRole.MIDFIELDER, 0.65f, 0.42f),
            Position(8, PositionRole.MIDFIELDER, 0.18f, 0.60f),
            Position(9, PositionRole.MIDFIELDER, 0.47f, 0.58f),
            Position(10, PositionRole.MIDFIELDER, 0.76f, 0.60f),
            Position(11, PositionRole.FORWARD, 0.47f, 0.78f)
        )
    )

    private fun create352(): Formation = Formation(
        id = "352",
        name = "3-5-2",
        displayName = "3-5-2 Wing Play",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.47f, 0.08f),
            Position(2, PositionRole.DEFENDER, 0.25f, 0.22f),
            Position(3, PositionRole.DEFENDER, 0.47f, 0.20f),
            Position(4, PositionRole.DEFENDER, 0.69f, 0.22f),
            Position(5, PositionRole.MIDFIELDER, 0.10f, 0.48f),
            Position(6, PositionRole.MIDFIELDER, 0.30f, 0.45f),
            Position(7, PositionRole.MIDFIELDER, 0.47f, 0.42f),
            Position(8, PositionRole.MIDFIELDER, 0.64f, 0.45f),
            Position(9, PositionRole.MIDFIELDER, 0.90f, 0.48f),
            Position(10, PositionRole.FORWARD, 0.35f, 0.75f),
            Position(11, PositionRole.FORWARD, 0.65f, 0.75f)
        )
    )

    private fun create343(): Formation = Formation(
        id = "343",
        name = "3-4-3",
        displayName = "3-4-3 Offensive",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.47f, 0.08f),
            Position(2, PositionRole.DEFENDER, 0.25f, 0.22f),
            Position(3, PositionRole.DEFENDER, 0.47f, 0.20f),
            Position(4, PositionRole.DEFENDER, 0.75f, 0.22f),
            Position(5, PositionRole.MIDFIELDER, 0.12f, 0.48f),
            Position(6, PositionRole.MIDFIELDER, 0.37f, 0.45f),
            Position(7, PositionRole.MIDFIELDER, 0.63f, 0.45f),
            Position(8, PositionRole.MIDFIELDER, 0.88f, 0.48f),
            Position(9, PositionRole.FORWARD, 0.20f, 0.75f),
            Position(10, PositionRole.FORWARD, 0.47f, 0.78f),
            Position(11, PositionRole.FORWARD, 0.80f, 0.75f)
        )
    )

    private fun create532(): Formation = Formation(
        id = "532",
        name = "5-3-2",
        displayName = "5-3-2 Defensive",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.47f, 0.08f),
            Position(2, PositionRole.DEFENDER, 0.10f, 0.28f),
            Position(3, PositionRole.DEFENDER, 0.30f, 0.22f),
            Position(4, PositionRole.DEFENDER, 0.47f, 0.20f),
            Position(5, PositionRole.DEFENDER, 0.70f, 0.22f),
            Position(6, PositionRole.DEFENDER, 0.90f, 0.28f),
            Position(7, PositionRole.MIDFIELDER, 0.25f, 0.48f),
            Position(8, PositionRole.MIDFIELDER, 0.5f, 0.45f),
            Position(9, PositionRole.MIDFIELDER, 0.75f, 0.48f),
            Position(10, PositionRole.FORWARD, 0.35f, 0.75f),
            Position(11, PositionRole.FORWARD, 0.65f, 0.75f)
        )
    )

    private fun create541(): Formation = Formation(
        id = "541",
        name = "5-4-1",
        displayName = "5-4-1 Ultra Defensive",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.47f, 0.08f),
            Position(2, PositionRole.DEFENDER, 0.10f, 0.28f),
            Position(3, PositionRole.DEFENDER, 0.30f, 0.22f),
            Position(4, PositionRole.DEFENDER, 0.5f, 0.20f),
            Position(5, PositionRole.DEFENDER, 0.70f, 0.22f),
            Position(6, PositionRole.DEFENDER, 0.90f, 0.28f),
            Position(7, PositionRole.MIDFIELDER, 0.15f, 0.50f),
            Position(8, PositionRole.MIDFIELDER, 0.38f, 0.47f),
            Position(9, PositionRole.MIDFIELDER, 0.62f, 0.47f),
            Position(10, PositionRole.MIDFIELDER, 0.85f, 0.50f),
            Position(11, PositionRole.FORWARD, 0.47f, 0.78f)
        )
    )

    private fun create451(): Formation = Formation(
        id = "451",
        name = "4-5-1",
        displayName = "4-5-1 Classic",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.47f, 0.08f),
            Position(2,PositionRole.DEFENDER, 0.15f, 0.28f),
            Position(3, PositionRole.DEFENDER, 0.38f, 0.28f),
            Position(4, PositionRole.DEFENDER, 0.62f, 0.28f),
            Position(5, PositionRole.DEFENDER, 0.85f, 0.28f),
            Position(6, PositionRole.MIDFIELDER, 0.10f, 0.50f),
            Position(7, PositionRole.MIDFIELDER, 0.30f, 0.47f),
            Position(8, PositionRole.MIDFIELDER, 0.47f, 0.45f),
            Position(9,PositionRole.MIDFIELDER, 0.64f, 0.47f),
            Position(10, PositionRole.MIDFIELDER, 0.85f, 0.50f),
            Position(11,PositionRole.FORWARD, 0.47f, 0.78f)

        )
    )
}
