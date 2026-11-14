package com.shivansh.ktlab.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shivansh.ktlab.data.KtLabDatabase
import com.shivansh.ktlab.data.SavedProgram
import com.shivansh.ktlab.data.repository.SettingsRepository
import com.shivansh.ktlab.interp.ExecutionResult
import com.shivansh.ktlab.interp.KotlinInterpreter
import com.shivansh.ktlab.ui.editor.insertSymbolInTextFieldValue
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = KtLabDatabase.getDatabase(application)
    private val savedProgramDao = db.savedProgramDao()
    private val settingsRepository = SettingsRepository(application)
    private val interpreter = KotlinInterpreter()

    var selectedTab by mutableIntStateOf(0) // 0: Editor, 1: Examples, 2: Tutorial, 3: Saved
    var fileName by mutableStateOf("Main.kt")

    var codeTextFieldValue by mutableStateOf(
        TextFieldValue(
            text = """
            fun main() {
                println("Hello, KtLab!")
                val numbers = listOf(1, 2, 3, 4, 5)
                for (n in numbers) {
                    println("Number: ${'$'}n")
                }
            }
            """.trimIndent(),
            selection = TextRange(0)
        )
    )

    var codeText: String
        get() = codeTextFieldValue.text
        set(value) {
            codeTextFieldValue = TextFieldValue(text = value, selection = TextRange(value.length))
        }

    var executionResult by mutableStateOf<ExecutionResult?>(null)
    var isSaveDialogOpen by mutableStateOf(false)
    var showSettingsScreen by mutableStateOf(false)
    var showAboutScreen by mutableStateOf(false)

    val savedPrograms: StateFlow<List<SavedProgram>> = savedProgramDao.getAllPrograms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val fontSize: StateFlow<Float> = settingsRepository.fontSizeFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 14f)

    val isDarkTheme: StateFlow<Boolean> = settingsRepository.themeModeFlow.map { it == "DARK" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun runCode() {
        viewModelScope.launch {
            executionResult = interpreter.execute(codeText)
        }
    }

    fun insertSymbol(symbol: String) {
        codeTextFieldValue = insertSymbolInTextFieldValue(codeTextFieldValue, symbol)
    }

    fun saveProgram(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            savedProgramDao.insertProgram(SavedProgram(title = title, code = codeText))
            fileName = if (title.endsWith(".kt")) title else "$title.kt"
            isSaveDialogOpen = false
        }
    }

    fun updateProgram(program: SavedProgram, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch {
            savedProgramDao.updateProgram(program.copy(title = newTitle, timestamp = System.currentTimeMillis()))
        }
    }

    fun deleteProgram(program: SavedProgram) {
        viewModelScope.launch {
            savedProgramDao.deleteProgram(program)
        }
    }

    fun loadCodeIntoEditor(code: String, name: String = "Main.kt") {
        codeText = code
        fileName = name
        executionResult = null
        selectedTab = 0 // Jump back to Editor tab
    }

    fun updateFontSize(size: Float) {
        viewModelScope.launch {
            settingsRepository.updateFontSize(size)
        }
    }

    fun updateDarkTheme(isDark: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateThemeMode(if (isDark) "DARK" else "LIGHT")
        }
    }
}
