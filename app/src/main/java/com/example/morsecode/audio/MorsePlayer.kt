package com.example.morsecode.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sin

/**
 * Speelt morsecode af als piepjes (700 Hz sinus).
 * Timing volgens standaard: punt = 1 unit, streep = 3 units.
 * Letter-pauze = 3 units, woord-pauze = 7 units.
 * unit (ms) = 1200 / WPM
 */
class MorsePlayer {

    private var job: Job? = null
    @Volatile private var playing: Boolean = false

    fun isPlaying(): Boolean = playing

    fun play(morse: String, wpm: Int = 18, onDone: () -> Unit = {}) {
        stop()
        if (morse.isBlank()) {
            onDone()
            return
        }
        val safeWpm = wpm.coerceIn(5, 40)
        val unit = 1200 / safeWpm // ms per unit
        // Normaliseer: " / " (woordscheiding) -> "|", spatie (letter) -> "*"
        val normalized = morse.trim()
            .replace(" / ", "|")
            .replace("/", "|")

        job = CoroutineScope(Dispatchers.IO).launch {
            playing = true
            try {
                for (ch in normalized) {
                    if (!playing) break
                    when (ch) {
                        '.' -> {
                            tone(unit, FREQ)
                            delay(unit.toLong())
                        }
                        '-' -> {
                            tone(unit * 3, FREQ)
                            delay(unit.toLong())
                        }
                        ' ', '*' -> {
                            // letter-pauze: totaal 3 units, 1 al gehad na vorige toon
                            delay((unit * 2).toLong())
                        }
                        '|' -> {
                            // woord-pauze: totaal 7 units, 1 al gehad
                            delay((unit * 6).toLong())
                        }
                        else -> { /* negeer */ }
                    }
                }
            } finally {
                playing = false
                withContext(Dispatchers.Main) { onDone() }
            }
        }
    }

    fun stop() {
        playing = false
        job?.cancel()
        job = null
    }

    private suspend fun tone(durationMs: Int, freq: Int) {
        val sampleRate = 22050
        val numSamples = (durationMs * sampleRate / 1000).coerceAtLeast(1)
        val buffer = ShortArray(numSamples)
        val fade = (0.005 * sampleRate).toInt().coerceAtLeast(1)
        for (i in buffer.indices) {
            val angle = 2.0 * Math.PI * i * freq / sampleRate
            val env = when {
                i < fade -> i.toFloat() / fade
                i > numSamples - fade -> (numSamples - i).toFloat() / fade
                else -> 1f
            }
            buffer[i] = (32767 * 0.5 * sin(angle) * env).toInt().toShort()
        }
        try {
            val track = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                buffer.size * 2,
                AudioTrack.MODE_STATIC
            )
            track.write(buffer, 0, buffer.size)
            track.play()
            delay(durationMs.toLong())
            track.stop()
            track.release()
        } catch (_: Exception) {
            delay(durationMs.toLong())
        }
    }

    companion object {
        private const val FREQ = 700
    }
}
