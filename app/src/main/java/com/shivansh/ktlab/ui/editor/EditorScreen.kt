package com.shivansh.ktlab.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shivansh.ktlab.interp.ExecutionResult
import com.shivansh.ktlab.ui.theme.*
import com.shivansh.ktlab.ui.viewmodel.MainViewModel

@Composable
fun EditorScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val lines = viewModel.codeText.split("\n")
    val lineCount = lines.size.coerceAtLeast(1)
    val lineNumbers = (1..lineCount).joinToString("\n")
    val fontSizeVal = viewModel.fontSize.collectAsState().value
    val currentFontSize = fontSizeVal.sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // File Name Bar & Action Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // File Name Badge
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.border(1.dp, DarkBorder, RoundedCornerShape(6.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Kotlin File",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = viewModel.fileName,
                        style = TextStyle(
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Save & Run Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.isSaveDialogOpen = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Save", tint = SecondaryAmber, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Save", color = TextPrimary, fontSize = 13.sp)
                }

                Button(
                    onClick = { viewModel.runCode() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = DarkBackground, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Run", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        HorizontalDivider(color = DarkBorder)

        // Code Editor Box with Line Numbers Column
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Line Numbers Column
                Text(
                    text = lineNumbers,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = currentFontSize,
                        color = TextMuted,
                        lineHeight = currentFontSize * 1.3f
                    ),
                    modifier = Modifier.padding(end = 12.dp)
                )

                // Code Input Field with Syntax Highlighting & FocusRequester
                BasicTextField(
                    value = viewModel.codeTextFieldValue,
                    onValueChange = { viewModel.codeTextFieldValue = it },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = currentFontSize,
                        color = TextPrimary,
                        lineHeight = currentFontSize * 1.3f
                    ),
                    cursorBrush = SolidColor(PrimaryGreen),
                    visualTransformation = SyntaxHighlighter(LocalSyntaxColors.current),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }
        }

        // Symbol Quick-Row Above Keyboard
        SymbolBar(onSymbolInserted = { symbol ->
            viewModel.insertSymbol(symbol)
            try {
                focusRequester.requestFocus()
            } catch (_: Exception) {}
        })

        // Output Panel showing Results, Errors in Red, and Duration Badge
        OutputPanel(result = viewModel.executionResult)
    }

    if (viewModel.isSaveDialogOpen) {
        SaveDialog(
            initialTitle = viewModel.fileName.removeSuffix(".kt"),
            onDismiss = { viewModel.isSaveDialogOpen = false },
            onSave = { title -> viewModel.saveProgram(title) }
        )
    }
}

@Composable
fun SymbolBar(onSymbolInserted: (String) -> Unit) {
    val symbols = listOf("{", "}", "(", ")", "[", "]", "\"", "=", ";", "Tab", "+", "-", "*", "/", ":", "$")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        symbols.forEach { sym ->
            Box(
                modifier = Modifier
                    .background(DarkSurfaceVariant, shape = RoundedCornerShape(6.dp))
                    .border(1.dp, DarkBorder, shape = RoundedCornerShape(6.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) {
                        onSymbolInserted(sym)
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = sym,
                    color = SecondaryAmber,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OutputPanel(result: ExecutionResult?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = 220.dp)
            .background(DarkSurfaceVariant)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Output",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            if (result != null) {
                Surface(
                    color = if (result.isSuccess) PrimaryGreen.copy(alpha = 0.2f) else ErrorRed.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (result.isSuccess) "Done in ${result.executionTimeMs} ms" else "Error in ${result.executionTimeMs} ms",
                        color = if (result.isSuccess) PrimaryGreen else ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (result == null) {
                Text("Press 'Run' to execute code...", color = TextMuted, fontSize = 13.sp)
            } else if (!result.isSuccess) {
                Text(
                    text = result.errorMessage ?: "Execution failed",
                    color = ErrorRed,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            } else {
                Text(
                    text = result.output.ifEmpty { "[No output generated]" },
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun SaveDialog(initialTitle: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var title by remember { mutableStateOf(initialTitle) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Program", color = TextPrimary) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Program Title") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(title) }) {
                Text("Save", color = PrimaryGreen)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = DarkSurface
    )
}
