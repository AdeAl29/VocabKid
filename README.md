# VocabKid 🌟

**VocabKid** is an interactive Android application designed to help elementary school students learn and retain English vocabulary. It combines the **Spaced Repetition (SM-2)** algorithm with gamification, illustrated interactive stories, speech-recognition pronunciation practice, conversation simulations, arcade mini-games, and a child-safe AI assistant.

---

## ✨ Key Features

### 👦 Student Mode
- **📚 Study (Spaced Repetition Flashcards):** Scheduled vocabulary review with interactive flip cards, Text-to-Speech (TTS) audio pronunciation, and self-grading buttons (*Again, Hard, Good, Easy*).
- **❓ Multi-Level Vocabulary Quiz:** Category-based multiple-choice quizzes featuring **Level 1 & Level 2** progression, animated visual feedback, and sound effects.
- **📖 Adventure Stories (10 Chapters / 50 Illustrated Scenes):** Illustrated English storybooks (*The Lost Kitten, The River Crossing, The Secret Garden, Space Explorer, Ocean Depths, Dragon Mountain, The Time Travel Market, My Robot Friend, Jungle Expedition,* and *The Magic Library*) with embedded comprehension quizzes and vocabulary galleries.
- **🗣️ Pronunciation Practice:** Interactive speaking practice powered by Android's `SpeechRecognizer` and `TextToSpeech` to help students master accurate English pronunciation.
- **💬 Conversation Practice:** Role-play everyday English dialogues across various scenarios with branching choices and progress tracking.
- **🕹️ Arcade (Word Playground – Memory Flip):** Card-matching vocabulary game across 3 difficulty levels (*Easy, Medium, Hard*) with move and timer star ratings.
- **🤖 VocabKid AI Assistant:** Smart learning tutor powered by **Google Gemini AI** with strict child-safe content filtering and automatic model fallback.
- **📊 Learning Progress & Analytics:** Visual statistics tracking mastered words, words due today, overall accuracy, review counts, and a dedicated *Difficult Words* review list.
- **🔔 Daily Study Reminder (WorkManager):** Automated, battery-friendly daily notifications reminding students when vocabulary cards are due for review.

### 👩‍🏫 Teacher Mode (Teacher Dashboard)
- **Learning Material Management:** Add, edit, delete, and search vocabulary entries along with categories, difficulty levels, and example sentences.
- **Student Roster by Grade:** View and organize student records across grades (Grades 4–6) with demographic summaries.

---

## 🧠 Spaced Repetition Algorithm (SM-2)

VocabKid schedules vocabulary reviews adaptively using a simplified **SM-2** algorithm implemented in [`SpacedRepetitionAlgorithm.kt`](app/src/main/java/com/example/vocabkid/domain/algorithm/SpacedRepetitionAlgorithm.kt):

### Progress Parameters
- `repetition`: Number of consecutive successful recalls.
- `intervalDays`: Number of days until the next scheduled review.
- `easeFactor`: Difficulty multiplier for the word (starts at `2.5`, minimum floor `1.3`).
- `dueDate`: Timestamp of the next scheduled review date.
- `lastReviewedDate`: Timestamp of the most recent review session.
- `correctCount` & `wrongCount`: Cumulative correct and incorrect review counts.
- `status`: Mastery state (`Baru` / New, `Dipelajari` / Learning, `Dikuasai` / Mastered, or `Sering Salah` / Frequently Wrong).

### Quality Score Mapping (`quality`)
| Mode | Student Response | `quality` Score |
| :--- | :--- | :---: |
| **Flashcard** | Again (*Ulangi*) | `0` |
| **Flashcard** | Hard (*Sulit*) | `3` |
| **Flashcard** | Good (*Cukup*) | `4` |
| **Flashcard** | Easy (*Mudah*) | `5` |
| **Quiz** | Correct Answer | `4` |
| **Quiz** | Wrong Answer | `0` |

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin (JVM Target 17)
- **UI Framework:** Jetpack Compose & Material 3 (supports both *Light & Dark Theme*)
- **Architecture:** MVVM (*Model-View-ViewModel*) + Repository Pattern
- **Local Database:** Room Database (Schema v10 with full migration path `MIGRATION_1_2` through `MIGRATION_9_10`, compiled with KSP)
- **Concurrency & Reactivity:** Kotlin Coroutines & StateFlow / Flow
- **Background Processing:** AndroidX WorkManager (`DailyStudyReminderWorker`)
- **Authentication:** Firebase Authentication (Email/Password) & Quick Local Username Session
- **AI Integration:** Google Generative AI SDK (`gemini-2.0-flash-lite` with automatic fallback to `gemini-1.5-flash`)
- **Testing:** JUnit 4, Kotlinx Coroutines Test, & Room `MigrationTestHelper`

---

## 📂 Project Structure

```text
com.example.vocabkid
├── audio                  # Sound effects (SoundPool) & background music player
├── data
│   ├── local
│   │   ├── dao            # Room Data Access Objects (DAO)
│   │   ├── database       # VocabKidDatabase (v10) & InitialVocabulary seed data
│   │   ├── entity         # SQLite/Room table entities
│   │   └── model          # Local query aggregation models
│   ├── remote             # GeminiChatService (AI Assistant wrapper)
│   └── repository         # VocabKidRepository (Single Source of Truth)
├── domain
│   ├── algorithm          # SpacedRepetitionAlgorithm (SM-2)
│   └── model              # Domain models & DateUtils
├── notification           # DailyStudyReminderWorker & StudyReminderScheduler
├── presentation
│   ├── arcade             # Memory Flip mini-game
│   ├── auth               # Student & Teacher authentication screens
│   ├── chatassistant      # Floating AI Chat Assistant overlay
│   ├── components         # Shared UI components, TTS helpers, & RobotCoach
│   ├── conversation       # Interactive dialogue practice
│   ├── home               # Student dashboard & daily summary
│   ├── navigation         # Navigation graph & GlassBottomNavigationBar
│   ├── onboarding         # Initial student profile setup
│   ├── profile            # Profile editor, avatar picker, & reminder settings
│   ├── progress           # Learning statistics & visual charts
│   ├── pronunciation      # Speech recognition pronunciation trainer
│   ├── quiz               # Multi-level multiple-choice quiz
│   ├── splash             # Animated splash screen
│   ├── story              # 10 interactive story chapters & 50 scene illustrations
│   ├── study              # SM-2 flashcard study session
│   ├── teacher            # Teacher Dashboard, vocabulary manager, & student roster
│   └── vocabulary         # Vocabulary list, search, & detail view
└── ui.theme               # Color palettes, typography, & app theming
```

---

## 🚀 Getting Started

1. **Clone the repository:**
   ```bash
   git clone https://github.com/AdeAl29/VocabKid.git
   ```
2. **Open in Android Studio** (Ladybug or newer supporting AGP `8.6.1` and Compile SDK `35`).
3. *(Optional)* To enable the **VocabKid AI Chat Assistant**, add your Gemini API key to `local.properties` in the project root:
   ```properties
   GEMINI_API_KEY=your_gemini_api_key_here
   ```
4. Wait for **Gradle Sync** to finish, then run the `app` configuration on an Android emulator or physical device (**Android 7.0 / API 24** or higher).
5. **Run unit tests:**
   ```bash
   ./gradlew testDebugUnitTest
   ```
