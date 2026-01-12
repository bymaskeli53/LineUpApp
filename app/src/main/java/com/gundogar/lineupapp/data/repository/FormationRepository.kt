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

    // 4-4-2 Classic Formation
    // Symmetric: GK center, 4 defenders spread evenly, 4 midfielders spread evenly, 2 strikers
    private fun create442(): Formation = Formation(
        id = "442",
        name = "4-4-2",
        displayName = "4-4-2 Classic",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),      // GK - center
            Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),       // LB
            Position(3, PositionRole.DEFENDER, 0.38f, 0.22f),       // LCB
            Position(4, PositionRole.DEFENDER, 0.62f, 0.22f),       // RCB
            Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),       // RB
            Position(6, PositionRole.MIDFIELDER, 0.12f, 0.48f),     // LM
            Position(7, PositionRole.MIDFIELDER, 0.38f, 0.45f),     // LCM
            Position(8, PositionRole.MIDFIELDER, 0.62f, 0.45f),     // RCM
            Position(9, PositionRole.MIDFIELDER, 0.88f, 0.48f),     // RM
            Position(10, PositionRole.FORWARD, 0.35f, 0.75f),       // LST
            Position(11, PositionRole.FORWARD, 0.65f, 0.75f)        // RST
        )
    )

    // 4-3-3 Attack Formation
    // Symmetric: GK center, 4 defenders, 3 midfielders (center heavy), 3 forwards
    private fun create433(): Formation = Formation(
        id = "433",
        name = "4-3-3",
        displayName = "4-3-3 Attack",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),      // GK - center
            Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),       // LB
            Position(3, PositionRole.DEFENDER, 0.38f, 0.22f),       // LCB
            Position(4, PositionRole.DEFENDER, 0.62f, 0.22f),       // RCB
            Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),       // RB
            Position(6, PositionRole.MIDFIELDER, 0.25f, 0.46f),     // LCM
            Position(7, PositionRole.MIDFIELDER, 0.5f, 0.42f),      // CDM - center
            Position(8, PositionRole.MIDFIELDER, 0.75f, 0.46f),     // RCM
            Position(9, PositionRole.FORWARD, 0.18f, 0.75f),        // LW
            Position(10, PositionRole.FORWARD, 0.5f, 0.78f),        // ST - center
            Position(11, PositionRole.FORWARD, 0.82f, 0.75f)        // RW
        )
    )

    // 4-2-3-1 Modern Formation
    // Symmetric: GK center, 4 defenders, 2 DMs, 3 AMs, 1 striker center
    private fun create4231(): Formation = Formation(
        id = "4231",
        name = "4-2-3-1",
        displayName = "4-2-3-1 Modern",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),      // GK - center
            Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),       // LB
            Position(3, PositionRole.DEFENDER, 0.38f, 0.22f),       // LCB
            Position(4, PositionRole.DEFENDER, 0.62f, 0.22f),       // RCB
            Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),       // RB
            Position(6, PositionRole.MIDFIELDER, 0.35f, 0.40f),     // LDM
            Position(7, PositionRole.MIDFIELDER, 0.65f, 0.40f),     // RDM
            Position(8, PositionRole.MIDFIELDER, 0.18f, 0.58f),     // LAM
            Position(9, PositionRole.MIDFIELDER, 0.5f, 0.56f),      // CAM - center
            Position(10, PositionRole.MIDFIELDER, 0.82f, 0.58f),    // RAM
            Position(11, PositionRole.FORWARD, 0.5f, 0.78f)         // ST - center
        )
    )

    // 3-5-2 Wing Play Formation
    // Symmetric: GK center, 3 CBs, 5 midfielders (wing backs + 3 central), 2 strikers
    private fun create352(): Formation = Formation(
        id = "352",
        name = "3-5-2",
        displayName = "3-5-2 Wing Play",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),      // GK - center
            Position(2, PositionRole.DEFENDER, 0.25f, 0.22f),       // LCB
            Position(3, PositionRole.DEFENDER, 0.5f, 0.20f),        // CB - center
            Position(4, PositionRole.DEFENDER, 0.75f, 0.22f),       // RCB
            Position(5, PositionRole.MIDFIELDER, 0.10f, 0.46f),     // LWB
            Position(6, PositionRole.MIDFIELDER, 0.30f, 0.43f),     // LCM
            Position(7, PositionRole.MIDFIELDER, 0.5f, 0.40f),      // CDM - center
            Position(8, PositionRole.MIDFIELDER, 0.70f, 0.43f),     // RCM
            Position(9, PositionRole.MIDFIELDER, 0.90f, 0.46f),     // RWB
            Position(10, PositionRole.FORWARD, 0.35f, 0.75f),       // LST
            Position(11, PositionRole.FORWARD, 0.65f, 0.75f)        // RST
        )
    )

    // 3-4-3 Offensive Formation
    // Symmetric: GK center, 3 CBs, 4 midfielders, 3 forwards
    private fun create343(): Formation = Formation(
        id = "343",
        name = "3-4-3",
        displayName = "3-4-3 Offensive",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),      // GK - center
            Position(2, PositionRole.DEFENDER, 0.25f, 0.22f),       // LCB
            Position(3, PositionRole.DEFENDER, 0.5f, 0.20f),        // CB - center
            Position(4, PositionRole.DEFENDER, 0.75f, 0.22f),       // RCB
            Position(5, PositionRole.MIDFIELDER, 0.12f, 0.46f),     // LM
            Position(6, PositionRole.MIDFIELDER, 0.38f, 0.43f),     // LCM
            Position(7, PositionRole.MIDFIELDER, 0.62f, 0.43f),     // RCM
            Position(8, PositionRole.MIDFIELDER, 0.88f, 0.46f),     // RM
            Position(9, PositionRole.FORWARD, 0.20f, 0.75f),        // LW
            Position(10, PositionRole.FORWARD, 0.5f, 0.78f),        // ST - center
            Position(11, PositionRole.FORWARD, 0.80f, 0.75f)        // RW
        )
    )

    // 5-3-2 Defensive Formation
    // Symmetric: GK center, 5 defenders (wing backs + 3 CBs), 3 midfielders, 2 strikers
    private fun create532(): Formation = Formation(
        id = "532",
        name = "5-3-2",
        displayName = "5-3-2 Defensive",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),      // GK - center
            Position(2, PositionRole.DEFENDER, 0.10f, 0.28f),       // LWB
            Position(3, PositionRole.DEFENDER, 0.30f, 0.22f),       // LCB
            Position(4, PositionRole.DEFENDER, 0.5f, 0.20f),        // CB - center
            Position(5, PositionRole.DEFENDER, 0.70f, 0.22f),       // RCB
            Position(6, PositionRole.DEFENDER, 0.90f, 0.28f),       // RWB
            Position(7, PositionRole.MIDFIELDER, 0.25f, 0.46f),     // LCM
            Position(8, PositionRole.MIDFIELDER, 0.5f, 0.43f),      // CM - center
            Position(9, PositionRole.MIDFIELDER, 0.75f, 0.46f),     // RCM
            Position(10, PositionRole.FORWARD, 0.35f, 0.75f),       // LST
            Position(11, PositionRole.FORWARD, 0.65f, 0.75f)        // RST
        )
    )

    // 5-4-1 Ultra Defensive Formation
    // Symmetric: GK center, 5 defenders, 4 midfielders, 1 striker center
    private fun create541(): Formation = Formation(
        id = "541",
        name = "5-4-1",
        displayName = "5-4-1 Ultra Defensive",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),      // GK - center
            Position(2, PositionRole.DEFENDER, 0.10f, 0.28f),       // LWB
            Position(3, PositionRole.DEFENDER, 0.30f, 0.22f),       // LCB
            Position(4, PositionRole.DEFENDER, 0.5f, 0.20f),        // CB - center
            Position(5, PositionRole.DEFENDER, 0.70f, 0.22f),       // RCB
            Position(6, PositionRole.DEFENDER, 0.90f, 0.28f),       // RWB
            Position(7, PositionRole.MIDFIELDER, 0.15f, 0.48f),     // LM
            Position(8, PositionRole.MIDFIELDER, 0.38f, 0.45f),     // LCM
            Position(9, PositionRole.MIDFIELDER, 0.62f, 0.45f),     // RCM
            Position(10, PositionRole.MIDFIELDER, 0.85f, 0.48f),    // RM
            Position(11, PositionRole.FORWARD, 0.5f, 0.78f)         // ST - center
        )
    )

    // 4-5-1 Classic Formation
    // Symmetric: GK center, 4 defenders, 5 midfielders, 1 striker center
    private fun create451(): Formation = Formation(
        id = "451",
        name = "4-5-1",
        displayName = "4-5-1 Classic",
        positions = listOf(
            Position(1, PositionRole.GOALKEEPER, 0.5f, 0.08f),      // GK - center
            Position(2, PositionRole.DEFENDER, 0.15f, 0.25f),       // LB
            Position(3, PositionRole.DEFENDER, 0.38f, 0.22f),       // LCB
            Position(4, PositionRole.DEFENDER, 0.62f, 0.22f),       // RCB
            Position(5, PositionRole.DEFENDER, 0.85f, 0.25f),       // RB
            Position(6, PositionRole.MIDFIELDER, 0.10f, 0.48f),     // LM
            Position(7, PositionRole.MIDFIELDER, 0.30f, 0.45f),     // LCM
            Position(8, PositionRole.MIDFIELDER, 0.5f, 0.42f),      // CDM - center
            Position(9, PositionRole.MIDFIELDER, 0.70f, 0.45f),     // RCM
            Position(10, PositionRole.MIDFIELDER, 0.90f, 0.48f),    // RM
            Position(11, PositionRole.FORWARD, 0.5f, 0.78f)         // ST - center
        )
    )
}
