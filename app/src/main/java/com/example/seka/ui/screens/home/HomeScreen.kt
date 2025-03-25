package com.example.seka.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.seka.R
import com.example.seka.ui.navigation.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material.icons.rounded.Water
import com.example.seka.ui.utils.SystemBarColors // sesuaikan dengan package Anda

data class FeatureItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val description: String,
    val gradientColors: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    // Data features
    val isDarkTheme = isSystemInDarkTheme()

    // Panggil fungsi SystemBarColors di sini
    SystemBarColors(
        statusBarColor = if (isDarkTheme) Color(0xFF151B1C) else Color(0xFFF6FCFD),
        navigationBarColor = if (isDarkTheme) Color(0xFF151B1C) else Color(0xFFF6FCFD),
        isDarkTheme = isDarkTheme
    )

    // Gradient colors with much higher contrast
    val features = listOf(
        FeatureItem(
            "To Do List",
            Icons.Rounded.CheckCircle,
            Screen.Todo.route,
            "Catat dan kelola tugas-tugas dengan prioritas",
            if (isDarkTheme)
                listOf(Color(0xFF64B5F6), Color(0xFF0D47A1))
            else
                listOf(Color(0xFF90CAF9), Color(0xFF0D47A1))
        ),
        FeatureItem(
            "Catatan",
            Icons.Rounded.Note,
            Screen.Note.route,
            "Simpan catatan penting",
            if (isDarkTheme)
                listOf(Color(0xFF81C784), Color(0xFF1B5E20))
            else
                listOf(Color(0xFFA5D6A7), Color(0xFF1B5E20))
        ),
        FeatureItem(
            "Tabungan",
            Icons.Rounded.Savings,
            Screen.Tabungan.route,
            "Kelola tabungan untuk barang yang diinginkan",
            if (isDarkTheme)
                listOf(Color(0xFFFFCC80), Color(0xFFE65100))
            else
                listOf(Color(0xFFFFE0B2), Color(0xFFE65100))
        ),
        FeatureItem(
            "Keuangan",
            Icons.Rounded.MonetizationOn,
            Screen.Keuangan.route,
            "Lacak pemasukan dan pengeluaran",
            if (isDarkTheme)
                listOf(Color(0xFFF8BBD0), Color(0xFF880E4F))
            else
                listOf(Color(0xFFF8BBD0), Color(0xFF880E4F))
        ),
        FeatureItem(
            "Ringkasan",
            Icons.Rounded.Summarize,
            Screen.Summary.route,
            "Ringkas teks panjang",
            if (isDarkTheme)
                listOf(Color(0xFFB39DDB), Color(0xFF311B92))
            else
                listOf(Color(0xFFD1C4E9), Color(0xFF311B92))
        ),
        FeatureItem(
            "Parafrase",
            Icons.Rounded.Repeat,
            Screen.Paraphrase.route,
            "Ubah teks dengan makna sama",
            if (isDarkTheme)
                listOf(Color(0xFF80CBC4), Color(0xFF004D40))
            else
                listOf(Color(0xFFB2DFDB), Color(0xFF004D40))
        ),
        FeatureItem(
            "Terjemahan",
            Icons.Rounded.Translate,
            Screen.Terjemahan.route,
            "Terjemahkan teks ke berbagai bahasa",
            if (isDarkTheme)
                listOf(Color(0xFF9FA8DA), Color(0xFF1A237E))
            else
                listOf(Color(0xFFC5CAE9), Color(0xFF1A237E))
        ),
        FeatureItem(
            "SEKA AI",
            Icons.Rounded.SmartToy,
            Screen.SekaAI.route,
            "Asisten AI dengan fitur suara",
            if (isDarkTheme)
                listOf(Color(0xFFFFCDD2), Color(0xFFB71C1C))
            else
                listOf(Color(0xFFFFEBEE), Color(0xFFB71C1C))
        ),
        FeatureItem(
            "Enkripsi Pesan",
            Icons.Rounded.Lock,
            Screen.Enkripsi.route,
            "Enkripsi pesan dengan kode unik",
            if (isDarkTheme)
                listOf(Color(0xFFCE93D8), Color(0xFF4A148C))
            else
                listOf(Color(0xFFE1BEE7), Color(0xFF4A148C))
        ),
        FeatureItem(
            "Tracker Air",
            Icons.Rounded.Water,
            Screen.AirMinum.route,
            "Lacak konsumsi air harian",
            if (isDarkTheme)
                listOf(Color(0xFF4FC3F7), Color(0xFF0277BD))
            else
                listOf(Color(0xFFB3E5FC), Color(0xFF0277BD))
        )
    )

    val scrollState = rememberScrollState()
    var isLoaded by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    // Banner images
    val bannerImages = listOf(
        "https://media-hosting.imagekit.io//23c34eb0fcb84f12/banner1.png?Expires=1837220164&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=RWkQrGmnCzJE~Dy0bHvNL1kZv-Hcy2AAgZbOBD4h7WDAxwEcOukgdlPOuE6uWANStCFzFkY~qb-0ejWzNpWyqmCNsU5tVrgFFjr7-TmSJ3ncPTIlw0qZYXYTShX1N99aljQBnbTqtr1J-w~MZDWIKb5BOA35dBAVDyPQ8KR8frQXg4SkKt5AvS-ARlFB~uQOs-Q4gDOQc5wQgmTFl0tc8np8pnEsjXuRBJRCFY8Q1cFUqaEhlfWhpV6FHFQW12yBV-ervTHEkqfJ1emQe3spQzse7Z4WN2uISLrxwir6GJibB0WA4-mjyTolaemxOc~FoOe0lqT3M7y-iXGkXeCTrQ__",
        "https://media-hosting.imagekit.io//4d02e20244b94f8b/banner1%20(1).png?Expires=1837222626&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=HFbfXlW5gW4PuQaoFABrhFb17bCKfvsp10wi6yNfu3lWEeoVhIaftZWln0dbqDv6jSVA1~yOKc2oIdSdblyf0uJfK996HYbx2Bn1iIVvNo9lp8lvaedJLbrMSd3On6x3Zw1rIpEK1zLAkF2k1oRjHb71g2DgW9DWXdkGTyT3NBUkOGsukB1T0QBOrZ9gyt633RjLjrwPpPnN3Y2P-dTdz5HBF86tWMOFpMhY6nIvJuqRhMoBr3nG2UzP9uGvAYD0-Qjv4lW7XrhXuppsPalyZaJRRBTDBy2tMSFMZnZCe2B19ehnfFXS2Zpxv9pFyd~r6EsahcAcXRQ~nNDB~hA4Xg__",
        "https://media-hosting.imagekit.io//c4572d61d62a4833/banner1%20(2).png?Expires=1837222628&Key-Pair-Id=K2ZIVPTIP2VGHC&Signature=RZxT5zQnm6xVgsnx0HQhL-PTBdM6yQCpJFrPiIUsYGRzuufrVti1lDwaxb~MqCF1DJEuDkopCdMSsYrcPeew5kq4JAhJt3DqTzpNoYJWc4LJKV5uAH0VBIZXWjGXs5r~GKu2uAwX9Ipn1-VVYFCxhFhi23V4Kq~FgV3ows2OySAlOX63028WxouElMTvtVgNJVnIOmrUP6LR~WJqLYKYIAhc9e8LBMai55Eyw8yrqp~DKrLDq3FuP2rIaPp6oX7A7d3vcmcMqRj~zP4IGqxZMs61l3CshUFwvl9Uzbk5Nq9KeDohkE3s9ZbrB2bjYjM6udKd7jVMPeCOLvpV47tpgg__"
    )

    // Filtered features based on search
    val filteredFeatures = remember(searchQuery) {
        if (searchQuery.isBlank()) features
        else features.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }
    }

    // Pager state for the banner carousel
    val pagerState = rememberPagerState(pageCount = { bannerImages.size })
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll the banner
    LaunchedEffect(key1 = Unit) {
        delay(100)
        isLoaded = true

        while (true) {
            delay(3000) // Change image every 3 seconds
            coroutineScope.launch {
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % bannerImages.size
                )
            }
        }
    }

    // Main content with fully scrollable layout including TopAppBar
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TopAppBar as a normal card that scrolls with content
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 3.dp) // Keep the card padding small
                .padding(top = 4.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(12.dp),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isDarkTheme) Color(0xFF1D2632) else Color(0xFF333355)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp), // Reduce padding in the row
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Adjust logo size without changing the card size
                val logoResource = R.drawable.logowhite

                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(tween(700)) + scaleIn(tween(700), 0.8f)
                ) {
                    Image(
                        painter = painterResource(id = logoResource),
                        contentDescription = "Logo SEKA",
                        modifier = Modifier
                            .height(45.dp)  // Adjust the height of the logo here
                            .padding(vertical = 2.dp)  // Keep padding around the logo small
                    )
                }

                // Search button without circle background
                IconButton(
                    onClick = { showSearchDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Cari fitur",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Welcome Message with animated gradient
        AnimatedVisibility(
            visible = isLoaded,
            enter = fadeIn(tween(700)) + slideInVertically(tween(700)) { it }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 8.dp, bottom = 12.dp, end = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Create an animatable for gradient animation
                val infiniteTransition = rememberInfiniteTransition(label = "gradientTransition")
                val offset by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "gradientOffset"
                )

                // Gradient colors for the text
                val gradientColors = listOf(
                    Color(0xFF64B5F6),  // Light Blue
                    Color(0xFFCE93D8),  // Light Purple
                    Color(0xFFFF8A80),  // Light Red/Pink
                    Color(0xFF64B5F6)   // Light Blue again to create a loop
                )

                Text(
                    text = "Selamat Datang di SEKA",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.sp,
                        fontSize = 19.sp,
                        brush = Brush.linearGradient(
                            colors = gradientColors,
                            start = Offset(offset * 100f, 0f),
                            end = Offset(offset * 100f + 500f, 0f)
                        )
                    ),
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Start
                )
            }
        }

        // Banner Carousel
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    pageSpacing = 0.dp,
                    pageContent = { page ->
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(bannerImages[page])
                                .crossfade(true)
                                .build(),
                            contentDescription = "Banner $page",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1920f / 600f),
                            contentScale = ContentScale.Crop
                        )
                    }
                )

                // Gradient overlay at the bottom
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // "Pilih Fitur" divider
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )

            Text(
                text = " Pilih Fitur ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Divider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // All features in a simple list
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            features.forEachIndexed { index, feature ->
                AnimatedVisibility(
                    visible = isLoaded,
                    enter = fadeIn(tween(durationMillis = 300, delayMillis = 100 * index)) +
                            slideInVertically(
                                tween(durationMillis = 300, delayMillis = 100 * index),
                                initialOffsetY = { it / 2 }
                            )
                ) {
                    EnhancedFeatureCard(
                        feature = feature,
                        onClick = { navController.navigate(feature.route) },
                        animationDelay = index * 100
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    // Enhanced Search Dialog
    if (showSearchDialog) {
        AlertDialog(
            onDismissRequest = {
                showSearchDialog = false
                searchQuery = ""
            },
            title = {
                Text(
                    "Cari Fitur",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            icon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            text = {
                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Masukkan kata kunci...") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                            }
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Clear,
                                        contentDescription = "Hapus"
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Show search results in the dialog
                    if (searchQuery.isNotBlank()) {
                        if (filteredFeatures.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Tidak ada fitur yang sesuai dengan pencarian",
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.heightIn(max = 300.dp)
                            ) {
                                items(filteredFeatures) { feature ->
                                    ListItem(
                                        headlineContent = { Text(feature.title, fontWeight = FontWeight.Bold) },
                                        supportingContent = { Text(feature.description) },
                                        leadingContent = {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(
                                                        brush = Brush.linearGradient(feature.gradientColors)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = feature.icon,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .clickable {
                                                showSearchDialog = false
                                                searchQuery = ""
                                                navController.navigate(feature.route)
                                            }
                                            .padding(vertical = 4.dp)
                                    )
                                    Divider()
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSearchDialog = false },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Tutup")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedFeatureCard(
    feature: FeatureItem,
    onClick: () -> Unit,
    animationDelay: Int = 0
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()
    val borderColor = if (isDarkTheme) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)

    LaunchedEffect(Unit) {
        delay(animationDelay.toLong())
        animationPlayed = true
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = feature.gradientColors[0].copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 4.dp,
            hoveredElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Feature icon with subtle gradient
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(feature.gradientColors[0])
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                // Inner area with icon - gradient medium
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    feature.gradientColors[0],
                                    feature.gradientColors[1].copy(alpha = 0.7f)
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(60f, 60f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = feature.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = feature.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.3.sp
                )
            }

            // Pretty arrow with circle background
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        color = feature.gradientColors[0].copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = feature.gradientColors[0],
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}