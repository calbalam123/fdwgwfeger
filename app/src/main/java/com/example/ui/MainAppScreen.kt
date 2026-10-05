package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusBadge
import com.example.ui.screens.BotBuilderScreen
import com.example.ui.screens.CloudHostingAndLogsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.IveFanroomScreen
import com.example.ui.screens.TicketsRolesScreen
import com.example.ui.theme.DiscordBlurple
import com.example.ui.theme.DiscordDarkBorder
import com.example.ui.theme.DiscordDarkSurface
import com.example.ui.theme.DiscordTextMuted
import com.example.ui.theme.DiscordTextPrimary
import com.example.ui.theme.DiscordTextSecondary
import com.example.ui.theme.IvePink
import com.example.ui.theme.IveViolet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(
    viewModel: BotViewModel,
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val config by viewModel.config.collectAsState()

    // Handle back button when on sub-screens to return to Dashboard
    if (currentTab != NavigationTab.DASHBOARD) {
        BackHandler {
            viewModel.selectTab(NavigationTab.DASHBOARD)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF111214),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DiscordDarkSurface,
                    titleContentColor = DiscordTextPrimary
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(IvePink, IveViolet))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("✨", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DIVE Bot Studio",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "IVE 팬방 올인원 매니저 & AI 봇",
                                fontSize = 11.sp,
                                color = DiscordTextSecondary
                            )
                        }
                        StatusBadge(
                            status = if (config?.isRunning == true) "ONLINE" else "OFFLINE",
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DiscordDarkSurface,
                modifier = Modifier
                    .border(1.dp, DiscordDarkBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                NavigationTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    val (icon, label) = when (tab) {
                        NavigationTab.DASHBOARD -> Icons.Default.Dashboard to "대시보드"
                        NavigationTab.BOT_BUILDER -> Icons.Default.SmartToy to "AI 봇 빌더"
                        NavigationTab.IVE_FANROOM -> Icons.Default.Favorite to "아이브 팬룸"
                        NavigationTab.TICKETS_ROLES -> Icons.Default.ConfirmationNumber to "티켓·역할"
                        NavigationTab.HOSTING_LOGS -> Icons.Default.CloudDone to "호스팅·로그"
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(tab) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = IvePink,
                            unselectedIconColor = DiscordTextMuted,
                            unselectedTextColor = DiscordTextMuted,
                            indicatorColor = IvePink.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.testTag("nav_item_${tab.iconTag}")
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
            when (currentTab) {
                NavigationTab.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                NavigationTab.BOT_BUILDER -> BotBuilderScreen(viewModel = viewModel)
                NavigationTab.IVE_FANROOM -> IveFanroomScreen(viewModel = viewModel)
                NavigationTab.TICKETS_ROLES -> TicketsRolesScreen(viewModel = viewModel)
                NavigationTab.HOSTING_LOGS -> CloudHostingAndLogsScreen(viewModel = viewModel)
            }
        }
    }
}
