package com.gundogar.lineupapp.ui.screens.pitches

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gundogar.lineupapp.R
import com.gundogar.lineupapp.data.model.FootballPitch
import com.gundogar.lineupapp.ui.screens.pitches.components.PitchCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PitchListView(
    pitches: List<FootballPitch>,
    isLoading: Boolean,
    error: String?,
    onRefresh: () -> Unit
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            error != null && pitches.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRefresh) {
                        Text(stringResource(R.string.btn_retry))
                    }
                }
            }
            pitches.isEmpty() && !isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.pitches_empty),
                        textAlign = TextAlign.Center
                    )
                }
            }
            isLoading && pitches.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(pitches, key = { it.id }) { pitch ->
                        PitchCard(pitch = pitch)
                    }
                }
            }
        }
    }
}
