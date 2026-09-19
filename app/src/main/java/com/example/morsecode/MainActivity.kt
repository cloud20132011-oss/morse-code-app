package com.example.morsecode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.morsecode.audio.MorsePlayer
import com.example.morsecode.data.Language
import com.example.morsecode.data.stringsFor
import com.example.morsecode.ui.LearnScreen
import com.example.morsecode.ui.MorseTheme
import com.example.morsecode.ui.QuizScreen
import com.example.morsecode.ui.TranslatorScreen

class MainActivity : ComponentActivity() {

    private val player = MorsePlayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MorseTheme {
                MorseApp(player)
            }
        }
    }

    override fun onDestroy() {
        player.stop()
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MorseApp(player: MorsePlayer) {
    var tab by remember { mutableStateOf(0) }
    var lang by remember { mutableStateOf(Language.NL) }
    var wpm by remember { mutableStateOf(18) }
    val strings = stringsFor(lang)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("📻 " + strings.appTitle, fontWeight = FontWeight.Bold)
                },
                actions = {
                    TextButton(onClick = { lang = Language.NL }) {
                        Text(
                            "NL",
                            fontWeight = if (lang == Language.NL) FontWeight.Black else FontWeight.Normal,
                            color = if (lang == Language.NL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text("|", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    TextButton(onClick = { lang = Language.EN }) {
                        Text(
                            "EN",
                            fontWeight = if (lang == Language.EN) FontWeight.Black else FontWeight.Normal,
                            color = if (lang == Language.EN) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { player.stop(); tab = 0 },
                    icon = { Icon(Icons.Filled.MenuBook, contentDescription = null) },
                    label = { Text(strings.tabLearn) }
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { player.stop(); tab = 1 },
                    icon = { Icon(Icons.Filled.Translate, contentDescription = null) },
                    label = { Text(strings.tabTranslator) }
                )
                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { player.stop(); tab = 2 },
                    icon = { Icon(Icons.Filled.Quiz, contentDescription = null) },
                    label = { Text(strings.tabQuiz) }
                )
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            when (tab) {
                0 -> LearnScreen(strings, player, wpm, { wpm = it }, lang == Language.NL)
                1 -> TranslatorScreen(strings, player, wpm, { wpm = it })
                2 -> QuizScreen(strings, player, wpm)
            }
        }
    }
}
