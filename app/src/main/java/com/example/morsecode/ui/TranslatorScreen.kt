package com.example.morsecode.ui

import android.content.Intent
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.morsecode.audio.MorsePlayer
import com.example.morsecode.data.AppStrings
import com.example.morsecode.data.MorseCode

@Composable
fun TranslatorScreen(
    strings: AppStrings,
    player: MorsePlayer,
    wpm: Int,
    onWpmChange: (Int) -> Unit
) {
    var textToMorse by remember { mutableStateOf(true) }
    var input by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }
    var justCopied by remember { mutableStateOf(false) }
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    val output = if (textToMorse) MorseCode.encode(input) else MorseCode.decode(input)
    val morseToPlay = if (textToMorse) output else input

    val quickPhrases = if (textToMorse) {
        listOf("SOS", "HELLO", "HELP", "MORSE CODE", "GOOD MORNING")
    } else {
        listOf("... --- ...", ".... . .-.. .-.. ---", ".... . .-.. .--.")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(strings.translatorTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = textToMorse,
                onClick = { textToMorse = true; input = "" },
                label = { Text(strings.textToMorse) }
            )
            FilterChip(
                selected = !textToMorse,
                onClick = { textToMorse = false; input = "" },
                label = { Text(strings.morseToText) }
            )
        }

        OutlinedTextField(
            value = input,
            onValueChange = { input = it; justCopied = false },
            label = { Text(if (textToMorse) strings.textToMorse else strings.morseToText) },
            placeholder = { Text(if (textToMorse) strings.inputHintText else strings.inputHintMorse) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 6
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(strings.outputLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    output.ifEmpty { "—" },
                    fontSize = if (textToMorse) 20.sp else 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = if (textToMorse) 2.sp else 1.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
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

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (isPlaying) {
                        player.stop()
                        isPlaying = false
                    } else if (morseToPlay.isNotBlank()) {
                        isPlaying = true
                        player.play(morseToPlay, wpm) { isPlaying = false }
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow, contentDescription = null)
                Text(if (isPlaying) " " + strings.stopButton else " " + strings.playButton)
            }
            OutlinedButton(onClick = {
                clipboard.setText(AnnotatedString(output))
                justCopied = true
            }) {
                Icon(Icons.Filled.ContentCopy, contentDescription = null)
                Text(" " + if (justCopied) strings.copied else strings.copyButton)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = { input = ""; player.stop(); isPlaying = false }, modifier = Modifier.weight(1f)) {
                Icon(Icons.Filled.Clear, contentDescription = null)
                Text(" " + strings.clearButton)
            }
            OutlinedButton(
                onClick = {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, output)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, strings.shareButton))
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Filled.Share, contentDescription = null)
                Text(" " + strings.shareButton)
            }
        }

        Text(strings.quickPhrases, fontWeight = FontWeight.SemiBold)
        quickPhrases.forEach { phrase ->
            OutlinedButton(
                onClick = { input = phrase; justCopied = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(phrase, modifier = Modifier.weight(1f))
                Text("→")
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}
