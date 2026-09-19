# 📻 Morse Leren — Learn Morse Code

**Leer morsecode met geluid, flashcards, quiz en vertaler.**
**Learn Morse code with sound, flashcards, quiz & translator.**

Tweetalige Android-app (NL/EN) met automatische APK-build via GitHub Actions.
Bilingual Android app (NL/EN) with automatic APK builds via GitHub Actions.

---

## ✨ Functies / Features

| Tab | NL | EN |
|-----|----|----|
| 📖 **Leren** | Alfabet + cijfers + leestekens, tik om te horen, flashcards, SOS-trainer, snelheid (WPM) instelbaar | Alphabet + numbers + punctuation, tap to hear, flashcards, SOS trainer, adjustable speed (WPM) |
| 🔁 **Vertaler** | Tekst ⇄ Morse, afspelen met echt geluid (700 Hz), kopiëren, delen, snelle zinnen | Text ⇄ Morse, real sound playback (700 Hz), copy, share, quick phrases |
| 🎯 **Quiz** | 3 modi: Tekst→Morse, Morse→Tekst, Luisteren. 10 vragen per ronde, score + reeks + beste score | 3 modes: Text→Morse, Morse→Text, Listening. 10 questions per round, score + streak + best score |

- 🌐 **NL/EN taalkeuze** rechtsboven in de app — language toggle top-right
- 🔊 **Echt morsegeluid** (punt = kort, streep = lang, correcte pauzes)
- 📴 **100% offline**, geen account nodig — works fully offline

---

## 🚀 In 3 stappen naar je APK (nieuwe repo)

### Stap 1 — Maak een nieuwe repository op GitHub
1. Ga naar [github.com/new](https://github.com/new)
2. **Repository name:** `morse-code-app` (of een eigen naam)
3. Kies **Public** of **Private** → klik **Create repository**
4. ⚠️ **NIET** aanvinken: *Add a README* / *.gitignore* / *license* (die zitten al in dit project)

### Stap 2 — Push deze code naar GitHub

Download/kopieer deze projectmap naar je computer en voer uit in een terminal:

```bash
cd morse-code-app
git init
git add .
git commit -m "📻 Morse Leren app"
git branch -M main
git remote add origin https://github.com/JOUWNAAM/morse-code-app.git
git push -u origin main
```

> Vervang `JOUWNAAM` door je GitHub-gebruikersnaam.
> Met GitHub CLI kan het ook zo:
> ```bash
> cd morse-code-app
> git init && git add . && git commit -m "📻 Morse Leren app"
> gh repo create morse-code-app --public --source=. --push
> ```

### Stap 3 — Download je APK uit GitHub Actions 🤖
1. Ga naar je repo → tabblad **Actions**
2. Klik op de run **"📱 Bouw APK (Build APK)"** (duurt ± 3–6 min bij eerste build)
3. Onderaan bij **Artifacts** → download **morse-leren-apk**
4. Pak het zipje uit → je hebt **`app-debug.apk`** 🎉

### Stap 4 — Installeer op je telefoon
1. Stuur de APK naar je telefoon (e-mail, Drive, USB, WhatsApp naar jezelf…)
2. Tik op het bestand op je telefoon
3. Sta **"Installeren uit onbekende bronnen"** toe als Android dat vraagt
4. Tik **Installeren** → open **Morse Leren** 📻

### 🏷️ Tip: een Release maken (vaste downloadlink)
```bash
git tag v1.0
git push origin v1.0
```
→ GitHub maakt automatisch een **Release** met de APK als download. Handig om te delen!

---

## 🛠️ Lokaal bouwen (optioneel)

- Open de map in **Android Studio** (Ladybug of nieuwer)
- Wacht tot Gradle klaar is → klik **Run ▶** of **Build → Build APK(s)**
- Vereisten: JDK 17, Android SDK 34, Kotlin 1.9.20

---

## 📁 Projectstructuur

```
morse-code-app/
├── .github/workflows/build-apk.yml   # 🤖 Automatische APK-build
├── app/
│   ├── build.gradle                  # App dependencies (Compose Material3)
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/morsecode/
│       │   ├── MainActivity.kt       # Navigatie + taalkeuze (NL/EN)
│       │   ├── data/MorseCode.kt     # Volledige morsetabel + vertaler
│       │   ├── data/AppText.kt       # Alle NL/EN teksten
│       │   ├── audio/MorsePlayer.kt  # Piepjes-generator (700 Hz)
│       │   └── ui/
│       │       ├── Theme.kt          # Donker thema (navy + amber)
│       │       ├── LearnScreen.kt    # Alfabet + flashcards + SOS
│       │       ├── TranslatorScreen.kt
│       │       └── QuizScreen.kt     # 3 quizmodi + highscore
│       └── res/                      # Icoon, kleuren, thema
├── build.gradle / settings.gradle
└── README.md
```

---

## 📡 Morse-alfabet (spiekbriefje / cheat sheet)

```
A .-    B -...  C -.-.  D -..   E .     F ..-.
G --.   H ....  I ..    J .---  K -.-   L .-..
M --    N -.    O ---   P .--.  Q --.-  R .-.
S ...   T -     U ..-   V ...-  W .--   X -..-
Y -.--  Z --..
1 .---- 2 ..--- 3 ...-- 4 ....- 5 .....
6 -.... 7 --... 8 ---.. 9 ----. 0 -----
SOS = ... --- ...
```

**Regels:** punt = 1 tik, streep = 3 tikken, pauze tussen letters = 3 tikken, tussen woorden = 7 tikken.

---

## 📄 Licentie / License

MIT — vrij te gebruiken en aan te passen. / Free to use and modify.
