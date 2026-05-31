package com.countdownapp.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.countdownapp.data.entity.Event
import com.countdownapp.ui.components.AddEventDialog
import com.countdownapp.ui.components.EventCard
import com.countdownapp.ui.components.SwipeToDeleteContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    events: List<Event>,
    isEditMode: Boolean,
    selectedEventIds: Set<Long>,
    onAddEvent: (String, Long, Int, Int, String, Int, Int, Int, Boolean, Boolean) -> Unit,
    onDeleteEvent: (Event) -> Unit,
    onEventClick: (Event) -> Unit,
    onEnterEditMode: () -> Unit,
    onExitEditMode: () -> Unit,
    onToggleSelection: (Long) -> Unit,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onDeleteSelected: () -> Unit,
    onTogglePin: (Event) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Track which swipe is open (mutual exclusion)
    var openedEventId by remember { mutableStateOf<Long?>(null) }
    var closeRequestVersion by remember { mutableIntStateOf(0) }

    fun closeOpenSwipe() {
        if (openedEventId != null) {
            openedEventId = null
            closeRequestVersion++
        }
    }

    // LazyColumn scroll state for closing swipes on scroll
    val lazyListState = androidx.compose.foundation.lazy.rememberLazyListState()

    // Close swipes when scroll begins
    LaunchedEffect(lazyListState.isScrollInProgress) {
        if (lazyListState.isScrollInProgress) {
            closeOpenSwipe()
        }
    }

    BackHandler(enabled = openedEventId != null) {
        closeOpenSwipe()
    }

    BackHandler(enabled = isEditMode && openedEventId == null) {
        onExitEditMode()
    }

    Scaffold(
        topBar = {
            if (isEditMode) {
                // Edit mode top bar
                TopAppBar(
                    title = {
                        Text(
                            text = "已选择 ${selectedEventIds.size} 项",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        TextButton(onClick = onExitEditMode) {
                            Text("取消", color = Color.White)
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { showDeleteConfirmDialog = true },
                            enabled = selectedEventIds.isNotEmpty()
                        ) {
                            Text(
                                text = "删除",
                                color = if (selectedEventIds.isNotEmpty()) Color(0xFFFF3B30) else Color.Gray
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF6650a4)
                    )
                )
            } else {
                // Normal top bar
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "倒数日",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    actions = {
                        TextButton(onClick = {
                            closeOpenSwipe()
                            onEnterEditMode()
                        }) {
                            Text("编辑", color = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFF6650a4)
                    )
                )
            }
        },
        floatingActionButton = {
            if (!isEditMode) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = Color(0xFF6650a4)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "添加事件",
                        tint = Color.White
                    )
                }
            }
        },
        bottomBar = {
            if (isEditMode) {
                BottomAppBar(
                    containerColor = Color.White,
                    contentColor = Color(0xFF6650a4)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        val isAllSelected = events.isNotEmpty() && selectedEventIds.size == events.size
                        TextButton(
                            onClick = {
                                if (isAllSelected) {
                                    onDeselectAll()
                                } else {
                                    onSelectAll()
                                }
                            },
                            enabled = events.isNotEmpty()
                        ) {
                            Text(if (isAllSelected) "全不选" else "全选")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
                .pointerInput(isEditMode, openedEventId) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val up = waitForUpOrCancellation(PointerEventPass.Final)
                        if (!isEditMode && openedEventId != null && up != null) {
                            val dx = up.position.x - down.position.x
                            val dy = up.position.y - down.position.y
                            val touchSlop = viewConfiguration.touchSlop
                            if (dx * dx + dy * dy <= touchSlop * touchSlop) {
                                closeOpenSwipe()
                            }
                        }
                    }
                }
        ) {
            if (events.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "还没有倒数日事件",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(isEditMode, openedEventId) {
                            awaitEachGesture {
                                val down = awaitFirstDown(requireUnconsumed = false)
                                val up = waitForUpOrCancellation(PointerEventPass.Final)
                                if (!isEditMode && openedEventId != null && up != null) {
                                    val dx = up.position.x - down.position.x
                                    val dy = up.position.y - down.position.y
                                    val touchSlop = viewConfiguration.touchSlop
                                    if (dx * dx + dy * dy <= touchSlop * touchSlop) {
                                        closeOpenSwipe()
                                    }
                                }
                            }
                        },
                    state = lazyListState,
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = events,
                        key = { it.id }
                    ) { event ->
                        val isOtherOpen = openedEventId != null && openedEventId != event.id

                        if (isEditMode) {
                            // Edit mode: checkbox + no swipe
                            EditModeEventCard(
                                event = event,
                                isSelected = selectedEventIds.contains(event.id),
                                onToggleSelection = { onToggleSelection(event.id) },
                                onClick = { onToggleSelection(event.id) }
                            )
                        } else {
                            SwipeToDeleteContainer(
                                event = event,
                                onDelete = onDeleteEvent,
                                onPinToggle = onTogglePin,
                                onCardClick = { clickedEvent ->
                                    if (openedEventId != null) {
                                        closeOpenSwipe()
                                    } else {
                                        onEventClick(clickedEvent)
                                    }
                                },
                                swipeEnabled = !isEditMode,
                                isOtherOpen = isOtherOpen,
                                closeRequestVersion = closeRequestVersion,
                                onOpenThis = { openedEventId = event.id },
                                onCloseThis = {
                                    if (openedEventId == event.id) {
                                        openedEventId = null
                                    }
                                }
                            ) { e, isOpen, onCardClickHandler ->
                                EventCard(
                                    event = e,
                                    isSwipedOpen = isOpen,
                                    onClick = { onCardClickHandler(e) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, targetDate, month, day, calType, lunarY, lunarM, lunarD, isLeap, isRepeat ->
                onAddEvent(name, targetDate, month, day, calType, lunarY, lunarM, lunarD, isLeap, isRepeat)
                showAddDialog = false
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("确认删除") },
            text = { Text("确认删除 ${selectedEventIds.size} 个事件吗？") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmDialog = false
                        onDeleteSelected()
                    }
                ) {
                    Text("删除", color = Color(0xFFFF3B30))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun EditModeEventCard(
    event: Event,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onClick: () -> Unit
) {
    val eventColor = com.countdownapp.util.generateEventColor(event.name)
    val days = com.countdownapp.util.calculateEventDays(event)
    val description = com.countdownapp.util.getDaysDescription(days)
    val isTall = event.isPinned

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isTall) 160.dp else 80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggleSelection() },
                modifier = Modifier.padding(start = 8.dp)
            )
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(eventColor)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (event.isPinned) {
                        Text(text = "📌 ", fontSize = 16.sp)
                    }
                    Text(
                        text = event.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
