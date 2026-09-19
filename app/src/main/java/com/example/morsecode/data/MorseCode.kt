package com.example.morsecode.data

/**
 * Complete morse code tabel + vertaal functies.
 */
object MorseCode {

    val map: Map<Char, String> = mapOf(
        'A' to ".-",
        'B' to "-...",
        'C' to "-.-.",
        'D' to "-..",
        'E' to ".",
        'F' to "..-.",
        'G' to "--.",
        'H' to "....",
        'I' to "..",
        'J' to ".---",
        'K' to "-.-",
        'L' to ".-..",
        'M' to "--",
        'N' to "-.",
        'O' to "---",
        'P' to ".--.",
        'Q' to "--.-",
        'R' to ".-.",
        'S' to "...",
        'T' to "-",
        'U' to "..-",
        'V' to "...-",
        'W' to ".--",
        'X' to "-..-",
        'Y' to "-.--",
        'Z' to "--..",
        '0' to "-----",
        '1' to ".----",
        '2' to "..---",
        '3' to "...--",
        '4' to "....-",
        '5' to ".....",
        '6' to "-....",
        '7' to "--...",
        '8' to "---..",
        '9' to "----.",
        '.' to ".-.-.-",
        ',' to "--..--",
        '?' to "..--..",
        '\'' to ".----.",
        '!' to "-.-.--",
        '/' to "-..-.",
        '(' to "-.--.",
        ')' to "-.--.-",
        '&' to ".-...",
        ':' to "---...",
        ';' to "-.-.-.",
        '=' to "-...-",
        '+' to ".-.-.",
        '-' to "-....-",
        '_' to "..--.-",
        '"' to ".-..-.",
        '@' to ".--.-."
    )

    val reverse: Map<String, Char> by lazy {
        map.entries.associate { (k, v) -> v to k }
    }

    val letters: List<Pair<Char, String>> = ('A'..'Z').map { it to (map[it] ?: "") }
    val numbers: List<Pair<Char, String>> = ('0'..'9').map { it to (map[it] ?: "") }
    val punctuation: List<Pair<Char, String>> =
        listOf('.', ',', '?', '!', '/', '=', '+', '-', '@', '&', ':', ';')
            .mapNotNull { c -> map[c]?.let { c to it } }

    val allLearnable: List<Pair<Char, String>> = letters + numbers

    /**
     * Tekst -> morse. Letters gescheiden door spatie, woorden door " / ".
     * Voorbeeld: "SOS" -> "... --- ..."
     */
    fun encode(text: String): String {
        if (text.isBlank()) return ""
        return text.uppercase().split(" ").filter { it.isNotEmpty() }.joinToString(" / ") { word ->
            word.mapNotNull { ch -> map[ch] }.joinToString(" ")
        }
    }

    /**
     * Morse -> tekst. Verwacht letters gescheiden door spatie, woorden door "/" of " / ".
     */
    fun decode(morse: String): String {
        if (morse.isBlank()) return ""
        val normalized = morse.trim()
            .replace(" / ", "|")
            .replace("/", "|")
        return normalized.split("|").joinToString(" ") { word ->
            word.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
                .map { code -> reverse[code]?.toString() ?: "�" }
                .joinToString("")
        }
    }

    /** Geheugensteuntje voor beginners. */
    fun mnemonic(char: Char, dutch: Boolean): String {
        val c = char.uppercaseChar()
        if (c == 'S' || c == 'O') {
            return if (dutch) "Onthoud SOS: ... --- ..." else "Remember SOS: ... --- ..."
        }
        if (c == 'V') {
            return if (dutch) "...- klinkt als Beethoven!" else "...- sounds like Beethoven!"
        }
        if (!dutch) return ""
        return when (c) {
            'A' -> "Denk: Arie"
            'B' -> "Denk: Banaan"
            'C' -> "Denk: Cola"
            'E' -> "Kortste teken: ."
            'T' -> "Kortste streep: -"
            'H' -> "4 puntjes: ...."
            else -> ""
        }
    }
}
