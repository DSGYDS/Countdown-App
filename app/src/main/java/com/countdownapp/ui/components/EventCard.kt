package com.countdownapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.countdownapp.data.entity.Event
import com.countdownapp.util.calculateEventDays
import com.countdownapp.util.formatLunarDate
import com.countdownapp.util.generateEventColor
import com.countdownapp.util.getDaysDescription

@Composable
fun EventCard(
    event: Event,
    isSwipedOpen: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val eventColor = generateEventColor(event.name)
    val days = calculateEventDays(event)
    val description = getDaysDescription(days)
    val lunarDateText = if (event.calendarType == "lunar" && event.lunarYear > 0) {
        formatLunarDate(
            year = event.lunarYear,
            month = event.lunarMonth,
            day = event.lunarDay,
            includeYear = !event.isRepeatYearly
        )
    } else {
        null
    }

    val isPinned = event.isPinned
    val isTall = isPinned // Tall card for pinned events
    val dateText = if (lunarDateText != null) {
        lunarDateText
    } else if (event.recurringMonth >= 0) {
        "${event.recurringMonth + 1}月${event.recurringDay}日"
    } else {
        val date = java.util.Date(event.targetDate)
        java.text.SimpleDateFormat("yyyy年MM月dd日", java.util.Locale.getDefault()).format(date)
    }
    val daysColor = if (days < 0) Color(0xFFFF5252) else Color(0xFF4CAF50)
    val cardModifier = if (isSwipedOpen) {
        modifier
    } else {
        modifier.clickable(onClick = onClick)
    }

    Card(
        modifier = cardModifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(if (isTall) 160.dp else 80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(eventColor)
            )

            if (isTall) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📌 ${event.name}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Column {
                            Text(
                                text = dateText,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = description,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (days < 0) (-days).toString() else days.toString(),
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            color = daysColor,
                            lineHeight = 56.sp
                        )
                        Text(
                            text = "天",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = event.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = lunarDateText?.let { "$it · $description" } ?: description,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
