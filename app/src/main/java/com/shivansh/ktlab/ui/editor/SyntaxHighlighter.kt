package com.shivansh.ktlab.ui.editor

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import com.shivansh.ktlab.ui.theme.SyntaxColors

class SyntaxHighlighter(private val syntaxColors: SyntaxColors) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = highlightCode(text.text),
            offsetMapping = OffsetMapping.Identity
        )
    }

    private fun highlightCode(code: String): AnnotatedString {
        return buildAnnotatedString {
            append(code)

            // 1. Highlight Keywords
            val keywordPattern = Regex("\\b(val|var|fun|if|else|while|for|in|return|true|false|null|break|continue|when|until|step)\\b")
            for (match in keywordPattern.findAll(code)) {
                addStyle(
                    style = SpanStyle(color = syntaxColors.keyword, fontWeight = FontWeight.Bold),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }

            // 2. Highlight Function Names
            val funDeclPattern = Regex("fun\\s+([a-zA-Z_][a-zA-Z0-9_]*)")
            for (match in funDeclPattern.findAll(code)) {
                val nameGroup = match.groups[1]
                if (nameGroup != null) {
                    addStyle(
                        style = SpanStyle(color = syntaxColors.function, fontWeight = FontWeight.Bold),
                        start = nameGroup.range.first,
                        end = nameGroup.range.last + 1
                    )
                }
            }

            val funCallPattern = Regex("\\b([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(")
            for (match in funCallPattern.findAll(code)) {
                val nameGroup = match.groups[1]
                if (nameGroup != null) {
                    val name = nameGroup.value
                    if (!listOf("if", "while", "for", "when").contains(name)) {
                        addStyle(
                            style = SpanStyle(color = syntaxColors.function),
                            start = nameGroup.range.first,
                            end = nameGroup.range.last + 1
                        )
                    }
                }
            }

            // 3. Highlight Numbers
            val numberPattern = Regex("\\b\\d+(\\.\\d+)?[L|l]?\\b")
            for (match in numberPattern.findAll(code)) {
                addStyle(
                    style = SpanStyle(color = syntaxColors.number),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }

            // 4. Highlight Strings
            val stringPattern = Regex("\"([^\"\\\\]|\\\\.)*\"")
            for (match in stringPattern.findAll(code)) {
                addStyle(
                    style = SpanStyle(color = syntaxColors.string),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }

            // 5. Highlight Comments
            val commentPattern = Regex("//.*|/\\*[\\s\\S]*?\\*/")
            for (match in commentPattern.findAll(code)) {
                addStyle(
                    style = SpanStyle(color = syntaxColors.comment),
                    start = match.range.first,
                    end = match.range.last + 1
                )
            }
        }
    }
}
