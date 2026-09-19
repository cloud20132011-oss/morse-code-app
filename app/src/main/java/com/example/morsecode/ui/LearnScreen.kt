package com.example.morsecode.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.morsecode.audio.MorsePlayer
import com.example.morsecode.data.AppStrings
import com.example.morsecode.data.MorseCode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
    strings: AppStrings,
    player: MorsePlayer,
    wpm: Int,
    onWpmChange: (Int) -> Unit,
    isDutch: Boolean
) {
    var filter by remember { mutableStateOf(0) } // 0 all, 1 letters, 2 numbers, 3 punct
    var playingCode by remember { mutableStateOf<String?>(null) }

    val items: List<Pair<Char, String>> = when (filter) {
        1 -> MorseCode.letters
        2 -> MorseCode.numbers
        3 -> MorseCode.punctuation
        else -> MorseCode.letters + MorseCode.numbers
    }

    fun togglePlay(code: String) {
        if (playingCode == code && player.isPlaying()) {
            player.stop()
            playingCode = null
        } else {
            playingCode = code
            player.play(code, wpm) { playingCode = null }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(strings.learnTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(strings.learnSubtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // SOS kaart
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("🆘 " + strings.sosTitle, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(Modifier.height(4.dp))
                    Text(strings.sosText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("... --- ...", fontSize = 22.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                IconButton(onClick = { togglePlay("... --- ...") }) {
                    Icon(
                        if (playingCode == "... --- ..." && player.isPlaying()) Icons.Filled.Stop else Icons.Filled.PlayArrow,
                        contentDescription = strings.playButton
                    )
                }
            }
        }

        // Snelheid
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("${strings.speedLabel}: $wpm ${strings.wpmUnit}", fontWeight = FontWeight.SemiBold)
                Slider(
                    value = wpm.toFloat(),
                    onValueChange = { onWpmChange(it.toInt()) },
                    valueRange = 5f..30f,
                    steps = 24
                )
            }
        }

        // Filters
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filter == 0, onClick = { filter = 0 }, label = { Text(strings.filterAll) })
            FilterChip(selected = filter == 1, onClick = { filter = 1 }, label = { Text(strings.filterLetters) })
            FilterChip(selected = filter == 2, onClick = { filter = 2 }, label = { Text(strings.filterNumbers) })
            FilterChip(selected = filter == 3, onClick = { filter = 3 }, label = { Text(strings.filterPunct) })
        }
        Text(strings.tapToHear, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // Grid als rijen van 3 (geen geneste scroll)
        items.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { (ch, code) ->
                    val isPlaying = playingCode == code && player.isPlaying()
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { togglePlay(code) },
                        colors = if (isPlaying)
                            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                        else
                            CardDefaults.cardColors()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                ch.toString(),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                code,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                if (isPlaying) Icons.Filled.Stop else Icons.Filled.VolumeUp,
                                contentDescription = null,
                                tint = if (isPlaying) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                // Lege plekken opvullen zodat rijen uitlijnen
                repeat(3 - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }

        // Flashcards
        FlashcardSection(strings, player, wpm, isDutch)

        Spacer(Modifier.height(80.dp))
    }
}

@Composable
private fun FlashcardSection(strings: AppStrings, player: MorsePlayer, wpm: Int, isDutch: Boolean) {
    var deck by remember { mutableStateOf(MorseCode.allLearnable.shuffled()) }
    var index by remember { mutableStateOf(0) }
    var revealed by remember { mutableStateOf(false) }
    val current = deck.getOrNull(index % deck.size) ?: ('E' to ".")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("🎴 " + strings.flashcardTitle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(strings.tapCardToFlip, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { revealed = !revealed },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!revealed) {
                        Text(current.first.toString(), fontSize = 64.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                        Text("?", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        Text(current.first.toString(), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Text(current.second, fontSize = 34.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp, color = MaterialTheme.colorScheme.primary)
                        val tip = MorseCode.mnemonic(current.first, isDutch)
                        if (tip.isNotEmpty()) {
                            Text(tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = {
                    deck = deck.shuffled()
                    index = 0
                    revealed = false
                }) {
                    Icon(Icons.Filled.Refresh, contentDescription = null)
                    Text(" " + strings.flashcardShuffle)
                }
                OutlinedButton(onClick = { player.play(current.second, wpm) }) {
                    Icon(Icons.Filled.VolumeUp, contentDescription = null)
                    Text(" " + strings.playButton)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { revealed = !revealed }) {
                    Text(if (revealed) strings.flashcardHide else strings.flashcardShow)
                }
                TextButton(onClick = {
                    index = (index + 1) % deck.size
                    revealed = false
                }) {
                    Text(strings.flashcardNext + " →")
                }
            }
            Text("${(index % deck.size) + 1} / ${deck.size}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
