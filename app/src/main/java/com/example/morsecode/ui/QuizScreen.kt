package com.example.morsecode.ui

import android.content.Context
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.morsecode.audio.MorsePlayer
import com.example.morsecode.data.AppStrings
import com.example.morsecode.data.MorseCode

private enum class QuizMode { TEXT_TO_MORSE, MORSE_TO_TEXT, LISTEN }

private const val TOTAL_QUESTIONS = 10
private const val PREFS = "morse_quiz"
private const val KEY_BEST = "best_score"

private data class Question(
    val answer: Pair<Char, String>,
    val options: List<Pair<Char, String>>
)

private fun makeQuestion(): Question {
    val pool = MorseCode.allLearnable
    val answer = pool.random()
    val distractors = pool.filter { it.first != answer.first }.shuffled().take(3)
    return Question(answer, (distractors + answer).shuffled())
}

@Composable
fun QuizScreen(strings: AppStrings, player: MorsePlayer, wpm: Int) {
    val context = LocalContext.current
    var mode by remember { mutableStateOf(QuizMode.TEXT_TO_MORSE) }
    var qIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var streak by remember { mutableStateOf(0) }
    var bestStreak by remember { mutableStateOf(0) }
    var question by remember { mutableStateOf(makeQuestion()) }
    var selected by remember { mutableStateOf<Int?>(null) }
    var best by remember { mutableStateOf(loadBest(context)) }
    var isNewBest by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    val finished = qIndex >= TOTAL_QUESTIONS
    val answered = selected != null

    // Nieuwe vraag bij moduswissel
    LaunchedEffect(mode) {
        player.stop()
        isPlaying = false
        qIndex = 0
        score = 0
        streak = 0
        question = makeQuestion()
        selected = null
        isNewBest = false
    }

    fun reset() {
        player.stop()
        isPlaying = false
        qIndex = 0
        score = 0
        streak = 0
        bestStreak = 0
        question = makeQuestion()
        selected = null
        isNewBest = false
    }

    fun pick(i: Int) {
        if (answered) return
        selected = i
        val correctIndex = question.options.indexOf(question.answer)
        if (i == correctIndex) {
            score++
            streak++
            if (streak > bestStreak) bestStreak = streak
        } else {
            streak = 0
        }
    }

    fun next() {
        player.stop()
        isPlaying = false
        if (qIndex + 1 >= TOTAL_QUESTIONS) {
            qIndex = TOTAL_QUESTIONS
            if (score > best) {
                best = score
                saveBest(context, score)
                isNewBest = true
            }
        } else {
            qIndex++
            question = makeQuestion()
            selected = null
            if (mode == QuizMode.LISTEN) {
                isPlaying = true
                player.play(question.answer.second, wpm) { isPlaying = false }
            }
        }
    }

    // Auto-play bij luistermodus (eerste vraag)
    LaunchedEffect(question, mode) {
        if (mode == QuizMode.LISTEN && !finished && selected == null && qIndex == 0) {
            // speel niet automatisch om verrassingen te voorkomen; gebruiker tikt op play
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(strings.quizTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(strings.quizSubtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        // Modus
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilterChip(selected = mode == QuizMode.TEXT_TO_MORSE, onClick = { mode = QuizMode.TEXT_TO_MORSE }, label = { Text(strings.modeTextToMorse, fontSize = 12.sp) })
            FilterChip(selected = mode == QuizMode.MORSE_TO_TEXT, onClick = { mode = QuizMode.MORSE_TO_TEXT }, label = { Text(strings.modeMorseToText, fontSize = 12.sp) })
            FilterChip(selected = mode == QuizMode.LISTEN, onClick = { mode = QuizMode.LISTEN }, label = { Text(strings.modeListen, fontSize = 12.sp) })
        }

        if (finished) {
            // Resultaat
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(strings.quizFinished, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("$score / $TOTAL_QUESTIONS", fontSize = 48.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("${strings.quizResult}: $score • ${strings.streakLabel}: $bestStreak", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    if (isNewBest) {
                        Text("🏆 " + strings.newBest, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    } else {
                        Text("Best: $best / $TOTAL_QUESTIONS", color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Button(onClick = { reset() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = null)
                        Text(" " + strings.restartButton)
                    }
                }
            }
        } else {
            // Voortgang + score
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("${strings.questionProgress} ${qIndex + 1}/$TOTAL_QUESTIONS", fontWeight = FontWeight.SemiBold)
                Text("⭐ ${strings.scoreLabel}: $score   🔥 $streak", fontWeight = FontWeight.SemiBold)
            }
            LinearProgressIndicator(progress = (qIndex + 1).toFloat() / TOTAL_QUESTIONS, modifier = Modifier.fillMaxWidth())

            // Vraagkaart
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (mode) {
                        QuizMode.TEXT_TO_MORSE -> {
                            Text(question.answer.first.toString(), fontSize = 72.sp, fontWeight = FontWeight.Black)
                            OutlinedButton(onClick = {
                                if (isPlaying) { player.stop(); isPlaying = false }
                                else { isPlaying = true; player.play(question.answer.second, wpm) { isPlaying = false } }
                            }) {
                                Icon(if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow, contentDescription = null)
                                Text(" " + strings.playButton)
                            }
                        }
                        QuizMode.MORSE_TO_TEXT -> {
                            Text(question.answer.second, fontSize = 44.sp, fontWeight = FontWeight.Black, letterSpacing = 3.sp, color = MaterialTheme.colorScheme.primary)
                            OutlinedButton(onClick = {
                                if (isPlaying) { player.stop(); isPlaying = false }
                                else { isPlaying = true; player.play(question.answer.second, wpm) { isPlaying = false } }
                            }) {
                                Icon(if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow, contentDescription = null)
                                Text(" " + strings.playButton)
                            }
                        }
                        QuizMode.LISTEN -> {
                            Text("🎧", fontSize = 48.sp)
                            Text(strings.listenTapToPlay, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Button(onClick = {
                                if (isPlaying) { player.stop(); isPlaying = false }
                                else { isPlaying = true; player.play(question.answer.second, wpm) { isPlaying = false } }
                            }) {
                                Icon(if (isPlaying) Icons.Filled.Stop else Icons.Filled.PlayArrow, contentDescription = null)
                                Text(" " + strings.playButton)
                            }
                        }
                    }
                }
            }

            // Opties
            val correctIndex = question.options.indexOf(question.answer)
            question.options.chunked(2).forEach { rowOpts ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowOpts.forEach { opt ->
                        val i = question.options.indexOf(opt)
                        val isCorrect = i == correctIndex
                        val isSelected = selected == i
                        val label = when (mode) {
                            QuizMode.TEXT_TO_MORSE -> opt.second
                            else -> opt.first.toString()
                        }
                        val colors = when {
                            answered && isCorrect -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            answered && isSelected -> ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            else -> ButtonDefaults.outlinedButtonColors()
                        }
                        if (answered && (isCorrect || isSelected)) {
                            Button(
                                onClick = { pick(i) },
                                modifier = Modifier.weight(1f),
                                colors = colors
                            ) {
                                Text(label, fontSize = if (mode == QuizMode.TEXT_TO_MORSE) 16.sp else 24.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            OutlinedButton(onClick = { pick(i) }, modifier = Modifier.weight(1f)) {
                                Text(label, fontSize = if (mode == QuizMode.TEXT_TO_MORSE) 16.sp else 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    if (rowOpts.size == 1) Spacer(Modifier.weight(1f))
                }
            }

            if (answered) {
                val wasCorrect = selected == correctIndex
                Text(
                    if (wasCorrect) "✅ " + strings.correct else "❌ " + strings.wrong + " (${question.answer.first} = ${question.answer.second})",
                    fontWeight = FontWeight.SemiBold,
                    color = if (wasCorrect) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                )
                Button(onClick = { next() }, modifier = Modifier.fillMaxWidth()) {
                    Text(strings.nextButton + " →")
                }
            }

            OutlinedButton(onClick = { reset() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
                Text(" " + strings.restartButton)
            }
        }

        Spacer(Modifier.height(80.dp))
    }
}

private fun loadBest(context: Context): Int {
    return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY_BEST, 0)
}

private fun saveBest(context: Context, score: Int) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY_BEST, score).apply()
}
