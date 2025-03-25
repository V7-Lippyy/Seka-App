package com.example.seka.ui.screens.todo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.seka.data.local.entity.TodoItem
import com.example.seka.data.local.entity.TodoUrgency
import com.example.seka.ui.navigation.Screen
import com.example.seka.ui.theme.UrgencyHigh
import com.example.seka.ui.theme.UrgencyLow
import com.example.seka.ui.theme.UrgencyMedium
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    navController: NavController,
    viewModel: TodoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "To Do List",
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
                    AnimatedVisibility(
                        visible = isSearchExpanded,
                        enter = fadeIn() + slideInVertically(),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = {
                                searchQuery = it
                                if (it.isEmpty()) {
                                    viewModel.loadTodos()
                                }
                            },
                            onSearch = {
                                viewModel.searchTodos(searchQuery)
                                isSearchExpanded = false
                            },
                            onClose = {
                                isSearchExpanded = false
                                searchQuery = ""
                                viewModel.loadTodos()
                            },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        )
                    }

                    AnimatedVisibility(
                        visible = !isSearchExpanded,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Row {
                            IconButton(onClick = { isSearchExpanded = true }) {
                                Icon(
                                    Icons.Rounded.Search,
                                    contentDescription = "Cari",
                                    tint = accentColor
                                )
                            }

                            IconButton(onClick = { showFilterDialog = true }) {
                                Icon(
                                    Icons.Rounded.FilterList,
                                    contentDescription = "Filter",
                                    tint = accentColor
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = mainBackground
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.TodoDetail.createRoute()) },
                containerColor = accentColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Tambah To Do",
                    modifier = Modifier.size(28.dp)
                )
            }
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
                    .padding(horizontal = 16.dp)
            ) {
                // Tambahkan statistik
                TodoStatistics(todos = uiState.todos)

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chip (if selected)
                if (uiState.selectedUrgency != null) {
                    Spacer(modifier = Modifier.height(8.dp))

                    FilterChip(
                        selected = true,
                        onClick = { viewModel.filterByUrgency(null) },
                        label = {
                            Text(
                                "Urgensi: ${uiState.selectedUrgency?.name ?: ""}",
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Circle,
                                contentDescription = null,
                                tint = when (uiState.selectedUrgency) {
                                    TodoUrgency.HIGH -> UrgencyHigh
                                    TodoUrgency.MEDIUM -> UrgencyMedium
                                    TodoUrgency.LOW -> UrgencyLow
                                    null -> MaterialTheme.colorScheme.primary
                                },
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = "Hapus filter",
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else if (uiState.todos.isEmpty()) {
                    EmptyTodoState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(uiState.todos) { index, todo ->
                            TodoItem(
                                todo = todo,
                                onToggleComplete = { viewModel.toggleTodoCompletion(todo) },
                                onDelete = { viewModel.deleteTodo(todo) },
                                onEdit = { navController.navigate(Screen.TodoDetail.createRoute(todo.id)) }
                            )
                            if (index < uiState.todos.size - 1) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                        // Add some space at the bottom for FAB
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            onDismiss = { showFilterDialog = false },
            onFilterSelected = { urgency ->
                viewModel.filterByUrgency(urgency)
                showFilterDialog = false
            },
            onShowAll = {
                viewModel.filterByUrgency(null)
                showFilterDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .padding(vertical = 8.dp)
            .height(56.dp),
        placeholder = { Text("Cari to do...") },
        leadingIcon = {
            Icon(
                Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Tutup",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch() }),
        shape = MaterialTheme.shapes.medium,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
    )
}

@Composable
fun TodoStatistics(todos: List<TodoItem>) {
    val highCount = todos.count { it.urgency == TodoUrgency.HIGH }
    val mediumCount = todos.count { it.urgency == TodoUrgency.MEDIUM }
    val lowCount = todos.count { it.urgency == TodoUrgency.LOW }
    val completedCount = todos.count { it.isCompleted }
    val totalCount = todos.size

    val completionPercentage = if (totalCount > 0) {
        (completedCount.toFloat() / totalCount * 100).roundToInt()
    } else {
        0
    }

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
                text = "Ringkasan Tugas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Indicator
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$completionPercentage% Selesai",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Statistics Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    count = highCount,
                    label = "Tinggi",
                    color = UrgencyHigh
                )

                StatItem(
                    count = mediumCount,
                    label = "Sedang",
                    color = UrgencyMedium
                )

                StatItem(
                    count = lowCount,
                    label = "Rendah",
                    color = UrgencyLow
                )

                StatItem(
                    count = completedCount,
                    label = "Selesai",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun StatItem(
    count: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = color.copy(alpha = 0.2f),
                    shape = MaterialTheme.shapes.medium
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmptyTodoState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Daftar tugas kosong",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tambahkan tugas baru dengan menekan tombol +",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onFilterSelected: (TodoUrgency) -> Unit,
    onShowAll: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Filter Berdasarkan Urgensi",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                UrgencyFilterOption(
                    text = "Tinggi",
                    color = UrgencyHigh,
                    onClick = { onFilterSelected(TodoUrgency.HIGH) }
                )

                UrgencyFilterOption(
                    text = "Sedang",
                    color = UrgencyMedium,
                    onClick = { onFilterSelected(TodoUrgency.MEDIUM) }
                )

                UrgencyFilterOption(
                    text = "Rendah",
                    color = UrgencyLow,
                    onClick = { onFilterSelected(TodoUrgency.LOW) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onShowAll,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Tampilkan Semua")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun UrgencyFilterOption(
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(MaterialTheme.shapes.small)
                .background(color = color)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TodoItem(
    todo: TodoItem,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val urgencyColor = when (todo.urgency) {
        TodoUrgency.HIGH -> UrgencyHigh
        TodoUrgency.MEDIUM -> UrgencyMedium
        TodoUrgency.LOW -> UrgencyLow
    }

    val isCompleted = todo.isCompleted

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(urgencyColor)
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggleComplete() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.outline
                    )
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text(
                        text = todo.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                        color = if (isCompleted)
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        else
                            MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = dateFormatter.format(todo.date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }

                    if (todo.content.isNotBlank()) {
                        Text(
                            text = todo.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isCompleted) 0.5f else 0.8f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}