package com.shivansh.ktlab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shivansh.ktlab.data.repository.TutorialsRepository
import com.shivansh.ktlab.ui.theme.*
import com.shivansh.ktlab.ui.viewmodel.MainViewModel

@Composable
fun TutorialScreen(viewModel: MainViewModel) {
    val tutorials = TutorialsRepository.getTutorials()
    var currentLessonIndex by remember { mutableIntStateOf(0) }
    val currentLesson = tutorials[currentLessonIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(12.dp)
    ) {
        Text("Kotlin Tutorials", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        // Navigation Stepper Bar (Back / Next)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurface, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (currentLessonIndex > 0) currentLessonIndex-- },
                enabled = currentLessonIndex > 0,
                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Back", color = TextPrimary, fontSize = 12.sp)
            }

            Text(
                text = "Lesson ${currentLessonIndex + 1} of ${tutorials.size}",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { if (currentLessonIndex < tutorials.size - 1) currentLessonIndex++ },
                enabled = currentLessonIndex < tutorials.size - 1,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Text("Next", color = DarkBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", tint = DarkBackground, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.height(10.dp))

        // Lesson Card Content
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = currentLesson.title,
                    color = PrimaryGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))

                Text(
                    text = currentLesson.summary,
                    color = SecondaryAmber,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(10.dp))

                Text(
                    text = currentLesson.content,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(16.dp))

                Text("Code Snippet:", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))

                // Snippet Display
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface, shape = RoundedCornerShape(6.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = currentLesson.codeSnippet,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.loadCodeIntoEditor(currentLesson.codeSnippet, "${currentLesson.id}.kt") },
                    colors = ButtonDefaults.buttonColors(containerColor = SecondaryAmber),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Try it in Editor", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
