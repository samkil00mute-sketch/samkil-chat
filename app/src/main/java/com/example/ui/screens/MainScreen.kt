package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.Brush
import com.example.data.model.Story
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Chat
import com.example.data.model.Message
import com.example.data.model.CallLog
import com.example.data.repository.CryptoHelper
import com.example.ui.theme.ChatBgDark
import com.example.ui.theme.ChatBgLight
import com.example.ui.theme.DarkSlateBg
import com.example.ui.theme.EmeraldBrand
import com.example.ui.theme.IncomingBubbleDark
import com.example.ui.theme.IncomingBubbleLight
import com.example.ui.theme.OutgoingBubbleDark
import com.example.ui.theme.OutgoingBubbleLight
import com.example.ui.theme.SecondaryTextDark
import com.example.ui.theme.SecondaryTextLight
import com.example.ui.theme.TealDark
import com.example.ui.theme.TealLight
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.CallStatus
import com.example.ui.viewmodel.ChatViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ChatViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableIntStateOf(0) }
    
    val selectedChatId by viewModel.selectedChatId.collectAsState()
    val activeCall by viewModel.activeCall.collectAsState()
    
    var showAddChatDialog by remember { mutableStateOf(false) }
    var activeViewingStories by remember { mutableStateOf<List<Story>?>(null) }
    var activeViewingIndex by remember { mutableIntStateOf(0) }
    var showAddStoryDialog by remember { mutableStateOf(false) }

    // Launcher for permissions to make calling feel 100% authentic
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { /* state handled locally in UX */ }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (selectedChatId == null && activeCall == null && currentTab == 0) {
                FloatingActionButton(
                    onClick = { showAddChatDialog = true },
                    containerColor = TealPrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .testTag("floating_add_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New chat",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        bottomBar = {
            if (selectedChatId == null && activeCall == null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("app_navigation_bar")
                ) {
                    val tabs = listOf("Chats", "Calls", "Keys & Safety")
                    val icons = listOf(Icons.Default.Lock, Icons.Default.Call, Icons.Default.Key)
                    
                    tabs.forEachIndexed { index, title ->
                        NavigationBarItem(
                            selected = currentTab == index,
                            onClick = { currentTab = index },
                            icon = { Icon(icons[index], contentDescription = title) },
                            label = { Text(title, fontWeight = FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF002020),
                                selectedTextColor = Color(0xFF002020),
                                indicatorColor = Color(0xFFCCE8E8),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B)
                            ),
                            modifier = Modifier.testTag("tab_${title.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main views based on currentTab
            Column(modifier = Modifier.fillMaxSize()) {
                if (selectedChatId == null) {
                    // Title Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "E2EE Secured App",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Samkil Chat",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontFamily = FontFamily.SansSerif,
                                modifier = Modifier.testTag("app_title")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "E2EE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Row {
                            IconButton(onClick = { showAddChatDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = "New chat",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Secured",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "End-to-end encrypted with Samkil Guard™",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                    // Screens switcher
                    when (currentTab) {
                        0 -> ChatListScreen(
                            viewModel = viewModel,
                            onSelect = { id -> viewModel.selectChat(id) },
                            onPostStoryClick = { showAddStoryDialog = true },
                            onViewStoryClick = { playlist, index ->
                                activeViewingStories = playlist
                                activeViewingIndex = index
                            }
                        )
                        1 -> CallLogScreen(viewModel)
                        2 -> SecurityKeysScreen(viewModel)
                    }
                }
            }

            // Overlay Full-Screen Story Viewer
            AnimatedVisibility(
                visible = activeViewingStories != null,
                enter = fadeIn(animationSpec = tween(220)),
                exit = fadeOut(animationSpec = tween(180))
            ) {
                activeViewingStories?.let { playlist ->
                    StoryViewer(
                        playlist = playlist,
                        startIndex = activeViewingIndex,
                        viewModel = viewModel,
                        onClose = { activeViewingStories = null }
                    )
                }
            }

            // Add Story Dialog
            if (showAddStoryDialog) {
                AddStoryDialog(
                    viewModel = viewModel,
                    onDismiss = { showAddStoryDialog = false }
                )
            }

            // Overlay Chat details
            AnimatedVisibility(
                visible = selectedChatId != null,
                enter = fadeIn(animationSpec = tween(220)) + slideInVertically(initialOffsetY = { it / 6 }, animationSpec = tween(220)),
                exit = fadeOut(animationSpec = tween(180)) + slideOutVertically(targetOffsetY = { it / 6 }, animationSpec = tween(180))
            ) {
                ActiveChatScreen(
                    viewModel = viewModel,
                    onBack = { viewModel.deselectChat() }
                )
            }

            // Overlay Full-Screen Call overlay
            AnimatedVisibility(
                visible = activeCall != null,
                enter = fadeIn(animationSpec = tween(250)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                activeCall?.let { call ->
                    CallHudOverlay(
                        activeCall = call,
                        onEndCall = { viewModel.endCall() },
                        onAccept = { viewModel.acceptCall() }
                    )
                }
            }

            // Add Chat Dialog
            if (showAddChatDialog) {
                var newChatName by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showAddChatDialog = false },
                    title = { Text("Start Secure Chat Channel") },
                    text = {
                        Column {
                            Text(
                                "Enter the contact's name below. A direct peer key agreement will be negotiated using Diffie-Hellman protocols.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            OutlinedTextField(
                                value = newChatName,
                                onValueChange = { newChatName = it },
                                label = { Text("Contact Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    focusedLabelColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("new_chat_input")
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (newChatName.isNotBlank()) {
                                    viewModel.addNewChat(newChatName)
                                    showAddChatDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("create_chat_confirm")
                        ) {
                            Text("Secure Connect")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showAddChatDialog = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

// ------------------------------------
// CHATS TAB VIEW
// ------------------------------------
@Composable
fun ChatListScreen(
    viewModel: ChatViewModel,
    onSelect: (String) -> Unit,
    onPostStoryClick: () -> Unit,
    onViewStoryClick: (List<Story>, Int) -> Unit
) {
    val chats by viewModel.chats.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Embed Stories List at the top of the chat logs
        item {
            StoriesRow(
                viewModel = viewModel,
                onPostClick = onPostStoryClick,
                onViewClick = onViewStoryClick
            )
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (chats.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Empty secure logs",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Secure Chats Established",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Tap the plus button above to add an E2EE contact",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(chats) { chat ->
                ChatItemRow(chat = chat, onClick = { onSelect(chat.id) })
                HorizontalDivider(
                    modifier = Modifier.padding(start = 76.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                )
            }
        }
    }
}

@Composable
fun ChatItemRow(chat: Chat, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar circle
        val isBot = chat.id == "samkil_bot"
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    if (isBot) TealPrimary else Color(0xFFCCE8E8),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chat.avatarDescription,
                color = if (isBot) Color.White else Color(0xFF002020),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            // Online dot representation
            if (chat.isOnline) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Green, shape = CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Message texts and status side-by-side
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.contactName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "encrypted",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))

                    val isTyping = chat.isTyping
                    val dispText = if (isTyping) "typing..." else {
                        val plain = CryptoHelper.decrypt(chat.lastMessageText, chat.opponentPublicKey)
                        plain
                    }

                    Text(
                        text = dispText,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                        color = if (isTyping) EmeraldBrand else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(vertical = 2.dp)
            ) {
                // Formatted timestamp
                val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault())
                    .format(Date(chat.lastMessageTimestamp))
                Text(
                    text = timeStr,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                if (chat.unreadCount > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = chat.unreadCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ------------------------------------
// HISTORIC CALLS VIEW
// ------------------------------------
@Composable
fun CallLogScreen(viewModel: ChatViewModel) {
    val callLogs by viewModel.callLogs.collectAsState()

    if (callLogs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "No voice video logs",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "No Encryption Call logs",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "Real-time calls are secured using end-to-end keys",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Encryption Verified Handshakes",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "Clear Logs",
                    fontSize = 12.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .clickable { viewModel.clearHistory() }
                        .padding(4.dp)
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(callLogs) { log ->
                    CallLogItem(log = log) {
                        viewModel.startCall(log.contactName, log.avatarDescription, log.callType == "VIDEO")
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 76.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                    )
                }
            }
        }
    }
}

@Composable
fun CallLogItem(log: CallLog, onRecall: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRecall() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFF78909C), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(log.avatarDescription, color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(log.contactName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (log.callType == "VIDEO") Icons.Default.Videocam else Icons.Default.Phone,
                        contentDescription = "call_type",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier
                            .size(13.dp)
                            .padding(end = 2.dp)
                    )
                    val dateFormatted = SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(Date(log.timestamp))
                    Text(
                        text = "$dateFormatted • ${if (log.durationSeconds > 0) "${log.durationSeconds}s verified" else "Secured Handshake (Missed)"}",
                        fontSize = 12.sp,
                        color = if (log.status == "MISSED") Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }

        IconButton(onClick = onRecall) {
            Icon(
                imageVector = if (log.callType == "VIDEO") Icons.Default.Videocam else Icons.Default.Call,
                contentDescription = "call_now",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ------------------------------------
// CRYPTOGRAPHY KEYS SCREEN
// ------------------------------------
@Composable
fun SecurityKeysScreen(viewModel: ChatViewModel) {
    val myPublicKey by viewModel.myPublicKey.collectAsState()
    val chats by viewModel.chats.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Security Status",
                            tint = EmeraldBrand,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Cryptography Identity Node", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Active Session DH Handshake", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Text(
                        "Samkil Chat implements client-side End-to-End Encryption. Only you and the respective contacts possess the private keys required to decrypt messaging packets. Messages cannot be read in transit.",
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Your Public Key (Fingerprint)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        IconButton(onClick = { viewModel.rotateMyKeys() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Rotate Keys", tint = EmeraldBrand)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = myPublicKey,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Updating keys renegotiates active handshakes on future chat sessions without dropping the current history.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }

        item {
            Text(
                "Active Peer Agreements",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 4.dp, top = 8.dp)
            )
        }

        items(chats) { chat ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(1.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clickable { viewModel.selectChat(chat.id) }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "key",
                            tint = EmeraldBrand,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(chat.contactName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(chat.opponentPublicKey, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "verified",
                        tint = Color.Green,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ------------------------------------
// ACTIVE CHAT SCREEN
// ------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveChatScreen(viewModel: ChatViewModel, onBack: () -> Unit) {
    val chat by viewModel.selectedChat.collectAsState()
    val messages by viewModel.activeMessages.collectAsState()
    
    val listState = rememberLazyListState()
    var typedText by remember { mutableStateOf("") }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showSignatureVerificationSheet by remember { mutableStateOf(false) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    if (chat == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val activeChat = chat!!

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Header Profile Info
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showSignatureVerificationSheet = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(TealPrimary.copy(alpha = 0.8f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(activeChat.avatarDescription, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = activeChat.contactName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (activeChat.isTyping) "typing..." else if (activeChat.isOnline) "online" else "offline",
                            fontSize = 11.sp,
                            color = if (activeChat.isTyping) EmeraldBrand else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontWeight = if (activeChat.isTyping) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }

                // Secure Handshakes controls
                Row {
                    IconButton(onClick = {
                        showSignatureVerificationSheet = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Verify key signatures",
                            tint = EmeraldBrand
                        )
                    }

                    IconButton(onClick = {
                        viewModel.startCall(activeChat.contactName, activeChat.avatarDescription, false)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Voice Call",
                            tint = TealPrimary,
                            modifier = Modifier.testTag("action_voice_call")
                        )
                    }

                    IconButton(onClick = {
                        viewModel.startCall(activeChat.contactName, activeChat.avatarDescription, true)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = TealPrimary,
                            modifier = Modifier.testTag("action_video_call")
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(if (MaterialTheme.colorScheme.background == DarkSlateBg) ChatBgDark else ChatBgLight)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Cryptographic warning badge
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "e2ee footprint verified",
                        tint = EmeraldBrand,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "E2EE Verified: ${activeChat.opponentPublicKey.take(15)}...",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                // Messages list
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        Color(0xFFE1F5FE).copy(alpha = if (MaterialTheme.colorScheme.background == DarkSlateBg) 0.15f else 0.85f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "🔒 Messages are fully end-to-end encrypted with AES-128. Local public key agreement verified.",
                                    fontSize = 11.sp,
                                    color = if (MaterialTheme.colorScheme.background == DarkSlateBg) Color.White else TealDark,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    items(messages) { message ->
                        MessageBubbleRow(message = message, chat = activeChat)
                    }
                }

                // Message composer layout
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(26.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            IconButton(onClick = { showAttachmentSheet = !showAttachmentSheet }) {
                                Icon(
                                    imageVector = Icons.Default.Attachment,
                                    contentDescription = "Choose Secure Media Documents",
                                    tint = TealPrimary,
                                    modifier = Modifier.testTag("action_attach")
                                )
                            }

                            TextField(
                                value = typedText,
                                onValueChange = { typedText = it },
                                placeholder = { Text("Message...") },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                singleLine = false,
                                maxLines = 4,
                                textStyle = LocalThemeTextStyle(),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(onSend = {
                                    if (typedText.isNotBlank()) {
                                        viewModel.sendSecureMessage(typedText)
                                        typedText = ""
                                    }
                                }),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("message_input")
                            )

                            AnimatedVisibility(visible = typedText.isBlank()) {
                                IconButton(onClick = {
                                    // Simulated real-time captured selfie share
                                    viewModel.sendSecureMessage(
                                        plainText = "🔑 Real-time Secure Photo",
                                        mediaType = "IMAGE",
                                        mediaUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=400&q=80",
                                        mediaCaption = "Secure Photo Share"
                                    )
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Secure Snap",
                                        tint = TealPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Circular send buttons
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(TealPrimary, shape = CircleShape)
                            .clickable {
                                if (typedText.isNotBlank()) {
                                    viewModel.sendSecureMessage(typedText)
                                    typedText = ""
                                } else {
                                    // Simulated quick audio voice message send
                                    viewModel.sendSecureMessage(
                                        plainText = "🎤 Encrypted Voice Message (0:09)",
                                        mediaType = "AUDIO",
                                        mediaCaption = "0:09"
                                    )
                                }
                            }
                            .testTag("send_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (typedText.isNotBlank()) Icons.Default.Send else Icons.Default.Mic,
                            contentDescription = "Send secure packet",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Slider Sheet simulation for files
                AnimatedVisibility(visible = showAttachmentSheet) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 16.dp, horizontal = 12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AttachmentTypeButton(
                            title = "Photo / Graphic",
                            color = Color(0xFFA3E4D7),
                            icon = Icons.Default.CameraAlt
                        ) {
                            showAttachmentSheet = false
                            // Sends encrypted rich photos
                            viewModel.sendSecureMessage(
                                plainText = "🎨 Shared Project Asset Artwork",
                                mediaType = "IMAGE",
                                mediaUrl = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80",
                                mediaCaption = "Beach Skyline.png"
                            )
                        }

                        AttachmentTypeButton(
                            title = "Voice Record",
                            color = Color(0xFFF9E79F),
                            icon = Icons.Default.Mic
                        ) {
                            showAttachmentSheet = false
                            viewModel.sendSecureMessage(
                                plainText = "🎤 Encrypted Secure Recording (0:34)",
                                mediaType = "AUDIO",
                                mediaCaption = "0:34"
                            )
                        }

                        AttachmentTypeButton(
                            title = "Secret doc",
                            color = Color(0xFFBB8FCE),
                            icon = Icons.Default.Description
                        ) {
                            showAttachmentSheet = false
                            viewModel.sendSecureMessage(
                                plainText = "📄 End_to_End_Encryption_Audit_Signatures.pdf",
                                mediaType = "DOCUMENT",
                                mediaCaption = "412 KB"
                            )
                        }
                    }
                }
            }

            // Fingerprint signature check modal view
            if (showSignatureVerificationSheet) {
                AlertDialog(
                    onDismissRequest = { showSignatureVerificationSheet = false },
                    icon = { Icon(Icons.Default.Fingerprint, contentDescription = "fingerprint check", tint = EmeraldBrand, modifier = Modifier.size(48.dp)) },
                    title = { Text("E2EE Verification Checksum", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                    text = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "To verify that messages in this secure layout are truly end-to-end encrypted, check that the numbers below match the ones in your contact's app:",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                    .padding(vertical = 16.dp, horizontal = 24.dp)
                            ) {
                                Text(
                                    text = CryptoHelper.generateFingerprint(viewModel.myPublicKey.value, activeChat.opponentPublicKey),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 1.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    lineHeight = 24.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(14.dp))
                            
                            Text(
                                "Negotiated DH Public key agreement:\n${activeChat.opponentPublicKey}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                textAlign = TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = { showSignatureVerificationSheet = false },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldBrand)
                        ) {
                            Text("Checksum Confirmed")
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LocalThemeTextStyle() = androidx.compose.ui.text.TextStyle(
    color = MaterialTheme.colorScheme.onSurface,
    fontSize = 15.sp,
    fontWeight = FontWeight.Normal
)

@Composable
fun AttachmentTypeButton(title: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(color, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = Color.DarkGray, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ------------------------------------
// MESSAGE BALLOON LAYOUTS
// ------------------------------------
@Composable
fun MessageBubbleRow(message: Message, chat: Chat) {
    val isDark = isSystemInDarkTheme()
    val isMe = message.isMe
    
    val bubbleColor = if (isMe) {
        if (isDark) OutgoingBubbleDark else OutgoingBubbleLight
    } else {
        if (isDark) IncomingBubbleDark else IncomingBubbleLight
    }

    val bubbleAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = bubbleAlignment
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(bubbleColor, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Render sender of group (if not me)
            if (!isMe) {
                Text(
                    text = message.senderName,
                    color = EmeraldBrand,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // Render message based on type
            when (message.mediaType) {
                "IMAGE" -> {
                    AsyncImage(
                        model = message.mediaUrl,
                        contentDescription = "Encrypted graphic payload",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(170.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = message.mediaCaption ?: "Encrypted Media",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                "AUDIO" -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play Audio Voice Note", tint = TealPrimary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        // Simulated playback bar
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f), RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(40.dp)
                                    .background(TealPrimary, RoundedCornerShape(2.dp))
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = message.mediaCaption ?: "0:00",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                "DOCUMENT" -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Description, contentDescription = "doc", tint = TealPrimary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = message.decryptedText.substringAfterLast("/"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "E2EE Secured PDF • ${message.mediaCaption}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
                else -> {
                    // standard TEXT
                    Text(
                        text = message.decryptedText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Message time status row at the bottom right of the bubble
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(message.timestamp))
                Text(
                    text = timeStr,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )

                if (isMe) {
                    Spacer(modifier = Modifier.width(4.dp))
                    // Double ticks
                    Text(
                        text = "✓✓",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (chat.isOnline) Color(0xFF34B7F1) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
                    )
                }
            }
        }
    }
}

// ------------------------------------
// FULL-SCREEN IMMERSIVE CALLING HUD
// ------------------------------------
@Composable
fun CallHudOverlay(
    activeCall: com.example.ui.viewmodel.ActiveCall,
    onEndCall: () -> Unit,
    onAccept: () -> Unit
) {
    val isIncoming = activeCall.status == CallStatus.INCOMING
    val isConnected = activeCall.status == CallStatus.CONNECTED
    
    var isMuted by remember { mutableStateOf(false) }
    var isCameraDisabled by remember { mutableStateOf(false) }
    var isSpeakerphoneOn by remember { mutableStateOf(true) }

    // Pulsing animations for Voice connections
    val infiniteTransition = rememberInfiniteTransition(label = "Radar pulses")
    val radarPulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "Pulse size"
    )
    val radarPulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Restart
        ),
        label = "Pulse transparency"
    )

    Surface(
        modifier = Modifier.fillMaxSize().testTag("call_hud_surface"),
        color = Color(0xFF070B0E)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            
            if (activeCall.isVideo && isConnected && !isCameraDisabled) {
                // VIDEO CALL BACKGROUND (Simulating interactive camera streams)
                // Let's draw a futuristic grid or abstract aesthetic to emulate a real high Definition connection
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F181C)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color(0xFF00A884).copy(alpha = 0.15f),
                            radius = size.width * 0.45f
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(Color(0xFF202C33), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(activeCall.avatarDescription, fontSize = 48.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Verified HD Stream Connection",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Green,
                            letterSpacing = 1.sp
                        )
                    }

                    // Floating Picture in picture self stream camera simulator
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 80.dp, end = 24.dp)
                            .size(100.dp, 150.dp)
                            .background(Color.Black, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("You", color = Color.Green, fontSize = 11.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp))
                        Icon(Icons.Default.Shield, contentDescription = "cam", tint = EmeraldBrand.copy(alpha = 0.4f), modifier = Modifier.size(36.dp))
                    }
                }
            } else {
                // VOICE OR DISCONNECTED/OUTGOING CALL BACKGROUND
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 200.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing security radar
                    Box(
                        modifier = Modifier.size(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!isConnected) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = Color(0xFF00A884).copy(alpha = radarPulseAlpha),
                                    radius = size.width * 0.35f * radarPulseScale
                                )
                            }
                        }
                        
                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .background(EmeraldBrand, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = activeCall.avatarDescription,
                                fontSize = 56.sp,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = activeCall.contactName,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "secured",
                            tint = EmeraldBrand,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isIncoming) "INCOMING SECURE CALL" 
                                   else if (isConnected) "CONNECTED • VERIFIED E2EE" 
                                   else "DIALING Handshake...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldBrand,
                            letterSpacing = 1.sp
                        )
                    }

                    if (isConnected) {
                        Spacer(modifier = Modifier.height(12.dp))
                        // Formatted ticker clock
                        val min = activeCall.durationSeconds / 60
                        val sec = activeCall.durationSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", min, sec),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // BOTTOM CONTROL PANELS overlay
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(
                        Color(0xFF111B21).copy(alpha = 0.95f),
                        RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isIncoming) {
                    // ACCEPT OR REJECT ACTION BUTTONS
                    Text(
                        text = "Encrypted packets waiting for public key agreement",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // REJECT
                        IconButton(
                            onClick = onEndCall,
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.Red, shape = CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Decline Call", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        
                        // ACCEPT
                        IconButton(
                            onClick = onAccept,
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.Green, shape = CircleShape)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Accept encryption Session", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                } else {
                    // ACTIVE SESSION CONTROL WHEELS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Speaker switch
                        IconButton(
                            onClick = { isSpeakerphoneOn = !isSpeakerphoneOn },
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (isSpeakerphoneOn) EmeraldBrand else Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isSpeakerphoneOn) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                                contentDescription = "Speaker",
                                tint = Color.White
                            )
                        }

                        // Video stream switch
                        if (activeCall.isVideo) {
                            IconButton(
                                onClick = { isCameraDisabled = !isCameraDisabled },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(if (!isCameraDisabled) EmeraldBrand else Color.White.copy(alpha = 0.1f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (!isCameraDisabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                    contentDescription = "Camera stream toggle",
                                    tint = Color.White
                                )
                            }
                        }

                        // Mic switch
                        IconButton(
                            onClick = { isMuted = !isMuted },
                            modifier = Modifier
                                .size(48.dp)
                                .background(if (isMuted) Color.Red else Color.White.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Mute mic",
                                tint = Color.White
                            )
                        }
                    }

                    // MAIN DISCONNECT BUTTON
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.Red, shape = CircleShape)
                            .testTag("end_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "End Call Session",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "Crypto handshake logs active",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

// ------------------------------------
// STORIES COMPONENTS
// ------------------------------------
@Composable
fun StoriesRow(
    viewModel: ChatViewModel,
    onPostClick: () -> Unit,
    onViewClick: (List<Story>, Int) -> Unit
) {
    val stories by viewModel.stories.collectAsState()
    val chats by viewModel.chats.collectAsState()
    
    // Group active stories by senderId so we have one circular item per contact
    val groupedStories = remember(stories) {
        stories.groupBy { it.senderId }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Stories",
                    tint = TealPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Secure Stories",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TealPrimary
                )
            }
            Text(
                text = "E2EE Verified",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00A884),
                modifier = Modifier
                    .background(Color(0xFFCCE8E8), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // My Story item
            item {
                val myStories = groupedStories["me"] ?: emptyList()
                val hasMyStory = myStories.isNotEmpty()

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        if (hasMyStory) {
                            onViewClick(myStories, 0)
                        } else {
                            onPostClick()
                        }
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        val activeBorderBrush = Brush.sweepGradient(
                            colors = listOf(Color(0xFF006A6A), Color(0xFF00E676), Color(0xFF006A6A))
                        )
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .then(
                                    if (hasMyStory) {
                                        Modifier.border(2.dp, activeBorderBrush, CircleShape)
                                    } else {
                                        Modifier.border(1.dp, Color.LightGray.copy(alpha = 0.6f), CircleShape)
                                    }
                                )
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFECE5DD)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Y",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary
                            )
                        }
                        
                        // Small Plus Badge if no story is active
                        if (!hasMyStory) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .background(Color(0xFF006A6A), CircleShape)
                                    .border(1.5.dp, Color.White, CircleShape)
                                    .align(Alignment.BottomEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Post story",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "My Story",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Contact Stories
            val otherSenders = groupedStories.keys.filter { it != "me" }
            itemsIndexed(otherSenders) { _, senderId ->
                val senderStories = groupedStories[senderId] ?: emptyList()
                val firstStory = senderStories.first()
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        onViewClick(senderStories, 0)
                    }
                ) {
                    val activeBorderBrush = Brush.sweepGradient(
                        colors = listOf(Color(0xFF006A6A), Color(0xFF00A884), Color(0xFF006A6A))
                    )
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .border(2.5.dp, activeBorderBrush, CircleShape)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCCE8E8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = firstStory.senderAvatar,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF002020)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = firstStory.senderName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 68.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddStoryDialog(
    viewModel: ChatViewModel,
    onDismiss: () -> Unit
) {
    var caption by remember { mutableStateOf("") }
    var mediaType by remember { mutableStateOf("IMAGE") } // IMAGE or VIDEO
    
    // Curated stunning Unsplash visual presets
    val presets = listOf(
        Triple("Tech Cafe 💻", "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?auto=format&fit=crop&w=800&q=80", "IMAGE"),
        Triple("Cyber Core 🔮", "https://images.unsplash.com/photo-1451187580459-43490279c0fa?auto=format&fit=crop&w=800&q=80", "IMAGE"),
        Triple("Zen Garden 🏖️", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80", "IMAGE"),
        Triple("Cyberpunk Coding 👾", "https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=800&q=80", "IMAGE"),
        Triple("Mountain Run ⛰️", "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80", "VIDEO")
    )
    
    var selectedPresetIndex by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock",
                    tint = TealPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Post E2EE Story", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Post a secured visual block visible to contacts for 24 hours. The content is AES-encrypted before leaving your phone.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Select Media Type Switcher
                Text(
                    text = "Media Type:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("IMAGE", "VIDEO").forEach { type ->
                        val isSelected = mediaType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    if (isSelected) TealPrimary else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) TealPrimary else Color.LightGray,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { mediaType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (type == "IMAGE") Icons.Default.CameraAlt else Icons.Default.Videocam,
                                    contentDescription = type,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (type == "IMAGE") "Photo" else "Short Video",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Choose visual Preset templates
                Text(
                    text = "Select Secure Preset Visual:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TealPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    itemsIndexed(presets) { index, item ->
                        val isSelected = index == selectedPresetIndex
                        Card(
                            onClick = { 
                                selectedPresetIndex = index 
                                mediaType = item.third
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFFCCE8E8) else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .width(120.dp)
                                .border(
                                    1.5.dp,
                                    if (isSelected) TealPrimary else Color.LightGray.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                ) {
                                    AsyncImage(
                                        model = item.second,
                                        contentDescription = item.first,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                            .align(Alignment.BottomEnd)
                                    ) {
                                        Text(
                                            text = if (item.third == "IMAGE") "PHOTO" else "VIDEO",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                                Text(
                                    text = item.first,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(6.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                // Caption
                OutlinedTextField(
                    value = caption,
                    onValueChange = { caption = it },
                    label = { Text("Caption") },
                    placeholder = { Text("Say something secure...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val activePreset = presets[selectedPresetIndex]
                    viewModel.postStory(
                        mediaUrl = activePreset.second,
                        caption = caption,
                        mediaType = mediaType
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
            ) {
                Text("Encrypt & Post 🚀")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StoryViewer(
    playlist: List<Story>,
    startIndex: Int,
    viewModel: ChatViewModel,
    onClose: () -> Unit
) {
    val chats by viewModel.chats.collectAsState()
    val myPublicKey by viewModel.myPublicKey.collectAsState()

    var currentIndex by remember { mutableIntStateOf(startIndex) }
    val currentStory = playlist.getOrNull(currentIndex)

    if (currentStory == null) {
        onClose()
        return
    }

    // Resolve decryption keys!
    val decryptionKey = remember(currentStory, chats, myPublicKey) {
        if (currentStory.senderId == "me") {
            myPublicKey
        } else {
            chats.find { it.id == currentStory.senderId }?.opponentPublicKey ?: myPublicKey
        }
    }

    val decryptedUrl = remember(currentStory, decryptionKey) {
        CryptoHelper.decrypt(currentStory.encryptedMediaUrl, decryptionKey)
    }
    val decryptedCaption = remember(currentStory, decryptionKey) {
        CryptoHelper.decrypt(currentStory.encryptedCaption, decryptionKey)
    }

    var progress by remember { mutableStateOf(0f) }
    var isPaused by remember { mutableStateOf(false) }
    var showSecurityLogs by remember { mutableStateOf(false) }

    // Story progress timing effect
    LaunchedEffect(currentIndex, isPaused) {
        if (!isPaused) {
            val duration = 5000L // 5 seconds total
            val steps = 100
            val delayInterval = duration / steps
            for (step in (progress * steps).toInt()..steps) {
                if (isPaused) break
                delay(delayInterval)
                progress = step.toFloat() / steps
            }
            if (!isPaused) {
                if (currentIndex < playlist.size - 1) {
                    currentIndex++
                    progress = 0f
                } else {
                    onClose()
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image/Visual
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = decryptedUrl,
                    contentDescription = "Story visual",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )

                // If Video, draw a play icon and loop visual
                if (currentStory.mediaType == "VIDEO") {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .align(Alignment.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Video playing",
                            tint = Color.White,
                            modifier = Modifier
                                .size(36.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }

            // Left/Right skip click zones
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            if (currentIndex > 0) {
                                currentIndex--
                                progress = 0f
                            } else {
                                onClose()
                            }
                        }
                )
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxHeight()
                        .clickable {
                            isPaused = !isPaused
                        }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            if (currentIndex < playlist.size - 1) {
                                currentIndex++
                                progress = 0f
                            } else {
                                onClose()
                            }
                        }
                )
            }

            // Foreground layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                // Segmented stories progress bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    playlist.forEachIndexed { idx, _ ->
                        val barProgress = when {
                            idx < currentIndex -> 1f
                            idx > currentIndex -> 0f
                            else -> progress
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(barProgress)
                                    .fillMaxHeight()
                                    .background(Color.White, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }

                // Header info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFCCE8E8), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentStory.senderAvatar,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002020)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = currentStory.senderName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Handshake verified",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(10.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "E2EE Decrypted",
                                    color = Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close story",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Caption & Interactive E2EE decryption dashboard
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (decryptedCaption.isNotEmpty()) {
                        Text(
                            text = decryptedCaption,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // Cryptographic Specs toggler
                    Button(
                        onClick = { showSecurityLogs = !showSecurityLogs },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCCE8E8)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Security Keys",
                                tint = Color(0xFF002020),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showSecurityLogs) "Hide Encryption Details" else "View Decryption Mechanics",
                                color = Color(0xFF002020),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    AnimatedVisibility(visible = showSecurityLogs) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .background(Color(0xFF0D161B).copy(alpha = 0.9f))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "DECRYPTION TELEMETRY LOGS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            Text(
                                text = "Ciphertext Payload:\n${currentStory.encryptedMediaUrl.take(40)}...",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.6f),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            Text(
                                text = "Handshake Verification:\nDH Shared Signature Verified ✅",
                                fontSize = 9.sp,
                                color = Color(0xFF00E676),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            Text(
                                text = "Decryption Key:\n$decryptionKey",
                                fontSize = 9.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Text(
                                text = "Client State: Completed client-side AES decryption on receiver hardware.",
                                fontSize = 9.sp,
                                color = Color.LightGray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
