package com.asta.cherryweather.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.asta.cherryweather.R

@Composable
fun CherryHeader(modifier: Modifier = Modifier, onSettingsClick: () -> Unit = {}) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(
                if (MaterialTheme.colorScheme.background.luminance() < 0.5f)
                    R.drawable.cherryapps_logo_full_dark else R.drawable.cherryapps_logo_full
            ),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(32.dp).padding(2.dp)
        )
        Text("Cherry Weather", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground)
        IconButton(onClick = onSettingsClick) {
            Icon(Icons.Rounded.Settings, contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
