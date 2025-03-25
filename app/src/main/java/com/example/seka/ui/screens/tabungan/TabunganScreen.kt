package com.example.seka.ui.screens.tabungan

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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.seka.data.local.entity.TabunganItem
import com.example.seka.ui.navigation.Screen
import com.example.seka.util.PDFGenerator
import com.itextpdf.text.DocumentException
import java.io.IOException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.seka.util.FileUtils
import java.io.File
import kotlin.math.ceil
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabunganScreen(
    navController: NavController,
    viewModel: TabunganViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tabungan",
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
                                    viewModel.loadAllTabungan()
                                }
                            },
                            onSearch = {
                                viewModel.searchTabungan(searchQuery)
                                isSearchExpanded = false
                            },
                            onClose = {
                                isSearchExpanded = false
                                searchQuery = ""
                                viewModel.loadAllTabungan()
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

                            IconButton(onClick = { generatePDF(context, uiState.tabunganItems) }) {
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
                onClick = { navController.navigate(Screen.TabunganDetail.createRoute()) }
            ) {
                Icon(
                    Icons.Rounded.Add,
                    contentDescription = "Tambah Tabungan",
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

                // Filter Chip (if selected)
                if (uiState.selectedKategori != null) {
                    FilterChip(
                        selected = true,
                        onClick = { viewModel.filterByKategori(null) },
                        label = {
                            Text(
                                "Kategori: ${uiState.selectedKategori}",
                                fontWeight = FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Category,
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
                } else if (uiState.tabunganItems.isEmpty()) {
                    EmptyTabunganState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        itemsIndexed(uiState.tabunganItems) { index, tabungan ->
                            TabunganCard(
                                tabungan = tabungan,
                                onDelete = { viewModel.deleteTabungan(tabungan) },
                                onEdit = { navController.navigate(Screen.TabunganDetail.createRoute(tabungan.id)) },
                                onTambahTabungan = { amount -> viewModel.updateTabungan(tabungan, amount) }
                            )

                            if (index < uiState.tabunganItems.size - 1) {
                                Spacer(modifier = Modifier.height(16.dp))
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
        val categoryList = uiState.tabunganItems
            .map { it.kategori }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()

        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = {
                Text(
                    "Filter berdasarkan Kategori",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                if (categoryList.isEmpty()) {
                    Text("Tidak ada kategori tersedia")
                } else {
                    Column {
                        categoryList.forEach { kategori ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp)
                                    .clickable {
                                        viewModel.filterByKategori(kategori)
                                        showFilterDialog = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Category,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = kategori,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.filterByKategori(null)
                        showFilterDialog = false
                    }
                ) {
                    Text("Tampilkan Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterDialog = false }) {
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
        placeholder = { Text("Cari tabungan...") },
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
fun EmptyTabunganState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Savings,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Belum ada tabungan",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tambahkan tabungan baru dengan menekan tombol +",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun TabunganCard(
    tabungan: TabunganItem,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onTambahTabungan: (Double) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val progressPercentage = remember(tabungan) {
        if (tabungan.hargaTarget > 0) {
            (tabungan.tabunganTerkumpul / tabungan.hargaTarget).coerceIn(0.0, 1.0)
        } else {
            0.0
        }
    }

    val estimasiHari = if (tabungan.hargaTarget > tabungan.tabunganTerkumpul && tabungan.cicilanJumlah > 0) {
        ceil((tabungan.hargaTarget - tabungan.tabunganTerkumpul) / tabungan.cicilanJumlah).toInt()
    } else {
        0
    }

    // Custom color based on progress
    val progressColor = when {
        progressPercentage >= 0.75 -> MaterialTheme.colorScheme.primary
        progressPercentage >= 0.5 -> MaterialTheme.colorScheme.tertiary
        progressPercentage >= 0.25 -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.error
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Tampilkan gambar jika tersedia
        if (tabungan.imagePath != null) {
            val file = File(tabungan.imagePath)
            if (file.exists()) {
                FileUtils.loadBitmapFromFile(tabungan.imagePath)?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Gambar ${tabungan.nama}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = 0.dp,
                                bottomEnd = 0.dp
                            )),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with Title and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tabungan.nama,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (tabungan.kategori.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Category,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = tabungan.kategori,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Row {
                    // Tambah Button
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AddCircle,
                            contentDescription = "Tambah",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Edit Button
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Delete Button
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

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(16.dp)
            ) {
                // Target & Terkumpul Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Target",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currencyFormat.format(tabungan.hargaTarget),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Terkumpul",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = currencyFormat.format(tabungan.tabunganTerkumpul),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = progressColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressPercentage.toFloat())
                            .fillMaxHeight()
                            .background(progressColor)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Text & Estimasi Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(progressPercentage * 100).toInt()}% terkumpul",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = progressColor
                    )

                    if (estimasiHari > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Schedule,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = "$estimasiHari hari lagi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Cicilan Info (if available)
            if (tabungan.cicilanJumlah > 0) {
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Payments,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Cicilan: ${currencyFormat.format(tabungan.cicilanJumlah)} per hari",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        // Format untuk angka dengan pemisah ribuan (titik sebagai pemisah ribuan untuk Indonesia)
        val decimalFormat = remember {
            val formatter = DecimalFormat("#,###")
            val symbols = formatter.decimalFormatSymbols
            symbols.groupingSeparator = '.'
            formatter.decimalFormatSymbols = symbols
            formatter
        }

        var jumlahTabunganText by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }
        val accentColor = MaterialTheme.colorScheme.primary

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    "Tambah Tabungan",
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                Column {
                    if (errorMessage.isNotBlank()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Rounded.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = jumlahTabunganText,
                        onValueChange = { inputText ->
                            // Hapus semua karakter non-digit
                            val cleanText = inputText.replace("[^0-9]".toRegex(), "")
                            if (cleanText.isEmpty()) {
                                jumlahTabunganText = ""
                            } else {
                                val number = cleanText.toDoubleOrNull() ?: 0.0
                                jumlahTabunganText = decimalFormat.format(number)
                            }
                        },
                        label = { Text("Jumlah (Rp)") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.MonetizationOn,
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
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            // Hapus semua karakter non-digit
                            val cleanText = jumlahTabunganText.replace("[^0-9]".toRegex(), "")
                            val amount = cleanText.toDoubleOrNull()
                            if (amount != null && amount > 0) {
                                onTambahTabungan(amount)
                                showAddDialog = false
                            } else {
                                errorMessage = "Masukkan jumlah yang valid"
                            }
                        } catch (e: Exception) {
                            errorMessage = "Format angka tidak valid"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor
                    )
                ) {
                    Icon(
                        Icons.Rounded.AddCircle,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tambah")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

fun generatePDF(context: Context, tabunganItems: List<TabunganItem>) {
    try {
        PDFGenerator.createTabunganPDF(context, tabunganItems)
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: DocumentException) {
        e.printStackTrace()
    }
}