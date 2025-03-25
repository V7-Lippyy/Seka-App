package com.example.seka.ui.screens.keuangan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.seka.data.local.entity.TransactionType
import com.example.seka.ui.theme.ExpenseColor
import com.example.seka.ui.theme.IncomeColor
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KeuanganDetailScreen(
    navController: NavController,
    keuanganId: Long,
    viewModel: KeuanganDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    var showDatePicker by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Colors
    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    // Format untuk angka dengan pemisah ribuan (titik sebagai pemisah ribuan untuk Indonesia)
    val decimalFormat = remember {
        val formatter = DecimalFormat("#,###")
        val symbols = formatter.decimalFormatSymbols
        symbols.groupingSeparator = '.'
        formatter.decimalFormatSymbols = symbols
        formatter
    }

    // State untuk input nilai dengan format ribuan
    var jumlahText by remember {
        mutableStateOf(
            if (uiState.jumlah > 0) decimalFormat.format(uiState.jumlah.toInt()) else ""
        )
    }

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
                        if (keuanganId == -1L) "Tambah Transaksi" else "Edit Transaksi",
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
                    IconButton(onClick = { viewModel.saveKeuangan() }) {
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
                if (uiState.error != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
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
                                text = uiState.error!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // Tipe Transaksi Card
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
                            text = "Tipe Transaksi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TransactionTypeButton(
                                text = "Pemasukan",
                                color = IncomeColor,
                                icon = Icons.Rounded.ArrowUpward,
                                isSelected = uiState.tipe == TransactionType.INCOME,
                                onClick = { viewModel.updateTipe(TransactionType.INCOME) },
                                modifier = Modifier.weight(1f)
                            )

                            TransactionTypeButton(
                                text = "Pengeluaran",
                                color = ExpenseColor,
                                icon = Icons.Rounded.ArrowDownward,
                                isSelected = uiState.tipe == TransactionType.EXPENSE,
                                onClick = { viewModel.updateTipe(TransactionType.EXPENSE) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Informasi Transaksi Card
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
                            text = "Informasi Transaksi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tanggal Transaksi
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
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
                                    text = "Tanggal Transaksi",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = dateFormatter.format(uiState.tanggal),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Judul
                        OutlinedTextField(
                            value = uiState.judul,
                            onValueChange = { viewModel.updateJudul(it) },
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
                            value = uiState.deskripsi,
                            onValueChange = { viewModel.updateDeskripsi(it) },
                            label = { Text("Deskripsi") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            maxLines = 3,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Description,
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

                        // Jumlah dengan format
                        OutlinedTextField(
                            value = jumlahText,
                            onValueChange = { inputText ->
                                // Hapus semua karakter non-digit
                                val cleanText = inputText.replace("[^0-9]".toRegex(), "")
                                if (cleanText.isEmpty()) {
                                    jumlahText = ""
                                    viewModel.updateJumlah("0")
                                } else {
                                    val number = cleanText.toDoubleOrNull() ?: 0.0
                                    jumlahText = decimalFormat.format(number)
                                    viewModel.updateJumlah(cleanText)
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
                                    tint = if (uiState.tipe == TransactionType.INCOME) IncomeColor else ExpenseColor
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (uiState.tipe == TransactionType.INCOME) IncomeColor else ExpenseColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Kategori
                        OutlinedTextField(
                            value = uiState.kategori,
                            onValueChange = { viewModel.updateKategori(it) },
                            label = { Text("Kategori") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Category,
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

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.saveKeuangan() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.tipe == TransactionType.INCOME) IncomeColor else ExpenseColor
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Icon(
                        imageVector = if (uiState.tipe == TransactionType.INCOME)
                            Icons.Rounded.ArrowUpward
                        else
                            Icons.Rounded.ArrowDownward,
                        contentDescription = null
                    )
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

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.tanggal.time
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                viewModel.updateTanggal(Date(millis))
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
    }
}

@Composable
fun TransactionTypeButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontWeight = FontWeight.Medium
        )
    }
}