package be.jelleskillz.wandelapp

import androidx.compose.runtime.Composable
import be.jelleskillz.wandelapp.ui.map.MapScreen

/** Root composable. Navigation between screens is added in a later step. */
@Composable
fun WandelApp() {
    MapScreen()
}
