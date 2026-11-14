package com.shivansh.ktlab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shivansh.ktlab.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About KtLab", color = TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("KtLab - Kotlin Offline IDE", color = PrimaryGreen, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Version 1.0 (100% Offline)", color = TextSecondary, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "KtLab is a lightweight offline Kotlin programming environment designed for learning, experimenting, and executing Kotlin code directly on Android devices.",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Supported Kotlin Subset", color = SecondaryAmber, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    val features = listOf(
                        "• Variable Declarations: val (immutable) & var (mutable)",
                        "• Primitive Types: Int, Double, String, Boolean",
                        "• Control Flow: if/else, while, for ranges (e.g. 1..10)",
                        "• Functions: Custom functions with parameters and return values",
                        "• Strings & Templates: String interpolation \${expr}",
                        "• Collections: listOf, mutableListOf, index access [i]",
                        "• Math Helpers: max(), min(), sqrt(), abs()",
                        "• Built-in Output: println(), print()"
                    )
                    features.forEach { f ->
                        Text(f, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(vertical = 2.dp))
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Trademark Notice", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Kotlin™ is a trademark of JetBrains s.r.o. KtLab is an independent offline IDE for educational purposes and is not affiliated with or endorsed by JetBrains s.r.o.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
