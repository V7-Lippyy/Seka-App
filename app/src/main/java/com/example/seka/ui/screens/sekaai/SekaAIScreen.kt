package com.example.seka.ui.screens.sekaai

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SekaAIScreen(
    navController: NavController,
    viewModel: SekaAIViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    // Auto-scroll ke bawah saat ada pesan baru
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SEKA AI",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "Kembali",
                            tint = accentColor
                        )
                    }
                },
                actions = {
                    // Tombol untuk beralih bahasa
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleLanguageMode() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Language,
                                contentDescription = "Ganti Bahasa",
                                tint = accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Label bahasa saat ini
                        Text(
                            text = if (uiState.languageMode == LanguageMode.INDONESIAN) "ID" else "EN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Tombol clear chat
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(
                            Icons.Rounded.DeleteSweep,
                            contentDescription = "Bersihkan Chat",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = mainBackground
                )
            )
        },
        bottomBar = {
            ChatInput(
                value = uiState.currentInput,
                onValueChange = { viewModel.updateCurrentInput(it) },
                onSend = {
                    viewModel.sendMessage()
                    scope.launch {
                        // Scroll ke bawah setelah mengirim pesan
                        listState.animateScrollToItem(uiState.messages.size)
                    }
                },
                isLoading = uiState.isLoading,
                focusRequester = focusRequester
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(mainBackground)
        ) {
            if (uiState.messages.isEmpty() && !uiState.isLoading) {
                // Empty state
                EmptyAIState(
                    onSuggestionClick = { suggestion ->
                        viewModel.updateCurrentInput(suggestion)
                        viewModel.sendMessage()
                    }
                )
            } else {
                // Message list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    state = listState,
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.messages) { message ->
                        MessageItem(
                            message = message,
                            onSpeakClick = { viewModel.speakMessage(message) },
                            isSpeaking = uiState.isSpeaking
                        )
                    }

                    if (uiState.isLoading) {
                        item {
                            TypingIndicator()
                        }
                    }

                    // Space at the bottom to prevent last message from being hidden by keyboard
                    item {
                        Spacer(modifier = Modifier.height(60.dp))
                    }
                }
            }

            // Error message
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ErrorMessage(
                    errorMessage = uiState.error ?: "",
                    onDismiss = { viewModel.clearError() },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyAIState(
    onSuggestionClick: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1000, easing = LinearEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulse"
            )

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SmartToy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(60.dp)
                        .scale(scale)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Halo, saya SEKA AI!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Silakan tanyakan sesuatu pada saya.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Beberapa contoh pertanyaan yang dapat diklik
            SuggestionChips(onSuggestionClick = onSuggestionClick)
        }
    }
}

@Composable
fun SuggestionChips(
    onSuggestionClick: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Coba tanyakan:",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(4.dp))

        val suggestions = listOf(
            "Apa yang bisa kamu lakukan?",
            "Siapa developer aplikasi ini?",
            "Bagaimana cara membuat kue?",
            "Jelaskan tentang energi terbarukan"
        )

        suggestions.forEach { suggestion ->
            SuggestionChip(
                onClick = { onSuggestionClick(suggestion) },
                label = { Text(suggestion) },
                shape = MaterialTheme.shapes.medium
            )
        }
    }
}

@Composable
fun ErrorMessage(
    errorMessage: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            }

            TextButton(onClick = onDismiss) {
                Text(
                    "Tutup",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MessageItem(
    message: Message,
    onSpeakClick: () -> Unit,
    isSpeaking: Boolean
) {
    val isFromUser = message.isFromUser
    val backgroundColor = if (isFromUser)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.secondaryContainer
    val textColor = if (isFromUser)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isFromUser) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SmartToy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(
            horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start,
            modifier = Modifier.weight(1f, false)
        ) {
            Surface(
                color = backgroundColor,
                shape = RoundedCornerShape(
                    topStart = if (isFromUser) 16.dp else 4.dp,
                    topEnd = if (isFromUser) 4.dp else 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                shadowElevation = 1.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = message.content,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    if (!isFromUser) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            val speakIconTint = if (isSpeaking)
                                MaterialTheme.colorScheme.primary
                            else
                                textColor.copy(alpha = 0.7f)

                            IconButton(
                                onClick = onSpeakClick,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking)
                                        Icons.Filled.VolumeUp
                                    else
                                        Icons.Outlined.VolumeUp,
                                    contentDescription = "Dengarkan",
                                    tint = speakIconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = timeFormatter.format(message.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        if (isFromUser) {
            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isLoading: Boolean,
    focusRequester: FocusRequester
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Text input field
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        "Ketikkan pesan...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { onSend() }
                ),
                shape = RoundedCornerShape(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Send button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (value.isNotBlank() && !isLoading)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(8.dp)
            ) {
                IconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank() && !isLoading,
                    modifier = Modifier.size(48.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Kirim",
                            tint = if (value.isNotBlank())
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .padding(start = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SmartToy,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Animating dots
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(3) { index ->
                val infiniteTransition = rememberInfiniteTransition(label = "dot$index")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = LinearEasing, delayMillis = index * 150),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot$index"
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(scale)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSecondaryContainer)
                )
            }
        }
    }
}