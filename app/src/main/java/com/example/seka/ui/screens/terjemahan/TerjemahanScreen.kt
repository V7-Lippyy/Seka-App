package com.example.seka.ui.screens.terjemahan

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TerjemahanScreen(
    navController: NavController,
    viewModel: TerjemahanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current

    var sourceExpanded by remember { mutableStateOf(false) }
    var targetExpanded by remember { mutableStateOf(false) }

    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Terjemahan",
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
                    if (uiState.translatedText.isNotBlank()) {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(uiState.translatedText))
                        }) {
                            Icon(
                                Icons.Outlined.ContentCopy,
                                contentDescription = "Salin",
                                tint = accentColor
                            )
                        }
                    }

                    if (uiState.originalText.isNotBlank() || uiState.translatedText.isNotBlank()) {
                        IconButton(onClick = { viewModel.clearTranslation() }) {
                            Icon(
                                Icons.Rounded.ClearAll,
                                contentDescription = "Bersihkan",
                                tint = accentColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = mainBackground
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(mainBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Error Message
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Rounded.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = uiState.error ?: "",
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { viewModel.clearError() },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Rounded.Close,
                                    contentDescription = "Tutup",
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Language Selection Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Pilih Bahasa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Language Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Source language
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Dari",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                ExposedDropdownMenuBox(
                                    expanded = sourceExpanded,
                                    onExpandedChange = { sourceExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = uiState.sourceLanguage,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            Icon(
                                                Icons.Filled.ArrowDropDown,
                                                contentDescription = "Pilih bahasa sumber"
                                            )
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium
                                    )

                                    ExposedDropdownMenu(
                                        expanded = sourceExpanded,
                                        onDismissRequest = { sourceExpanded = false }
                                    ) {
                                        viewModel.availableLanguages.forEach { language ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        language,
                                                        fontWeight = if (language == uiState.sourceLanguage)
                                                            FontWeight.Bold
                                                        else
                                                            FontWeight.Normal
                                                    )
                                                },
                                                onClick = {
                                                    viewModel.updateSourceLanguage(language)
                                                    sourceExpanded = false
                                                },
                                                trailingIcon = {
                                                    if (language == uiState.sourceLanguage) {
                                                        Icon(
                                                            Icons.Filled.Check,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Swap button
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { viewModel.swapLanguages() }
                                ) {
                                    Icon(
                                        Icons.Rounded.SwapHoriz,
                                        contentDescription = "Tukar Bahasa",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            // Target language
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ke",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                ExposedDropdownMenuBox(
                                    expanded = targetExpanded,
                                    onExpandedChange = { targetExpanded = it }
                                ) {
                                    OutlinedTextField(
                                        value = uiState.targetLanguage,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = {
                                            Icon(
                                                Icons.Filled.ArrowDropDown,
                                                contentDescription = "Pilih bahasa target"
                                            )
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium
                                    )

                                    ExposedDropdownMenu(
                                        expanded = targetExpanded,
                                        onDismissRequest = { targetExpanded = false }
                                    ) {
                                        viewModel.availableLanguages.forEach { language ->
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        language,
                                                        fontWeight = if (language == uiState.targetLanguage)
                                                            FontWeight.Bold
                                                        else
                                                            FontWeight.Normal
                                                    )
                                                },
                                                onClick = {
                                                    viewModel.updateTargetLanguage(language)
                                                    targetExpanded = false
                                                },
                                                trailingIcon = {
                                                    if (language == uiState.targetLanguage) {
                                                        Icon(
                                                            Icons.Filled.Check,
                                                            contentDescription = null,
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Input text
                Text(
                    text = "Teks ${uiState.sourceLanguage}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.originalText,
                    onValueChange = { viewModel.updateOriginalText(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp),
                    placeholder = {
                        Text(
                            "Masukkan teks di sini...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    maxLines = 8,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Translate Button
                Button(
                    onClick = { viewModel.translate() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = uiState.originalText.isNotBlank() && !uiState.isLoading,
                    shape = MaterialTheme.shapes.medium
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            "Menerjemahkan...",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Translate,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            "Terjemahkan",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Translation Result
                AnimatedVisibility(
                    visible = uiState.translatedText.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(32.dp))

                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Hasil Terjemahan (${uiState.targetLanguage})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = uiState.translatedText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Salin",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(uiState.translatedText))
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.ContentCopy,
                                            contentDescription = "Salin",
                                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // Empty state if no translation
                if (uiState.translatedText.isBlank() && !uiState.isLoading && uiState.originalText.isBlank()) {
                    Spacer(modifier = Modifier.height(32.dp))

                    EmptyTranslationState()
                }
            }
        }
    }
}

@Composable
fun EmptyTranslationState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "rotate")
        val rotation by infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "rotate"
        )

        Icon(
            imageVector = Icons.Outlined.Translate,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier
                .size(70.dp)
                .rotate(rotation)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Terjemahkan Dengan Mudah",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Masukkan teks pada kolom di atas dan pilih bahasa untuk mulai menerjemahkan",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}