package com.example.seka.ui.screens.note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    navController: NavController,
    noteId: Long,
    viewModel: NoteDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Colors
    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            navController.navigateUp()
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            // Handle error, e.g., show a snackbar
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (noteId == -1L) "Tambah Catatan" else "Edit Catatan",
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
                    IconButton(onClick = { viewModel.saveNote() }) {
                        Icon(
                            imageVector = Icons.Rounded.Save,
                            contentDescription = "Simpan",
                            tint = accentColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = mainBackground
                )
            )
        },
        containerColor = mainBackground
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = accentColor)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Header Card - Note Details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Detail Catatan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Judul
                        OutlinedTextField(
                            value = uiState.title,
                            onValueChange = { viewModel.updateTitle(it) },
                            label = { Text("Judul") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Title,
                                    contentDescription = null,
                                    tint = accentColor
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Content Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Isi Catatan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Isi Catatan
                        OutlinedTextField(
                            value = uiState.content,
                            onValueChange = { viewModel.updateContent(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 200.dp),
                            shape = MaterialTheme.shapes.medium,
                            maxLines = 15,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.saveNote() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Icon(Icons.Rounded.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Simpan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}