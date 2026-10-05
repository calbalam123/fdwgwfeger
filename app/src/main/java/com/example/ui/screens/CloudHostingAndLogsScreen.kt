package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BotConfigEntity
import com.example.data.remote.DiscordHelper
import com.example.ui.BotViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DiscordBlurple
import com.example.ui.theme.DiscordDarkBorder
import com.example.ui.theme.DiscordDarkCard
import com.example.ui.theme.DiscordDarkSurface
import com.example.ui.theme.DiscordTextMuted
import com.example.ui.theme.DiscordTextPrimary
import com.example.ui.theme.DiscordTextSecondary
import com.example.ui.theme.IveGold
import com.example.ui.theme.IvePink
import com.example.ui.theme.IveRose
import com.example.ui.theme.IveViolet
import com.example.ui.theme.StatusOnline

@Composable
fun CloudHostingAndLogsScreen(
    viewModel: BotViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val config by viewModel.config.collectAsState()
    val logs by viewModel.logs.collectAsState()
    val uptime by viewModel.uptimeSeconds.collectAsState()
    val ping by viewModel.serverPing.collectAsState()
    val cpu by viewModel.serverCpu.collectAsState()
    val ram by viewModel.serverRam.collectAsState()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Cloud 24/7 Hosting, 1: Security Shield, 2: Live Logs
    var selectedLogFilter by remember { mutableStateOf("ALL") }

    val currentConfig = config ?: BotConfigEntity()
    var antiRaid by remember(currentConfig) { mutableStateOf(currentConfig.antiRaid) }
    var antiSpam by remember(currentConfig) { mutableStateOf(currentConfig.antiSpam) }

    val hours = uptime / 3600
    val minutes = (uptime % 3600) / 60
    val uptimeString = "${hours}시간 ${minutes}분 ${uptime % 60}초"

    val filteredLogs = remember(logs, selectedLogFilter) {
        if (selectedLogFilter == "ALL") logs else logs.filter { it.level == selectedLogFilter }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("hosting_logs_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top SubTabs: [24시 클라우드 호스팅] [보안 설정] [실시간 로그]
        item {
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = DiscordDarkSurface,
                contentColor = StatusOnline,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = StatusOnline
                    )
                }
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("24시 호스팅", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("보안 설정", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = activeSubTab == 2,
                    onClick = { activeSubTab = 2 },
                    text = { Text("실시간 로그 (${logs.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        // --- SUBTAB 0: 24/7 CLOUD HOSTING ---
        if (activeSubTab == 0) {
            // Cloud Status Overview Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StatusOnline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(StatusOnline.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = StatusOnline, modifier = Modifier.size(24.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = currentConfig.hostingProvider,
                                        color = DiscordTextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "무중단 Watchdog 데몬 상시 감시 중",
                                        color = StatusOnline,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Switch(
                                checked = currentConfig.isCloud247Enabled,
                                onCheckedChange = { viewModel.toggleCloud247(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = StatusOnline, checkedTrackColor = StatusOnline.copy(alpha = 0.5f)),
                                modifier = Modifier.testTag("toggle_cloud_hosting_switch")
                            )
                        }

                        // Resource Metrics Grid (Ping, CPU, RAM, Uptime)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MetricBox(label = "지연시간 (Ping)", value = "${ping}ms", icon = Icons.Default.Speed, modifier = Modifier.weight(1f))
                            MetricBox(label = "CPU 사용량", value = String.format("%.1f%%", cpu), icon = Icons.Default.Memory, modifier = Modifier.weight(1f))
                            MetricBox(label = "메모리 점유", value = "${ram}MB", icon = Icons.Default.Cloud, modifier = Modifier.weight(1f))
                        }

                        // Uptime display
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DiscordDarkCard)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = StatusOnline, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("가동 시간 (Uptime):", color = DiscordTextSecondary, fontSize = 12.sp)
                                }
                                Text(uptimeString, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // 1-Click Cloud Hosting Deploy Options (Railway, Render, Fly.io, Cloud Run)
            item {
                SectionHeader(
                    title = "원클릭 외부 클라우드 호스팅 코드 내보내기",
                    subtitle = "개인 서버나 Railway, Fly.io 등에 24시간 배포할 수 있는 완전한 소스코드를 즉시 생성합니다"
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DiscordDarkBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = DiscordBlurple)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("24/7 discord.py 완전 실행 스크립트", color = DiscordTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Button(
                                onClick = {
                                    val code = DiscordHelper.generatePythonBotScript(
                                        botName = currentConfig.botName,
                                        prefix = currentConfig.prefix,
                                        statusText = currentConfig.statusText,
                                        hasGemini = true
                                    )
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Discord Bot Script", code))
                                    Toast.makeText(context, "24시간 봇 파이썬 스크립트가 클립보드에 복사되었습니다!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("코드 복사", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F1012))
                                .padding(10.dp)
                        ) {
                            Text(
                                text = DiscordHelper.generatePythonBotScript(
                                    botName = currentConfig.botName,
                                    prefix = currentConfig.prefix,
                                    statusText = currentConfig.statusText,
                                    hasGemini = true
                                ),
                                color = Color(0xFF6EE7B7),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 6
                            )
                        }

                        // Dockerfile export button
                        OutlinedButton(
                            onClick = {
                                val docker = DiscordHelper.generateDockerfile()
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Dockerfile", docker))
                                Toast.makeText(context, "Dockerfile이 클립보드에 복사되었습니다!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Dockerfile 복사 (Docker / Cloud Run 배포용)", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // --- SUBTAB 1: SECURITY SETTINGS ---
        if (activeSubTab == 1) {
            item {
                SectionHeader(
                    title = "서버 및 봇 보안 방화벽 설정",
                    subtitle = "테러 및 스팸, 도배 공격으로부터 아이브 팬방을 완벽 보호합니다"
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, DiscordDarkBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Anti-Raid Shield
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF0284C7).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Anti-Raid 레이드 방어 쉴드", color = DiscordTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("단시간 대량 비정상 계정 입장 시 자동 격리", color = DiscordTextSecondary, fontSize = 11.sp)
                                }
                            }

                            Switch(
                                checked = antiRaid,
                                onCheckedChange = {
                                    antiRaid = it
                                    viewModel.updateBotConfig(currentConfig.copy(antiRaid = it))
                                    Toast.makeText(context, "Anti-Raid 설정 변경됨: $it", Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF38BDF8))
                            )
                        }

                        // Anti-Spam Filter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(IvePink.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = IvePink, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("피싱 링크 & 도배 자동 차단", color = DiscordTextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("유해 URL, 디스코드 가짜 니트로 링크 즉시 삭제", color = DiscordTextSecondary, fontSize = 11.sp)
                                }
                            }

                            Switch(
                                checked = antiSpam,
                                onCheckedChange = {
                                    antiSpam = it
                                    viewModel.updateBotConfig(currentConfig.copy(antiSpam = it))
                                    Toast.makeText(context, "Anti-Spam 설정 변경됨: $it", Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = IvePink)
                            )
                        }

                        // Token Encryption Storage Badge
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(DiscordDarkCard)
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = StatusOnline, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("AES-256 토큰 보안 금고 보관 중", color = StatusOnline, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text("디스코드 봇 토큰 및 제미나이 키는 기기 내 격리 저장소에 안전하게 보호됩니다.", color = DiscordTextMuted, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- SUBTAB 2: REAL-TIME EVENT LOGS ---
        if (activeSubTab == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("실시간 게이트웨이 및 봇 이벤트 로그", color = DiscordTextSecondary, fontSize = 13.sp)
                    IconButton(
                        onClick = {
                            viewModel.clearAllLogs()
                            Toast.makeText(context, "로그가 초기화되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = "로그 비우기", tint = DiscordTextSecondary)
                    }
                }
            }

            // Filter row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL" to "전체", "AI" to "AI 대화", "SUCCESS" to "성공", "SECURITY" to "보안", "INFO" to "정보").forEach { (key, label) ->
                        val isSelected = selectedLogFilter == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) DiscordBlurple else DiscordDarkCard)
                                .clickable { selectedLogFilter = key }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else DiscordTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (filteredLogs.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DiscordDarkCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("표시할 로그 내역이 없습니다.", color = DiscordTextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(filteredLogs, key = { it.id }) { log ->
                    LogItemRow(log = log)
                }
            }
        }
    }
}

@Composable
fun MetricBox(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DiscordDarkCard)
            .padding(10.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = DiscordTextMuted, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(label, color = DiscordTextMuted, fontSize = 10.sp, maxLines = 1)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}
