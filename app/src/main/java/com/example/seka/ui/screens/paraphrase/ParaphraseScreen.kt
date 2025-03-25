package com.example.seka.ui.screens.paraphrase

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
import androidx.compose.ui.graphics.Brush
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
fun ParaphraseScreen(
    navController: NavController,
    viewModel: ParaphraseViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val clipboardManager = LocalClipboardManager.current

    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Parafrase",
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
                    if (uiState.paraphrasedText.isNotBlank()) {
                        IconButton(onClick = {
                            clipboardManager.setText(AnnotatedString(uiState.paraphrasedText))
                        }) {
                            Icon(
                                Icons.Outlined.ContentCopy,
                                contentDescription = "Salin",
                                tint = accentColor
                            )
                        }
                    }

                    if (uiState.originalText.isNotBlank() || uiState.paraphrasedText.isNotBlank()) {
                        IconButton(onClick = { viewModel.clearParaphrase() }) {
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

                // Description Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Parafrase membantu mengubah kalimat dengan makna yang sama namun dengan kata-kata yang berbeda.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Input Section
                Text(
                    text = "Teks Asli",
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
                    maxLines = 10,
                    shape = MaterialTheme.shapes.medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Paraphrase Button
                Button(
                    onClick = { viewModel.generateParaphrase() },
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
                            "Memproses...",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    } else {
                        Icon(
                            Icons.Rounded.Autorenew,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            "Parafrase Teks",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Result Section
                AnimatedVisibility(
                    visible = uiState.paraphrasedText.isNotBlank(),
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

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Rounded.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Hasil Parafrase",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = uiState.paraphrasedText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
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
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                    )

                                    Spacer(modifier = Modifier.width(4.dp))

                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(uiState.paraphrasedText))
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.ContentCopy,
                                            contentDescription = "Salin",
                                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }

                // Empty state if no result
                if (uiState.paraphrasedText.isBlank() && !uiState.isLoading && uiState.originalText.isBlank()) {
                    Spacer(modifier = Modifier.height(32.dp))

                    EmptyParaphraseState()
                }
            }
        }
    }
}

@Composable
fun EmptyParaphraseState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "rotate")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(5000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotate"
        )

        Icon(
            imageVector = Icons.Outlined.Autorenew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier
                .size(70.dp)
                .rotate(rotation)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ubah Kata-kata Anda",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Masukkan teks pada kolom di atas untuk mengubah cara penyampaian tanpa mengubah makna.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}