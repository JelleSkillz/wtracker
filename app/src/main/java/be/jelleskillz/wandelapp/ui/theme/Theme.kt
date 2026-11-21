package be.jelleskillz.wandelapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** Simple light Material 3 theme. Map colors live elsewhere (see README "Kleuren"). */
@Composable
fun WandelTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content,
    )
}
