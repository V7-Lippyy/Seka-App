package com.example.seka.ui.screens.airminum

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.seka.data.local.entity.IntervalUnit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirMinumScreen(
    navController: NavController,
    viewModel: AirMinumViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showResetMingguanDialog by remember { mutableStateOf(false) }
    var showResetOptionsDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val mainBackground = MaterialTheme.colorScheme.surface
    val accentColor = MaterialTheme.colorScheme.primary

    // Add animation for progress
    val airMinumHariIni = uiState.airMinumHariIni
    val targetGelas = airMinumHariIni?.targetGelas ?: 8
    val jumlahGelas = airMinumHariIni?.jumlahGelas ?: 0
    val ukuranGelas = airMinumHariIni?.ukuranGelas ?: 250

    // Format waktu terakhir minum
    val lastDrinkTime = remember(airMinumHariIni?.waktuGelasTerakhir) {
        airMinumHariIni?.waktuGelasTerakhir?.let { waktu ->
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(waktu)
        } ?: "-"
    }

    // Animate progress changes
    val progressAnimation by animateFloatAsState(
        targetValue = if (targetGelas > 0) jumlahGelas.toFloat() / targetGelas else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "Progress Animation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Tracker Minum Air",
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
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            Icons.Rounded.Settings,
                            contentDescription = "Pengaturan",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Visualisasi Progress
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Progress
                val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2
                    drawCircle(
                        color = surfaceVariant,
                        radius = radius,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )
                }

                // Progress Aktual - using animated progress
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawArc(
                        color = accentColor,
                        startAngle = -90f,
                        sweepAngle = progressAnimation * 360f,
                        useCenter = false,
                        style = Stroke(width = 20f, cap = StrokeCap.Round)
                    )
                }

                // Konten Tengah
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.WaterDrop,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(60.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$jumlahGelas / $targetGelas Gelas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    val percentProgress = (progressAnimation * 100).toInt()
                    Text(
                        text = "$percentProgress% Target",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${jumlahGelas * ukuranGelas} / ${targetGelas * ukuranGelas} ml",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (airMinumHariIni?.waktuGelasTerakhir != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Terakhir: $lastDrinkTime",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tombol Tambah Gelas
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.tambahGelas() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Tambah Gelas")
            }

            // Statistik Mingguan
            Spacer(modifier = Modifier.height(16.dp))

            // Calculate statistics inside the Composable
            val statistik = remember(uiState.hariAktif, uiState.targetTercapai, uiState.rataRataKeberhasilan) {
                val hariAktif = uiState.hariAktif
                val targetTercapai = uiState.targetTercapai
                val rataRata = uiState.rataRataKeberhasilan.toInt()

                mapOf(
                    "totalHari" to hariAktif,  // Gunakan hariAktif
                    "hariTercapai" to targetTercapai,
                    "persentaseTercapai" to if (hariAktif > 0) {
                        (targetTercapai.toDouble() / hariAktif * 100).toInt()
                    } else {
                        0
                    },
                    "rataRata" to rataRata
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Statistik Mingguan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Total Hari: ${statistik["totalHari"]}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Hari Mencapai Target: ${statistik["hariTercapai"]}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Persentase Tercapai: ${statistik["persentaseTercapai"]}%",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "Rata-rata 7 Hari: ${statistik["rataRata"]}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Info Pengingat
            Spacer(modifier = Modifier.height(16.dp))

            if (airMinumHariIni?.pengingat == true) {
                // Format interval text based on unit
                val intervalText = remember(airMinumHariIni.intervalPengingat, airMinumHariIni.intervalUnit) {
                    when (airMinumHariIni.intervalUnit) {
                        IntervalUnit.MINUTES -> {
                            if (airMinumHariIni.intervalPengingat < 60) {
                                "Kamu akan diingatkan minum air setiap ${airMinumHariIni.intervalPengingat} menit"
                            } else {
                                val jam = airMinumHariIni.intervalPengingat / 60
                                val menit = airMinumHariIni.intervalPengingat % 60
                                if (menit == 0) {
                                    "Kamu akan diingatkan minum air setiap $jam jam"
                                } else {
                                    "Kamu akan diingatkan minum air setiap $jam jam $menit menit"
                                }
                            }
                        }
                        IntervalUnit.HOURS -> "Kamu akan diingatkan minum air setiap ${airMinumHariIni.intervalPengingat / 60} jam"
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = intervalText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // Tombol Reset Data
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showResetOptionsDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Outlined.Refresh,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset Data")
            }

            // Pengaturan Dialog
            if (showSettingsDialog) {
                var targetGelas by remember {
                    mutableStateOf(airMinumHariIni?.targetGelas?.toString() ?: "8")
                }
                var ukuranGelas by remember {
                    mutableStateOf(airMinumHariIni?.ukuranGelas?.toString() ?: "250")
                }
                var pengingatAktif by remember {
                    mutableStateOf(airMinumHariIni?.pengingat ?: true)
                }

                // Konversi nilai interval ke format yang sesuai untuk tampilan
                var intervalValue by remember {
                    val defaultValue = if (airMinumHariIni?.intervalUnit == IntervalUnit.HOURS) {
                        (airMinumHariIni.intervalPengingat / 60).toString()
                    } else {
                        airMinumHariIni?.intervalPengingat?.toString() ?: "60"
                    }
                    mutableStateOf(defaultValue)
                }

                // Pilihan unit interval (menit atau jam)
                var selectedIntervalUnit by remember {
                    mutableStateOf(airMinumHariIni?.intervalUnit ?: IntervalUnit.MINUTES)
                }

                AlertDialog(
                    onDismissRequest = { showSettingsDialog = false },
                    title = {
                        Text(
                            "Pengaturan Tracker Air",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    text = {
                        Column {
                            // Target Gelas
                            OutlinedTextField(
                                value = targetGelas,
                                onValueChange = {
                                    targetGelas = it.filter { char -> char.isDigit() }
                                },
                                label = { Text("Target Gelas per Hari") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Ukuran Gelas
                            OutlinedTextField(
                                value = ukuranGelas,
                                onValueChange = {
                                    ukuranGelas = it.filter { char -> char.isDigit() }
                                },
                                label = { Text("Ukuran Gelas (ml)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Interval Pengingat (hanya tampil jika pengingat aktif)
                            AnimatedVisibility(visible = pengingatAktif) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Input field untuk nilai interval
                                        OutlinedTextField(
                                            value = intervalValue,
                                            onValueChange = {
                                                intervalValue = it.filter { char -> char.isDigit() }
                                            },
                                            label = { Text("Interval Pengingat") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        // Dropdown untuk memilih unit interval (menit atau jam)
                                        Column {
                                            Text(
                                                "Unit",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier
                                                    .clip(MaterialTheme.shapes.small)
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                                    .padding(4.dp)
                                            ) {
                                                // Pilihan Menit
                                                Box(
                                                    modifier = Modifier
                                                        .clip(MaterialTheme.shapes.small)
                                                        .background(
                                                            if (selectedIntervalUnit == IntervalUnit.MINUTES)
                                                                MaterialTheme.colorScheme.primary
                                                            else
                                                                MaterialTheme.colorScheme.surfaceVariant
                                                        )
                                                        .clickable { selectedIntervalUnit = IntervalUnit.MINUTES }
                                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                                ) {
                                                    Text(
                                                        "Menit",
                                                        color = if (selectedIntervalUnit == IntervalUnit.MINUTES)
                                                            MaterialTheme.colorScheme.onPrimary
                                                        else
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(4.dp))

                                                // Pilihan Jam
                                                Box(
                                                    modifier = Modifier
                                                        .clip(MaterialTheme.shapes.small)
                                                        .background(
                                                            if (selectedIntervalUnit == IntervalUnit.HOURS)
                                                                MaterialTheme.colorScheme.primary
                                                            else
                                                                MaterialTheme.colorScheme.surfaceVariant
                                                        )
                                                        .clickable { selectedIntervalUnit = IntervalUnit.HOURS }
                                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                                ) {
                                                    Text(
                                                        "Jam",
                                                        color = if (selectedIntervalUnit == IntervalUnit.HOURS)
                                                            MaterialTheme.colorScheme.onPrimary
                                                        else
                                                            MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Informasi pengingat
                                    val unitText = if (selectedIntervalUnit == IntervalUnit.MINUTES) "menit" else "jam"
                                    Text(
                                        text = "Kamu akan diingatkan jika tidak minum air selama ${intervalValue} $unitText",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            // Pengingat
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = pengingatAktif,
                                    onCheckedChange = { pengingatAktif = it }
                                )
                                Text("Aktifkan Pengingat")
                            }
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val targetGelasInt = targetGelas.toIntOrNull() ?: 8
                                val ukuranGelasInt = ukuranGelas.toIntOrNull() ?: 250

                                // Konversi nilai interval berdasarkan unit yang dipilih
                                val intervalInt = intervalValue.toIntOrNull() ?: if (selectedIntervalUnit == IntervalUnit.MINUTES) 60 else 1

                                // Nilai interval disimpan dalam menit di database
                                val intervalInMinutes = if (selectedIntervalUnit == IntervalUnit.HOURS) {
                                    intervalInt * 60
                                } else {
                                    intervalInt
                                }

                                viewModel.updatePengaturan(
                                    targetGelas = targetGelasInt,
                                    ukuranGelas = ukuranGelasInt,
                                    pengingat = pengingatAktif,
                                    intervalPengingat = intervalInMinutes,
                                    intervalUnit = selectedIntervalUnit
                                )
                                showSettingsDialog = false
                            }
                        ) {
                            Text("Simpan")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSettingsDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            // Dialog Opsi Reset
            if (showResetOptionsDialog) {
                AlertDialog(
                    onDismissRequest = { showResetOptionsDialog = false },
                    title = {
                        Text(
                            "Pilih Opsi Reset",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pilih data yang ingin direset:")

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    showResetOptionsDialog = false
                                    showResetDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Reset Data Hari Ini")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    showResetOptionsDialog = false
                                    showResetMingguanDialog = true
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Reset Data Minggu Ini")
                            }
                        }
                    },
                    confirmButton = {},
                    dismissButton = {
                        TextButton(onClick = { showResetOptionsDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            // Reset Dialog Hari Ini
            if (showResetDialog) {
                AlertDialog(
                    onDismissRequest = { showResetDialog = false },
                    title = {
                        Text(
                            "Reset Data Hari Ini",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    text = {
                        Text("Apakah kamu yakin ingin me-reset data minum air hari ini? Jumlah gelas akan dikembalikan ke 0.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.resetDataHariIni()
                                showResetDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("Reset")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            // Reset Dialog Mingguan
            if (showResetMingguanDialog) {
                AlertDialog(
                    onDismissRequest = { showResetMingguanDialog = false },
                    title = {
                        Text(
                            "Reset Data Minggu Ini",
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    text = {
                        Text("Apakah kamu yakin ingin me-reset data minum air selama seminggu terakhir? Jumlah gelas untuk 7 hari terakhir akan dikembalikan ke 0.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.resetDataMingguan()
                                showResetMingguanDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("Reset")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResetMingguanDialog = false }) {
                            Text("Batal")
                        }
                    }
                )
            }

            // Error Handling
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Tutup")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}