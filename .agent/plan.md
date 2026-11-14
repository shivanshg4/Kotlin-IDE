# Project Plan

KtLab - Kotlin Offline IDE Android application with custom Kotlin subset interpreter, Room DB, Jetpack Compose Material 3, line numbers, syntax highlighting, symbol bar, examples, tutorials, saved programs, settings, and about screen.

## Project Brief

App Name: KtLab - Kotlin Offline IDE
Package: com.shivansh.ktlab
Min SDK: 26, targetSdk/compileSdk: 35
Architecture: MVVM, ViewModels, Room DB for saved programs, Jetpack Compose, Material 3, Classic Navigation Compose (androidx.navigation:navigation-compose).
Permissions: No INTERNET permission (100% offline).
Theme & Styling: Dark theme with background #0F1117, surface #0A0C10, primary #2EA66B, secondary #F0A030, monospace font for code. Full Edge-to-Edge display, adaptive launcher icon matching core function.
Build Config: Enable R8 minification (`isMinifyEnabled = true`) and resource shrinking (`isShrinkResources = true`) for release builds in `app/build.gradle.kts`. Ensure `./gradlew assembleDebug` passes.

Key Requirements & Architecture:
1. Interpreter Engine (`com.shivansh.ktlab.interp`):
   - Custom Kotlin-subset interpreter with Lexer, Parser to AST, and tree-walking Evaluator.
   - Evaluator supports variables (`val`, `var`), basic types (`Int`, `Double`, `String`, `Boolean`), binary/unary operations, `println`/`print`, `if`/`else`, `while`, basic `for` loops, functions, string templates, array/list basics.
   - Captures output text, return values, runtime errors, and calculates execution time in milliseconds.
2. Data Layer:
   - Room Database (`KtLabDatabase`) with `SavedProgram` entity (id, title, code, timestamp) and `SavedProgramDao`.
   - Repository pattern for saved programs and bundled static examples/tutorials (15 examples, 15 tutorial lessons).
3. UI Screens & Features:
   - Splash Screen: 1-second splash with app logo transitioning to Editor.
   - Main Screen with Bottom Navigation: 4 tabs: Editor (default), Examples, Tutorial, Saved. Back button from non-Editor tabs returns to Editor; back from Editor exits.
   - Editor Screen: Top bar menu (Settings, About), Action buttons (Run, Save). Code editor with line numbers, syntax highlighting (keywords, strings, numbers, comments), symbol quick-row (`{`, `}`, `(`, `)`, `[`, `]`, `"`, `=`, `;`, `Tab`), and output panel showing execution output & time in ms.
   - Save Dialog: Name program and save to Room.
   - Examples Screen: 15 curated Kotlin examples with "Open in Editor" action.
   - Tutorial Screen: 15 Kotlin lessons with explanations and runnable "Try It" snippets.
   - Saved Screen: Saved programs list from Room DB, with open and delete options.
   - Settings Screen: Font size options and theme settings.
   - About Screen: Kotlin subset documentation & JetBrains trademark notice.

## Implementation Steps

### Task_1_CoreInterpreterAndDataLayer: Implement custom Kotlin-subset interpreter engine (Lexer, Parser, AST, Evaluator supporting val/var, Int/Double/String/Boolean, operators, print/println, if/else, while, basic loops, functions, array/list basics, error handling, timing) and Data Layer (Room database for saved programs, Dao, Repository, bundled 15 static examples and 15 tutorial lessons, DataStore for settings).
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - Interpreter correctly parses and evaluates Kotlin subset code and captures output and execution time
  - Room database and DAO created for storing saved programs
  - Static repository provides 15 Kotlin examples and 15 tutorial lessons
- **StartTime:** 2026-11-01 20:12:17 IST

### Task_2_EditorUIAndTheme: Implement Material 3 dark theme (#0F1117 background, #0A0C10 surface, #2EA66B primary, #F0A030 secondary, monospace code font), full Edge-to-Edge display, and the Editor screen with line numbers, syntax highlighting (keywords, strings, numbers, comments), symbol quick-row, output panel, and Save program dialog.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Material 3 dark theme and styling implemented with monospace code editor
  - Code editor supports line numbers, syntax highlighting, and symbol quick-row
  - Output panel displays output text and execution duration
  - Save dialog enables saving programs to Room DB
  - The implemented UI must match the design provided in input_images/image_0.png

### Task_3_NavigationScreensAndAppIcon: Implement full app navigation flow (Splash screen, 4-tab bottom navigation for Editor, Examples, Tutorial, Saved, back stack handling), Examples screen (15 items with Open in Editor action), Tutorial screen (15 lessons with Try It action), Saved programs screen (list with open & delete), Settings screen (font size, theme), About screen, and adaptive launcher icon.
- **Status:** PENDING
- **Acceptance Criteria:**
  - Bottom navigation bar links Editor, Examples, Tutorial, and Saved screens
  - Examples screen loads 15 examples and supports loading code into Editor
  - Tutorial screen presents 15 lessons with runnable Try It snippets
  - Saved screen lists saved programs with open and delete functionality
  - Settings and About screens implemented with trademark notice
  - Adaptive app icon created matching app function
  - The implemented UI must match the design provided in input_images/image_0.png

### Task_4_BuildR8AndRunVerify: Configure release build R8 minification and resource shrinking in app/build.gradle.kts, run unit tests and debug build, and instruct critic_agent to verify application stability, offline execution, navigation flow, and overall functionality.
- **Status:** PENDING
- **Acceptance Criteria:**
  - build pass
  - make sure all existing tests pass
  - app does not crash
  - R8 minification and resource shrinking enabled in release build
  - Offline interpreter and full navigation flow verified
  - The implemented UI must match the design provided in input_images/image_0.png

