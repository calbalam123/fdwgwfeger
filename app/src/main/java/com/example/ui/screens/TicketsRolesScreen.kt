package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AutoNoticeEntity
import com.example.data.local.RoleRuleEntity
import com.example.data.local.TicketEntity
import com.example.ui.BotViewModel
import com.example.ui.components.DiscordEmbedPreview
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
import com.example.ui.theme.StatusIdle
import com.example.ui.theme.StatusOnline

@Composable
fun TicketsRolesScreen(
    viewModel: BotViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tickets by viewModel.tickets.collectAsState()
    val notices by viewModel.notices.collectAsState()
    val roles by viewModel.roleRules.collectAsState()
    val filterStatus by viewModel.selectedTicketFilter.collectAsState()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Tickets, 1: Roles, 2: Auto Notices

    var showCreateTicketDialog by remember { mutableStateOf(false) }
    var showCreateRoleDialog by remember { mutableStateOf(false) }
    var showCreateNoticeDialog by remember { mutableStateOf(false) }

    val filteredTickets = remember(tickets, filterStatus) {
        if (filterStatus == "ALL") tickets else tickets.filter { it.status == filterStatus }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tickets_roles_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Sub-tabs (티켓 관리, 역할 관리, 공지 자동화)
        item {
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = DiscordDarkSurface,
                contentColor = IvePink,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = IvePink
                    )
                }
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text("문의 티켓 (${tickets.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text("역할 관리 (${roles.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = activeSubTab == 2,
                    onClick = { activeSubTab = 2 },
                    text = { Text("공지 자동화 (${notices.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        // --- SUBTAB 0: TICKETS ---
        if (activeSubTab == 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "티켓 상태 필터",
                        color = DiscordTextSecondary,
                        fontSize = 13.sp
                    )

                    Button(
                        onClick = { showCreateTicketDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = IvePink),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("create_ticket_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("새 티켓 생성", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Filter chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("ALL" to "전체", "OPEN" to "접수(OPEN)", "IN_PROGRESS" to "처리중", "RESOLVED" to "해결완료").forEach { (key, label) ->
                        val isSelected = filterStatus == key
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) DiscordBlurple else DiscordDarkCard)
                                .clickable { viewModel.setTicketFilter(key) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else DiscordTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (filteredTickets.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(DiscordDarkCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("해당 조건의 티켓이 없습니다.", color = DiscordTextMuted, fontSize = 13.sp)
                    }
                }
            } else {
                items(filteredTickets, key = { it.id }) { ticket ->
                    TicketCardItem(
                        ticket = ticket,
                        onStatusChange = { newStatus ->
                            viewModel.updateTicketStatus(ticket, newStatus)
                            Toast.makeText(context, "티켓 #${ticket.ticketNumber} 상태 변경: $newStatus", Toast.LENGTH_SHORT).show()
                        },
                        onDelete = {
                            viewModel.deleteTicket(ticket.id)
                            Toast.makeText(context, "티켓 #${ticket.ticketNumber}이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        // --- SUBTAB 1: ROLE MANAGEMENT ---
        if (activeSubTab == 1) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "자동 역할 및 리액션 역할 목록",
                        color = DiscordTextSecondary,
                        fontSize = 13.sp
                    )

                    Button(
                        onClick = { showCreateRoleDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = IveViolet),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_role_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("새 역할 규칙 추가", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(roles, key = { it.id }) { role ->
                RoleCardItem(
                    role = role,
                    onDelete = {
                        viewModel.deleteRoleRule(role.id)
                        Toast.makeText(context, "${role.roleName} 규칙이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // --- SUBTAB 2: AUTO NOTICES ---
        if (activeSubTab == 2) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "스케줄 자동 공지 및 임베드 빌더",
                        color = DiscordTextSecondary,
                        fontSize = 13.sp
                    )

                    Button(
                        onClick = { showCreateNoticeDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = IvePink),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("add_notice_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("새 공지 추가", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(notices, key = { it.id }) { notice ->
                NoticeCardItem(
                    notice = notice,
                    onToggle = { isEnabled -> viewModel.toggleNotice(notice, isEnabled) },
                    onSendTest = {
                        viewModel.sendInstantNoticeTest(notice)
                        Toast.makeText(context, "${notice.channelName} 채널로 공지 즉시 전송 완료!", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = {
                        viewModel.deleteNotice(notice.id)
                        Toast.makeText(context, "공지가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    // Dialog: Create Ticket
    if (showCreateTicketDialog) {
        var title by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("팬아트/총공 인증") }
        var author by remember { mutableStateOf("DIVE팬#1201") }
        var details by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCreateTicketDialog = false },
            title = { Text("새 문의/인증 티켓 생성", color = DiscordTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("티켓 제목") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = author,
                        onValueChange = { author = it },
                        label = { Text("작성자 닉네임") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("카테고리 (인증/문의/신고 등)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = details,
                        onValueChange = { details = it },
                        label = { Text("상세 내용") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.createTicket(
                                title = title,
                                category = category,
                                author = author,
                                details = details.ifBlank { "내용 없음" },
                                priority = "NORMAL"
                            )
                            showCreateTicketDialog = false
                            Toast.makeText(context, "티켓이 정상적으로 접수되었습니다!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IvePink)
                ) {
                    Text("생성", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateTicketDialog = false }) {
                    Text("취소", color = DiscordTextSecondary)
                }
            },
            containerColor = DiscordDarkSurface
        )
    }

    // Dialog: Create Role Rule
    if (showCreateRoleDialog) {
        var roleName by remember { mutableStateOf("") }
        var emoji by remember { mutableStateOf("🌸") }
        var channel by remember { mutableStateOf("#역할-선택") }
        var desc by remember { mutableStateOf("이모지 클릭 시 자동 지급") }

        AlertDialog(
            onDismissRequest = { showCreateRoleDialog = false },
            title = { Text("새 역할 규칙 추가", color = DiscordTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = roleName,
                        onValueChange = { roleName = it },
                        label = { Text("역할 명칭 (예: 💎 Official DIVE)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = emoji,
                        onValueChange = { emoji = it },
                        label = { Text("반응 이모지") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = channel,
                        onValueChange = { channel = it },
                        label = { Text("적용 채널") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("규칙 설명") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (roleName.isNotBlank()) {
                            viewModel.addRoleRule(
                                name = roleName,
                                colorHex = "#EC4899",
                                emoji = emoji,
                                ruleType = "REACTION",
                                targetChannel = channel,
                                desc = desc
                            )
                            showCreateRoleDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IveViolet)
                ) {
                    Text("추가", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateRoleDialog = false }) {
                    Text("취소", color = DiscordTextSecondary)
                }
            },
            containerColor = DiscordDarkSurface
        )
    }

    // Dialog: Create Notice
    if (showCreateNoticeDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var channel by remember { mutableStateOf("#공지사항") }
        var scheduleTime by remember { mutableStateOf("매일 18:00") }

        AlertDialog(
            onDismissRequest = { showCreateNoticeDialog = false },
            title = { Text("새 자동 공지사항 등록", color = DiscordTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("공지 제목") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("공지 내용") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = channel,
                        onValueChange = { channel = it },
                        label = { Text("전송 채널 (예: #공지사항)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = scheduleTime,
                        onValueChange = { scheduleTime = it },
                        label = { Text("스케줄 시간 (예: 매일 오전 9시)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addNotice(
                                title = title,
                                content = content,
                                channel = channel,
                                scheduleType = "DAILY_MORNING",
                                scheduleDisplay = scheduleTime,
                                colorHex = "#EC4899"
                            )
                            showCreateNoticeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IvePink)
                ) {
                    Text("등록", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateNoticeDialog = false }) {
                    Text("취소", color = DiscordTextSecondary)
                }
            },
            containerColor = DiscordDarkSurface
        )
    }
}

@Composable
fun TicketCardItem(
    ticket: TicketEntity,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DiscordDarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${ticket.ticketNumber}",
                        color = IvePink,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DiscordDarkCard)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = ticket.category,
                            color = DiscordTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                StatusBadge(status = ticket.status)
            }

            Text(
                text = ticket.title,
                color = DiscordTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = ticket.details,
                color = DiscordTextSecondary,
                fontSize = 12.sp,
                maxLines = 2
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "작성자: ${ticket.authorDiscordTag} • 담당: ${ticket.assignedStaff}",
                    color = DiscordTextMuted,
                    fontSize = 11.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (ticket.status != "RESOLVED") {
                        Button(
                            onClick = { onStatusChange("RESOLVED") },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusOnline),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("해결 완료", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onStatusChange("OPEN") },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("다시 열기", fontSize = 10.sp)
                        }
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "삭제", tint = IveRose, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun RoleCardItem(
    role: RoleRuleEntity,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DiscordDarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DiscordDarkCard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(role.emoji, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = role.roleName,
                        color = DiscordTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${role.targetChannel}  •  ${role.description} (${role.assignedCount}명 보유)",
                        color = DiscordTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "삭제", tint = DiscordTextMuted, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
fun NoticeCardItem(
    notice: AutoNoticeEntity,
    onToggle: (Boolean) -> Unit,
    onSendTest: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DiscordDarkSurface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, DiscordDarkBorder, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notice.channelName,
                        color = DiscordBlurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "스케줄: ${notice.scheduleDisplayTime}",
                        color = DiscordTextSecondary,
                        fontSize = 11.sp
                    )
                }

                Switch(
                    checked = notice.isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = IvePink, checkedTrackColor = IvePink.copy(alpha = 0.5f))
                )
            }

            // Embedded preview
            DiscordEmbedPreview(
                title = notice.title,
                description = notice.content,
                channelName = notice.channelName
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSendTest,
                    colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("디스코드로 즉시 전송 테스트", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "삭제", tint = IveRose, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
