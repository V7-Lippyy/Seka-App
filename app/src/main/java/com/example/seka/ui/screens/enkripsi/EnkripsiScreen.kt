package com.example.seka.ui.screens.enkripsi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnkripsiScreen(
    navController: NavController,
    viewModel: EnkripsiViewModel = hiltViewModel()
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
                        "Enkripsi Pesan",
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
                    IconButton(onClick = { viewModel.clearFields() }) {
                        Icon(
                            imageVector = Icons.Rounded.ClearAll,
                            contentDescription = "Bersihkan",
                            tint = accentColor
                        )
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

                // Mode Selection
                ModeSelector(
                    currentMode = uiState.mode,
                    onModeChange = { viewModel.toggleMode() }
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                        val descriptionText = when (uiState.mode) {
                            EncryptionMode.ENCRYPT -> "Enkripsi pesan Anda dengan kode unik. Hanya orang dengan kode yang sama yang dapat mendekripsi pesan."
                            EncryptionMode.DECRYPT -> "Dekripsi pesan terenkripsi menggunakan kode yang sama dengan yang digunakan untuk enkripsi."
                        }

                        Text(
                            text = descriptionText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Encryption Code
                Text(
                    text = "Kode Enkripsi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = uiState.encryptionCode,
                    onValueChange = { viewModel.updateEncryptionCode(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Masukkan kode numerik (misalnya: 38985)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Input/Output section based on mode
                when (uiState.mode) {
                    EncryptionMode.ENCRYPT -> {
                        // Original Text Input
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
                                .heightIn(min = 120.dp),
                            placeholder = {
                                Text(
                                    "Masukkan teks yang ingin dienkripsi...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            maxLines = 8,
                            shape = MaterialTheme.shapes.medium
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Encrypt Button
                        Button(
                            onClick = { viewModel.processText() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            enabled = uiState.originalText.isNotBlank() &&
                                    uiState.encryptionCode.isNotBlank() &&
                                    !uiState.isLoading,
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
                                    Icons.Rounded.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    "Enkripsi",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Result Section
                        AnimatedVisibility(
                            visible = uiState.encryptedText.isNotBlank(),
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
                                    text = "Hasil Enkripsi",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )

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
                                            text = uiState.encryptedText,
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
                                            TextButton(
                                                onClick = {
                                                    clipboardManager.setText(
                                                        AnnotatedString(uiState.encryptedText)
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Outlined.ContentCopy,
                                                    contentDescription = "Salin",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    "Salin",
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    EncryptionMode.DECRYPT -> {
                        // Encrypted Text Input
                        Text(
                            text = "Teks Terenkripsi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = uiState.encryptedText,
                            onValueChange = { viewModel.updateEncryptedText(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            placeholder = {
                                Text(
                                    "Masukkan teks terenkripsi yang ingin didekripsi...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            },
                            maxLines = 8,
                            shape = MaterialTheme.shapes.medium
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Decrypt Button
                        Button(
                            onClick = { viewModel.processText() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            enabled = uiState.encryptedText.isNotBlank() &&
                                    uiState.encryptionCode.isNotBlank() &&
                                    !uiState.isLoading,
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
                                    Icons.Rounded.LockOpen,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    "Dekripsi",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Result Section
                        AnimatedVisibility(
                            visible = uiState.originalText.isNotBlank(),
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
                                    text = "Hasil Dekripsi",
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
                                            text = uiState.originalText,
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
                                            TextButton(
                                                onClick = {
                                                    clipboardManager.setText(
                                                        AnnotatedString(uiState.originalText)
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    Icons.Outlined.ContentCopy,
                                                    contentDescription = "Salin",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )

                                                Spacer(modifier = Modifier.width(4.dp))

                                                Text(
                                                    "Salin",
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ModeSelector(
    currentMode: EncryptionMode,
    onModeChange: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Encrypt Button
            Button(
                onClick = {
                    if (currentMode != EncryptionMode.ENCRYPT) onModeChange()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentMode == EncryptionMode.ENCRYPT)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (currentMode == EncryptionMode.ENCRYPT)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    Icons.Filled.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Enkripsi")
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Decrypt Button
            Button(
                onClick = {
                    if (currentMode != EncryptionMode.DECRYPT) onModeChange()
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (currentMode == EncryptionMode.DECRYPT)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (currentMode == EncryptionMode.DECRYPT)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    Icons.Filled.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Dekripsi")
            }
        }
    }
}