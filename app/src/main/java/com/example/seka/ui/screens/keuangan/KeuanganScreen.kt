package com.example.seka.ui.screens.keuangan

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.seka.data.local.entity.KeuanganItem
import com.example.seka.data.local.entity.TransactionType
import com.example.seka.ui.navigation.Screen
import com.example.seka.ui.theme.ExpenseColor
import com.example.seka.ui.theme.IncomeColor
import com.example.seka.util.PDFGenerator
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeuanganScreen(
    navController: NavController,
    viewModel: KeuanganViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFilterTypeDialog by remember { mutableStateOf(false) }
    var showFilterKategoriDialog by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Keuangan",
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
                                    viewModel.loadAllTransactions()
                                }
                            },
                            onSearch = {
                                viewModel.searchTransactions(searchQuery)
                                isSearchExpanded = false
                            },
                            onClose = {
                                isSearchExpanded = false
                                searchQuery = ""
                                viewModel.loadAllTransactions()
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

                            IconButton(onClick = { showFilterTypeDialog = true }) {
                                Icon(
                                    Icons.Rounded.FilterList,
                                    contentDescription = "Filter Tipe",
                                    tint = accentColor
                                )
                            }

                            IconButton(onClick = { showFilterKategoriDialog = true }) {
                                Icon(
                                    Icons.Rounded.Category,
                                    contentDescription = "Filter Kategori",
                                    tint = accentColor
                                )
                            }

                            IconButton(
                                onClick = {
                                    generatePDF(context, uiState.transactions, uiState.totalIncome, uiState.totalExpense)
                                }
                            ) {
                                Icon(
                                    Icons.Rounded.PictureAsPdf,
                                    contentDescription = "Export PDF",
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
                onClick = { navController.navigate(Screen.KeuanganDetail.createRoute()) }
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Tambah Transaksi",
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
                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips
                if (uiState.selectedType != null || uiState.selectedKategori != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Type Filter Chip
                        uiState.selectedType?.let { type ->
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.filterByType(null) },
                                label = {
                                    Text(
                                        text = if (type == TransactionType.INCOME) "Pemasukan" else "Pengeluaran",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        if (type == TransactionType.INCOME)
                                            Icons.Rounded.ArrowUpward
                                        else
                                            Icons.Rounded.ArrowDownward,
                                        contentDescription = null,
                                        tint = if (type == TransactionType.INCOME)
                                            IncomeColor
                                        else
                                            ExpenseColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Rounded.Close,
                                        contentDescription = "Hapus filter",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }

                        // Category Filter Chip
                        uiState.selectedKategori?.let { kategori ->
                            FilterChip(
                                selected = true,
                                onClick = { viewModel.filterByKategori(null) },
                                label = {
                                    Text(
                                        "Kategori: $kategori",
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Rounded.Category,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        Icons.Rounded.Close,
                                        contentDescription = "Hapus filter",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Summary Cards
                SummaryCards(
                    totalIncome = uiState.totalIncome,
                    totalExpense = uiState.totalExpense,
                    totalBalance = uiState.totalBalance
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = accentColor)
                    }
                } else if (uiState.transactions.isEmpty()) {
                    EmptyTransactionState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(uiState.transactions) { index, transaction ->
                            TransactionCard(
                                transaction = transaction,
                                onDelete = { viewModel.deleteTransaction(transaction) },
                                onEdit = { navController.navigate(Screen.KeuanganDetail.createRoute(transaction.id)) }
                            )

                            if (index < uiState.transactions.size - 1) {
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

    if (showFilterTypeDialog) {
        AlertDialog(
            onDismissRequest = { showFilterTypeDialog = false },
            title = {
                Text(
                    "Filter berdasarkan Tipe",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column {
                    FilterOption(
                        text = "Pemasukan",
                        icon = Icons.Outlined.ArrowUpward,
                        color = IncomeColor,
                        onClick = {
                            viewModel.filterByType(TransactionType.INCOME)
                            showFilterTypeDialog = false
                        }
                    )

                    FilterOption(
                        text = "Pengeluaran",
                        icon = Icons.Outlined.ArrowDownward,
                        color = ExpenseColor,
                        onClick = {
                            viewModel.filterByType(TransactionType.EXPENSE)
                            showFilterTypeDialog = false
                        }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.filterByType(null)
                        showFilterTypeDialog = false
                    }
                ) {
                    Text("Tampilkan Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterTypeDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    if (showFilterKategoriDialog) {
        val kategoriList = uiState.transactions
            .map { it.kategori }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        AlertDialog(
            onDismissRequest = { showFilterKategoriDialog = false },
            title = {
                Text(
                    "Filter berdasarkan Kategori",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                if (kategoriList.isEmpty()) {
                    Text("Tidak ada kategori tersedia")
                } else {
                    Column {
                        kategoriList.forEach { kategori ->
                            FilterOption(
                                text = kategori,
                                icon = Icons.Outlined.Category,
                                color = MaterialTheme.colorScheme.primary,
                                onClick = {
                                    viewModel.filterByKategori(kategori)
                                    showFilterKategoriDialog = false
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.filterByKategori(null)
                        showFilterKategoriDialog = false
                    }
                ) {
                    Text("Tampilkan Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterKategoriDialog = false }) {
                    Text("Tutup")
                }
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
        placeholder = { Text("Cari transaksi...") },
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
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun FilterOption(text: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
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
fun EmptyTransactionState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountBalance,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Belum ada transaksi",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tambahkan transaksi baru dengan menekan tombol +",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun SummaryCards(
    totalIncome: Double,
    totalExpense: Double,
    totalBalance: Double
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Balance
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saldo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = currencyFormat.format(totalBalance),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (totalBalance >= 0) MaterialTheme.colorScheme.primary else ExpenseColor
                )
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Income & Expense
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Income Column
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(IncomeColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowUpward,
                                contentDescription = null,
                                tint = IncomeColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Pemasukan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currencyFormat.format(totalIncome),
                        style = MaterialTheme.typography.titleMedium,
                        color = IncomeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Expense Column
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(ExpenseColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowDownward,
                                contentDescription = null,
                                tint = ExpenseColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Pengeluaran",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currencyFormat.format(totalExpense),
                        style = MaterialTheme.typography.titleMedium,
                        color = ExpenseColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionCard(
    transaction: KeuanganItem,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val isIncome = transaction.tipe == TransactionType.INCOME
    val transactionColor = if (isIncome) IncomeColor else ExpenseColor

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header - Title and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.judul,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.DateRange,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = dateFormatter.format(transaction.tanggal),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }

                // Amount
                Text(
                    text = "${if (isIncome) "+" else "-"} ${currencyFormat.format(transaction.jumlah)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = transactionColor
                )
            }

            // Description (if available)
            if (transaction.deskripsi.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = transaction.deskripsi,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer - Category and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                if (transaction.kategori.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = transaction.kategori,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                // Transaction Type Indicator
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(transactionColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isIncome)
                                Icons.Rounded.ArrowUpward
                            else
                                Icons.Rounded.ArrowDownward,
                            contentDescription = null,
                            tint = transactionColor,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = if (isIncome) "Pemasukan" else "Pengeluaran",
                            style = MaterialTheme.typography.bodySmall,
                            color = transactionColor
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Action Buttons
                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

fun generatePDF(context: Context, transactions: List<KeuanganItem>, totalIncome: Double, totalExpense: Double) {
    PDFGenerator.createKeuanganPDF(context, transactions, totalIncome, totalExpense)
}