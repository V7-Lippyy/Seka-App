package com.example.seka.ui.screens.tabungan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.seka.util.FileUtils
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabunganDetailScreen(
    navController: NavController,
    tabunganId: Long,
    viewModel: TabunganDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
    var showDatePicker by remember { mutableStateOf(false) }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val context = LocalContext.current
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
    var hargaTargetText by remember { mutableStateOf(if (uiState.hargaTarget > 0) decimalFormat.format(uiState.hargaTarget.toInt()) else "") }
    var tabunganTerkumpulText by remember { mutableStateOf(if (uiState.tabunganTerkumpul > 0) decimalFormat.format(uiState.tabunganTerkumpul.toInt()) else "") }
    var cicilanJumlahText by remember { mutableStateOf(if (uiState.cicilanJumlah > 0) decimalFormat.format(uiState.cicilanJumlah.toInt()) else "") }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateImageUri(it) }
    }

    // Menu popup untuk pilihan gambar
    var showImagePickerMenu by remember { mutableStateOf(false) }

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
                        if (tabunganId == -1L) "Tambah Tabungan" else "Edit Tabungan",
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
                    IconButton(onClick = { viewModel.saveTabungan() }) {
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

                // Foto Barang Card
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
                            text = "Foto Barang",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Image picker section
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { showImagePickerMenu = true },
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.imagePath != null) {
                                // Tampilkan gambar jika tersedia
                                val file = File(uiState.imagePath!!)
                                if (file.exists()) {
                                    FileUtils.loadBitmapFromFile(uiState.imagePath!!)?.let { bitmap ->
                                        Image(
                                            bitmap = bitmap.asImageBitmap(),
                                            contentDescription = "Gambar barang",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            } else {
                                // Tampilkan placeholder jika tidak ada gambar
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AddAPhoto,
                                        contentDescription = null,
                                        tint = accentColor,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Tambahkan Gambar")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Informasi Barang Card
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
                            text = "Informasi Barang",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Nama Barang
                        OutlinedTextField(
                            value = uiState.nama,
                            onValueChange = { viewModel.updateNama(it) },
                            label = { Text("Nama Barang") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.ShoppingBag,
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

                        // Harga Target
                        OutlinedTextField(
                            value = hargaTargetText,
                            onValueChange = { inputText ->
                                // Hapus semua karakter non-digit
                                val cleanText = inputText.replace("[^0-9]".toRegex(), "")
                                if (cleanText.isEmpty()) {
                                    hargaTargetText = ""
                                    viewModel.updateHargaTarget("0")
                                } else {
                                    val number = cleanText.toDoubleOrNull() ?: 0.0
                                    hargaTargetText = decimalFormat.format(number)
                                    viewModel.updateHargaTarget(cleanText)
                                }
                            },
                            label = { Text("Harga Target (Rp)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.PriceCheck,
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

                Spacer(modifier = Modifier.height(4.dp))

                // Rencana Tabungan Card
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
                            text = "Rencana Tabungan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tabungan Terkumpul
                        OutlinedTextField(
                            value = tabunganTerkumpulText,
                            onValueChange = { inputText ->
                                // Hapus semua karakter non-digit
                                val cleanText = inputText.replace("[^0-9]".toRegex(), "")
                                if (cleanText.isEmpty()) {
                                    tabunganTerkumpulText = ""
                                    viewModel.updateTabunganTerkumpul("0")
                                } else {
                                    val number = cleanText.toDoubleOrNull() ?: 0.0
                                    tabunganTerkumpulText = decimalFormat.format(number)
                                    viewModel.updateTabunganTerkumpul(cleanText)
                                }
                            },
                            label = { Text("Tabungan Awal (Rp)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Payments,
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

                        // Cicilan Harian
                        OutlinedTextField(
                            value = cicilanJumlahText,
                            onValueChange = { inputText ->
                                // Hapus semua karakter non-digit
                                val cleanText = inputText.replace("[^0-9]".toRegex(), "")
                                if (cleanText.isEmpty()) {
                                    cicilanJumlahText = ""
                                    viewModel.updateCicilanJumlah("0")
                                } else {
                                    val number = cleanText.toDoubleOrNull() ?: 0.0
                                    cicilanJumlahText = decimalFormat.format(number)
                                    viewModel.updateCicilanJumlah(cleanText)
                                }
                            },
                            label = { Text("Jumlah Cicilan per Hari (Rp)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarToday,
                                    contentDescription = null,
                                    tint = accentColor
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        if (uiState.hargaTarget > 0 && uiState.cicilanJumlah > 0) {
                            Spacer(modifier = Modifier.height(4.dp))

                            // Estimasi Informasi
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Estimasi: ${uiState.estimasiHari} hari untuk mencapai target",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tanggal Target
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
                                imageVector = Icons.Outlined.Event,
                                contentDescription = null,
                                tint = if (uiState.targetDate != null) accentColor else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = if (uiState.targetDate != null) "Target Tanggal" else "Pilih Target Tanggal",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (uiState.targetDate != null)
                                        dateFormatter.format(uiState.targetDate!!)
                                    else
                                        "Opsional",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (uiState.targetDate != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline,
                                    fontWeight = if (uiState.targetDate != null) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        }

                        AnimatedVisibility(
                            visible = uiState.targetDate != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = { viewModel.updateTargetDate(null) },
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
                                    Text("Hapus Tanggal")
                                }
                            }
                        }
                    }
                }

                // Progress visualization if we're editing
                if (tabunganId != -1L && uiState.hargaTarget > 0) {
                    Spacer(modifier = Modifier.height(4.dp))

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
                                text = "Status Tabungan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val progress = (uiState.tabunganTerkumpul / uiState.hargaTarget)
                                .coerceIn(0.0, 1.0)

                            Text(
                                text = "${(progress * 100).toInt()}% Terkumpul",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = accentColor
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = progress.toFloat(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(12.dp)
                                    .clip(MaterialTheme.shapes.small),
                                color = accentColor,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Terkumpul: ${currencyFormat.format(uiState.tabunganTerkumpul)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Target: ${currencyFormat.format(uiState.hargaTarget)}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.saveTabungan() },
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

        if (showImagePickerMenu) {
            AlertDialog(
                onDismissRequest = { showImagePickerMenu = false },
                title = { Text("Pilih Sumber Gambar", fontWeight = FontWeight.SemiBold) },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                imagePickerLauncher.launch("image/*")
                                showImagePickerMenu = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Outlined.Image, contentDescription = null, tint = accentColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Galeri")
                        }

                        if (uiState.imagePath != null) {
                            TextButton(
                                onClick = {
                                    viewModel.updateImageUri(null)
                                    showImagePickerMenu = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hapus Gambar", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showImagePickerMenu = false }) {
                        Text("Batal")
                    }
                }
            )
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = uiState.targetDate?.time
                    ?: System.currentTimeMillis()
            )

            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                viewModel.updateTargetDate(Date(millis))
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