package com.shivansh.ktlab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shivansh.ktlab.data.SavedProgram
import com.shivansh.ktlab.ui.theme.*
import com.shivansh.ktlab.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(viewModel: MainViewModel) {
    val savedPrograms = viewModel.savedPrograms.collectAsState().value
    var programToRename by remember { mutableStateOf<SavedProgram?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(12.dp)
    ) {
        Text("Saved Programs", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        if (savedPrograms.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No saved programs yet.", color = TextMuted, fontSize = 14.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(savedPrograms, key = { it.id }) { prog ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { dismissValue ->
                            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                                viewModel.deleteProgram(prog)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(ErrorRed)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextPrimary)
                            }
                        },
                        content = {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(prog.title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        val formattedDate = remember(prog.timestamp) {
                                            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(prog.timestamp))
                                        }
                                        Text(formattedDate, color = TextMuted, fontSize = 11.sp)
                                    }

                                    Spacer(Modifier.height(4.dp))
                                    Text(prog.code.take(60) + if (prog.code.length > 60) "..." else "", color = TextSecondary, fontSize = 12.sp)

                                    Spacer(Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                val name = if (prog.title.endsWith(".kt")) prog.title else "${prog.title}.kt"
                                                viewModel.loadCodeIntoEditor(prog.code, name)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                                        ) {
                                            Text("Open", color = DarkBackground)
                                        }

                                        Row {
                                            IconButton(onClick = { programToRename = prog }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Rename", tint = SecondaryAmber)
                                            }
                                            IconButton(onClick = { viewModel.deleteProgram(prog) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (programToRename != null) {
        RenameDialog(
            currentTitle = programToRename!!.title,
            onDismiss = { programToRename = null },
            onRename = { newTitle ->
                viewModel.updateProgram(programToRename!!, newTitle)
                programToRename = null
            }
        )
    }
}

@Composable
fun RenameDialog(currentTitle: String, onDismiss: () -> Unit, onRename: (String) -> Unit) {
    var newTitle by remember { mutableStateOf(currentTitle) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Program", color = TextPrimary) },
        text = {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                label = { Text("New Title") },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onRename(newTitle) }) {
                Text("Rename", color = PrimaryGreen)
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
