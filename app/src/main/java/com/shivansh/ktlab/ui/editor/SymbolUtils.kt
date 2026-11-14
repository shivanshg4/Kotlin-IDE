package com.shivansh.ktlab.ui.editor

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun insertSymbolInTextFieldValue(current: TextFieldValue, symbol: String): TextFieldValue {
    val text = current.text
    val sel = current.selection
    val start = sel.min.coerceIn(0, text.length)
    val end = sel.max.coerceIn(0, text.length)
    val hasSelection = start < end

    return when (symbol) {
        "Tab" -> {
            val indent = "    "
            val newText = text.substring(0, start) + indent + text.substring(end)
            TextFieldValue(text = newText, selection = TextRange(start + indent.length))
        }
        "{", "(", "[", "\"" -> {
            val closing = when (symbol) {
                "{" -> "}"
                "(" -> ")"
                "[" -> "]"
                "\"" -> "\""
                else -> ""
            }

            if (hasSelection) {
                val selectedText = text.substring(start, end)
                val newText = text.substring(0, start) + symbol + selectedText + closing + text.substring(end)
                TextFieldValue(text = newText, selection = TextRange(end + 2))
            } else {
                // If symbol is '"' and next character is already '"', overtype/skip
                if (symbol == "\"" && start < text.length && text[start] == '"') {
                    TextFieldValue(text = text, selection = TextRange(start + 1))
                } else {
                    val newText = text.substring(0, start) + symbol + closing + text.substring(start)
                    TextFieldValue(text = newText, selection = TextRange(start + 1))
                }
            }
        }
        "}", ")", "]" -> {
            if (!hasSelection && start < text.length && text[start].toString() == symbol) {
                // Overtype / skip existing closing bracket
                TextFieldValue(text = text, selection = TextRange(start + 1))
            } else {
                val newText = text.substring(0, start) + symbol + text.substring(end)
                TextFieldValue(text = newText, selection = TextRange(start + symbol.length))
            }
        }
        else -> {
            val newText = text.substring(0, start) + symbol + text.substring(end)
            TextFieldValue(text = newText, selection = TextRange(start + symbol.length))
        }
    }
}
