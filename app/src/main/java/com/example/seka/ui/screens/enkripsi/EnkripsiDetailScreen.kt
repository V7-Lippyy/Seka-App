package com.example.seka.ui.screens.enkripsi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnkripsiDetailScreen(
    navController: NavController,
    enkripsiId: Long,
    viewModel: EnkripsiDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val scrollState = rememberScrollState()
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))

    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(uiState.title.ifEmpty { "Detail Enkripsi" })
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = accentColor
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                if (uiState.error != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = uiState.error!!,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                // Date and Code info
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Dibuat pada: ${dateFormatter.format(uiState.createdAt)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Key,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Kode Enkripsi: ${uiState.encryptionCode}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Original Text Section
                Text(
                    text = "Teks Asli",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = uiState.originalText,
                            style = MaterialTheme.typography.bodyLarge
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(uiState.originalText))
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.ContentCopy,
                                    contentDescription = "Salin",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text("Salin")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Encrypted Text Section
                Text(
                    text = "Teks Terenkripsi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = uiState.encryptedText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(uiState.encryptedText))
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.ContentCopy,
                                    contentDescription = "Salin",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text("Salin")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Verify section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Verifikasi Kode",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Periksa apakah pesan dapat didekripsi dengan kode yang benar. Masukkan kode untuk memverifikasi.",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { /* Verifikasi diimplementasikan di dialog */ },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text("Verifikasi Kode")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}