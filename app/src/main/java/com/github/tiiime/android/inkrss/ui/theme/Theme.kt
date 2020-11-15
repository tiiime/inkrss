package com.github.tiiime.android.inkrss.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val Colors = lightColors(
    primary = white,
    primaryVariant = black,
    secondary = half,
    secondaryVariant = black,
    background = white,
    surface = white,
    onPrimary = black,
    onSecondary = black,
    onSurface = black,
    onError = Color.Red
)

@Composable
fun InkRssTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
        colors = Colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}