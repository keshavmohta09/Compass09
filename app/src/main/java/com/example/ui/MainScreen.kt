package com.example.ui

import android.app.Activity
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CompassScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VastuGuideScreen
import com.example.ui.theme.BackgroundBlack
import com.example.ui.theme.CardBorder
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldSubtle
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextTertiary
import com.example.viewmodel.CompassTab
import com.example.viewmodel.CompassViewModel

@Composable
fun MainScreen(
    viewModel: CompassViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Keep screen on management
    DisposableEffect(uiState.appSettings.keepScreenOn) {
        val window = (context as? Activity)?.window
        if (uiState.appSettings.keepScreenOn) {
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundBlack),
        containerColor = BackgroundBlack,
        bottomBar = {
            Surface(
                color = SurfaceDark,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                NavigationBar(
                    containerColor = SurfaceDark,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    // Compass Tab
                    NavigationBarItem(
                        selected = uiState.activeTab == CompassTab.COMPASS,
                        onClick = {
                            viewModel.triggerClickHaptic()
                            viewModel.setActiveTab(CompassTab.COMPASS)
                        },
                        icon = {
                            Icon(
                                imageVector = if (uiState.activeTab == CompassTab.COMPASS) Icons.Filled.Explore else Icons.Outlined.Explore,
                                contentDescription = "Compass Tab"
                            )
                        },
                        label = {
                            Text(
                                text = "Compass",
                                fontSize = 11.sp,
                                fontWeight = if (uiState.activeTab == CompassTab.COMPASS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldLight,
                            selectedTextColor = EmeraldLight,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary,
                            indicatorColor = SurfaceVariantDark
                        ),
                        modifier = Modifier.testTag("nav_compass")
                    )

                    // Vastu Guide Tab
                    NavigationBarItem(
                        selected = uiState.activeTab == CompassTab.VASTU_GUIDE,
                        onClick = {
                            viewModel.triggerClickHaptic()
                            viewModel.setActiveTab(CompassTab.VASTU_GUIDE)
                        },
                        icon = {
                            Icon(
                                imageVector = if (uiState.activeTab == CompassTab.VASTU_GUIDE) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Vastu Guide Tab"
                            )
                        },
                        label = {
                            Text(
                                text = "Vastu Guide",
                                fontSize = 11.sp,
                                fontWeight = if (uiState.activeTab == CompassTab.VASTU_GUIDE) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldLight,
                            selectedTextColor = EmeraldLight,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary,
                            indicatorColor = SurfaceVariantDark
                        ),
                        modifier = Modifier.testTag("nav_vastu")
                    )

                    // Settings Tab
                    NavigationBarItem(
                        selected = uiState.activeTab == CompassTab.SETTINGS,
                        onClick = {
                            viewModel.triggerClickHaptic()
                            viewModel.setActiveTab(CompassTab.SETTINGS)
                        },
                        icon = {
                            Icon(
                                imageVector = if (uiState.activeTab == CompassTab.SETTINGS) Icons.Filled.Settings else Icons.Outlined.Settings,
                                contentDescription = "Settings Tab"
                            )
                        },
                        label = {
                            Text(
                                text = "Settings",
                                fontSize = 11.sp,
                                fontWeight = if (uiState.activeTab == CompassTab.SETTINGS) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldLight,
                            selectedTextColor = EmeraldLight,
                            unselectedIconColor = TextTertiary,
                            unselectedTextColor = TextTertiary,
                            indicatorColor = SurfaceVariantDark
                        ),
                        modifier = Modifier.testTag("nav_settings")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                CompassTab.COMPASS -> CompassScreen(viewModel = viewModel, uiState = uiState)
                CompassTab.VASTU_GUIDE -> VastuGuideScreen(viewModel = viewModel)
                CompassTab.SETTINGS -> SettingsScreen(viewModel = viewModel, uiState = uiState)
            }
        }
    }
}
