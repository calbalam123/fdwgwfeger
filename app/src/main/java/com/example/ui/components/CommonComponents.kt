package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DiscordDarkBorder
import com.example.ui.theme.DiscordDarkCard
import com.example.ui.theme.DiscordTextMuted
import com.example.ui.theme.DiscordTextPrimary
import com.example.ui.theme.DiscordTextSecondary
import com.example.ui.theme.IvePink
import com.example.ui.theme.StatusDnd
import com.example.ui.theme.StatusIdle
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusOnline

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label, isPulsing) = when (status.uppercase()) {
        "ONLINE", "ACTIVE" -> Quadruple(StatusOnline.copy(alpha = 0.2f), StatusOnline, "ONLINE", true)
        "CLOUD_RUNNING", "24/7" -> Quadruple(IvePink.copy(alpha = 0.2f), IvePink, "24/7 CLOUD", true)
        "IDLE", "IN_PROGRESS" -> Quadruple(StatusIdle.copy(alpha = 0.2f), StatusIdle, "IDLE", false)
        "DND", "URGENT" -> Quadruple(StatusDnd.copy(alpha = 0.2f), StatusDnd, "DND", false)
        "RESOLVED" -> Quadruple(StatusOnline.copy(alpha = 0.2f), StatusOnline, "RESOLVED", false)
        else -> Quadruple(StatusOffline.copy(alpha = 0.2f), StatusOffline, "OFFLINE", false)
    }

    val alphaAnim by if (isPulsing) {
        val transition = rememberInfiniteTransition(label = "pulse")
        transition.animateFloat(
            initialValue = 0.4f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(800),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulseAlpha"
        )
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableFloatStateOf(1f) }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .alpha(alphaAnim)
                .clip(CircleShape)
                .background(textColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun StatCard(
    title: String,
    value: String,
    subtext: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = "stat_card"
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DiscordDarkCard),
        modifier = modifier
            .border(1.dp, DiscordDarkBorder.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = DiscordTextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = DiscordTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                color = DiscordTextMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                color = DiscordTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    color = DiscordTextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                color = IvePink,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable(onClick = onActionClick)
                    .padding(vertical = 4.dp, horizontal = 6.dp)
            )
        }
    }
}

@Composable
fun DiscordEmbedPreview(
    title: String,
    description: String,
    channelName: String,
    authorName: String = "IVE DIVE Notice Bot",
    footerText: String = "DIVE와 영원히 함께할 IVE 커뮤니티 알림",
    embedColor: Color = IvePink,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1F22)),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, DiscordDarkBorder, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Channel & Bot Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(embedColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🤖", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = authorName,
                    color = DiscordTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF5865F2))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "BOT",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "in $channelName",
                    color = DiscordTextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Embed Container with Left Border
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF2B2D31))
            ) {
                // Colored Left Strip
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(100.dp)
                        .background(embedColor)
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = description,
                        color = Color(0xFFDBDEE1),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$footerText • 오늘",
                        color = Color(0xFF949BA4),
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
