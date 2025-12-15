package com.gundogar.lineupapp.data.model

import androidx.compose.ui.graphics.Color
import com.gundogar.lineupapp.ui.theme.DefaultJerseyPrimary
import com.gundogar.lineupapp.ui.theme.DefaultJerseySecondary

data class TeamConfig(
    val teamName: String = "My Team",
    val primaryColor: Color = DefaultJerseyPrimary,
    val secondaryColor: Color = DefaultJerseySecondary,
    val jerseyStyle: JerseyStyle = JerseyStyle.SOLID
)
