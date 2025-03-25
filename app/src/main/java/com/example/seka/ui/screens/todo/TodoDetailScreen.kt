package com.example.seka.ui.screens.todo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.seka.data.local.entity.TimeUnit
import com.example.seka.data.local.entity.TodoUrgency
import com.example.seka.ui.theme.UrgencyHigh
import com.example.seka.ui.theme.UrgencyLow
import com.example.seka.ui.theme.UrgencyMedium
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    navController: NavController,
    todoId: Long,
    viewModel: TodoDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    var showDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }
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
                        if (todoId == -1L) "Tambah To Do" else "Edit To Do",
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
                    IconButton(onClick = { viewModel.saveTodo() }) {
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

                // Header Card - Task Details
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
                            text = "Detail Tugas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Deskripsi
                        OutlinedTextField(
                            value = uiState.content,
                            onValueChange = { viewModel.updateContent(it) },
                            label = { Text("Deskripsi") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 120.dp),
                            shape = MaterialTheme.shapes.medium,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
                                    contentDescription = null,
                                    tint = accentColor
                                )
                            },
                            maxLines = 10,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dates Section Card
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
                            text = "Waktu",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tanggal awal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { showDatePicker = true }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarToday,
                                contentDescription = null,
                                tint = accentColor
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Tanggal Mulai",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dateFormatter.format(uiState.date),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tenggat waktu (opsional)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                .clickable { showDueDatePicker = true }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DateRange,
                                contentDescription = null,
                                tint = if (uiState.dueDate != null) accentColor else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = if (uiState.dueDate != null) "Tenggat" else "Tambahkan Tenggat",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (uiState.dueDate != null)
                                        dateFormatter.format(uiState.dueDate!!)
                                    else
                                        "Opsional",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (uiState.dueDate != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                                    fontWeight = if (uiState.dueDate != null) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = uiState.dueDate != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { viewModel.updateDueDate(null) },
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Hapus Tenggat")
                                    }
                                }

                                // Pengingat tenggat waktu
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .clip(MaterialTheme.shapes.medium)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                        .padding(8.dp)
                                ) {
                                    Checkbox(
                                        checked = uiState.dueDateReminder,
                                        onCheckedChange = { viewModel.toggleDueDateReminder() },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = accentColor,
                                            uncheckedColor = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                    Text(
                                        "Ingatkan saya H-",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))

                                    AnimatedVisibility(
                                        visible = uiState.dueDateReminder,
                                        enter = fadeIn(tween(200)),
                                        exit = fadeOut(tween(200))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            OutlinedTextField(
                                                value = uiState.dueDateReminderDays.toString(),
                                                onValueChange = { viewModel.updateDueDateReminderDays(it) },
                                                modifier = Modifier.width(64.dp),
                                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                singleLine = true,
                                                shape = MaterialTheme.shapes.small,
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = accentColor,
                                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                "hari",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Urgensi section
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
                            text = "Urgensi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            UrgencyButton(
                                text = "Tinggi",
                                color = UrgencyHigh,
                                isSelected = uiState.urgency == TodoUrgency.HIGH,
                                onClick = { viewModel.updateUrgency(TodoUrgency.HIGH) },
                                modifier = Modifier.weight(1f)
                            )

                            UrgencyButton(
                                text = "Sedang",
                                color = UrgencyMedium,
                                isSelected = uiState.urgency == TodoUrgency.MEDIUM,
                                onClick = { viewModel.updateUrgency(TodoUrgency.MEDIUM) },
                                modifier = Modifier.weight(1f)
                            )

                            UrgencyButton(
                                text = "Rendah",
                                color = UrgencyLow,
                                isSelected = uiState.urgency == TodoUrgency.LOW,
                                onClick = { viewModel.updateUrgency(TodoUrgency.LOW) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pengaturan tambahan
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
                            text = "Pengaturan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Checkbox untuk status selesai
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = uiState.isCompleted,
                                onCheckedChange = { viewModel.toggleCompletion() },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = accentColor,
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            Text(
                                "Tandai tugas selesai",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Pengingat harian
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = uiState.dailyReminder,
                                onCheckedChange = { viewModel.toggleDailyReminder() },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = accentColor,
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            Text(
                                "Ingatkan saya setiap hari",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Pengingat Interval (baru)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                        ) {
                            Checkbox(
                                checked = uiState.intervalReminder,
                                onCheckedChange = { viewModel.toggleIntervalReminder() },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = accentColor,
                                    uncheckedColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            Text(
                                "Ingatkan saya setiap interval waktu",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        // Interval settings
                        AnimatedVisibility(
                            visible = uiState.intervalReminder,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 36.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                                    .clip(MaterialTheme.shapes.medium)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    "Atur interval pengingat:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Input nilai interval
                                    OutlinedTextField(
                                        value = uiState.intervalValue.toString(),
                                        onValueChange = { viewModel.updateIntervalValue(it) },
                                        modifier = Modifier.width(80.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        shape = MaterialTheme.shapes.small,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = accentColor,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    // Pemilihan unit waktu
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable { viewModel.updateIntervalUnit(TimeUnit.SECONDS) }
                                        ) {
                                            RadioButton(
                                                selected = uiState.intervalUnit == TimeUnit.SECONDS,
                                                onClick = { viewModel.updateIntervalUnit(TimeUnit.SECONDS) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = accentColor
                                                )
                                            )
                                            Text("Detik", style = MaterialTheme.typography.bodyMedium)
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable { viewModel.updateIntervalUnit(TimeUnit.MINUTES) }
                                        ) {
                                            RadioButton(
                                                selected = uiState.intervalUnit == TimeUnit.MINUTES,
                                                onClick = { viewModel.updateIntervalUnit(TimeUnit.MINUTES) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = accentColor
                                                )
                                            )
                                            Text("Menit", style = MaterialTheme.typography.bodyMedium)
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clickable { viewModel.updateIntervalUnit(TimeUnit.HOURS) }
                                        ) {
                                            RadioButton(
                                                selected = uiState.intervalUnit == TimeUnit.HOURS,
                                                onClick = { viewModel.updateIntervalUnit(TimeUnit.HOURS) },
                                                colors = RadioButtonDefaults.colors(
                                                    selectedColor = accentColor
                                                )
                                            )
                                            Text("Jam", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.saveTodo() },
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

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Date Picker untuk tanggal mulai
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.date.time
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                viewModel.updateDate(Date(millis))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Batal")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Date Picker untuk tenggat waktu
        if (showDueDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.dueDate?.time ?: System.currentTimeMillis()
            )

            DatePickerDialog(
                onDismissRequest = { showDueDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                viewModel.updateDueDate(Date(millis))
                            }
                            showDueDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDueDatePicker = false }) {
                        Text("Batal")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
fun UrgencyButton(
    text: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else color
        ),
        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder,
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        ),
        modifier = modifier.height(44.dp)
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )
    }
}