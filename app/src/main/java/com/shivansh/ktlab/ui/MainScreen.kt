package com.shivansh.ktlab.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shivansh.ktlab.ui.editor.EditorScreen
import com.shivansh.ktlab.ui.screens.AboutScreen
import com.shivansh.ktlab.ui.screens.ExamplesScreen
import com.shivansh.ktlab.ui.screens.SavedScreen
import com.shivansh.ktlab.ui.screens.SettingsScreen
import com.shivansh.ktlab.ui.screens.TutorialScreen
import com.shivansh.ktlab.ui.theme.*
import com.shivansh.ktlab.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = viewModel()) {

    if (viewModel.showSettingsScreen) {
        SettingsScreen(
            viewModel = viewModel,
            onBack = { viewModel.showSettingsScreen = false }
        )
        return
    }

    if (viewModel.showAboutScreen) {
        AboutScreen(
            onBack = { viewModel.showAboutScreen = false }
        )
        return
    }

    // Back button handling: If not on Editor tab (0), return to Editor tab (0)
    BackHandler(enabled = viewModel.selectedTab != 0) {
        viewModel.selectedTab = 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KtLab - Kotlin IDE", color = TextPrimary) },
                actions = {
                    IconButton(onClick = { viewModel.showSettingsScreen = true }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextPrimary)
                    }
                    IconButton(onClick = { viewModel.showAboutScreen = true }) {
                        Icon(Icons.Default.Info, contentDescription = "About", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkSurface)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = DarkSurface) {
                NavigationBarItem(
                    selected = viewModel.selectedTab == 0,
                    onClick = { viewModel.selectedTab = 0 },
                    icon = { Icon(Icons.Default.Code, contentDescription = "Editor") },
                    label = { Text("Editor") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = PrimaryGreen,
                        indicatorColor = PrimaryGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = viewModel.selectedTab == 1,
                    onClick = { viewModel.selectedTab = 1 },
                    icon = { Icon(Icons.Default.Book, contentDescription = "Examples") },
                    label = { Text("Examples") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = PrimaryGreen,
                        indicatorColor = PrimaryGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = viewModel.selectedTab == 2,
                    onClick = { viewModel.selectedTab = 2 },
                    icon = { Icon(Icons.Default.School, contentDescription = "Tutorial") },
                    label = { Text("Tutorial") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = PrimaryGreen,
                        indicatorColor = PrimaryGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
                NavigationBarItem(
                    selected = viewModel.selectedTab == 3,
                    onClick = { viewModel.selectedTab = 3 },
                    icon = { Icon(Icons.Default.Folder, contentDescription = "Saved") },
                    label = { Text("Saved") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = PrimaryGreen,
                        indicatorColor = PrimaryGreen,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.padding(innerPadding),
            color = DarkBackground
        ) {
            when (viewModel.selectedTab) {
                0 -> EditorScreen(viewModel)
                1 -> ExamplesScreen(viewModel)
                2 -> TutorialScreen(viewModel)
                3 -> SavedScreen(viewModel)
            }
        }
    }
}
