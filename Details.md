# Dokumentasi Proyek: SEKA - Android Kotlin Jetpack Compose MVVM

## 1. Judul dan Deskripsi Proyek

**Nama Proyek**: SEKA (Sekretaris Pribadi berbasis Android)

**Deskripsi**: 
Aplikasi Android menggunakan Kotlin dan Jetpack Compose dengan arsitektur MVVM. SEKA dirancang sebagai asisten digital pribadi yang membantu pengguna mengatur berbagai aspek kehidupan sehari-hari secara efisien dalam satu platform terintegrasi. Aplikasi ini bertujuan menyederhanakan pengelolaan tugas harian, keuangan pribadi, dan kesehatan dengan menyediakan antarmuka yang intuitif dan kaya fitur.

SEKA memiliki sepuluh fitur utama:
1. **To-Do List**: Pengelolaan tugas dengan tingkat urgensi, pengingat berulang, tenggat waktu, dan status penyelesaian.
2. **Catatan**: Pencatatan informasi penting dengan kemampuan pencarian.
3. **Tabungan**: Pelacakan progress tabungan untuk barang yang diinginkan, termasuk visualisasi progress dan perkiraan waktu pencapaian.
4. **Keuangan**: Pencatatan pemasukan dan pengeluaran dengan kategorisasi dan ekspor laporan PDF.
5. **Tracking Air Minum**: Pemantauan konsumsi air harian dengan pengingat terjadwal.
6. **Ringkasan Teks**: Utility AI untuk meringkas teks panjang menjadi poin-poin utama.
7. **Parafrase**: Utility AI untuk mengubah kalimat dengan makna yang sama namun berbeda kata.
8. **Terjemahan**: Utility AI untuk menerjemahkan teks antar bahasa.
9. **SEKA AI**: Asisten berbasis AI yang dapat menjawab pertanyaan dan membantu tugas pengguna.
10. **Enkripsi Pesan**: Utility untuk mengenkripsi dan mendekripsi pesan dengan kode unik.

**Teknologi**: 
- Kotlin 1.8+
- Jetpack Compose 1.5+ untuk UI berbasis declarative
- Arsitektur MVVM (Model-View-ViewModel)
- Room Database untuk penyimpanan data lokal
- Flow & StateFlow untuk reactive programming
- WorkManager untuk background tasks
- OpenAI API untuk fitur kecerdasan buatan
- Hilt untuk dependency injection
- Material 3 Design untuk antarmuka modern

**Dependensi Utama**:
- `androidx.core:core-ktx`: Untuk ekstensi Kotlin pada Android Core
- `androidx.compose.ui:ui`: Framework UI Composable
- `androidx.compose.material3:material3`: Komponen Material Design 3
- `androidx.lifecycle:lifecycle-viewmodel-compose`: ViewModel terintegrasi dengan Compose
- `androidx.room:room-ktx`: Persistence library dengan dukungan Kotlin
- `androidx.hilt:hilt-navigation-compose`: Integrasi Hilt dengan navigasi Compose
- `androidx.work:work-runtime-ktx`: API WorkManager dengan dukungan Kotlin
- `com.aallam.openai:openai-client`: Klien untuk OpenAI API
- `com.itextpdf:itextpdf`: Library untuk pembuatan dokumen PDF

SEKA menjadi solusi all-in-one untuk manajemen pribadi, dengan pendekatan arsitektur yang modular dan terstruktur untuk memastikan pemeliharaan kode yang mudah dan performa yang optimal.

## 2. Persyaratan Sistem (System Requirements)

**Perangkat Keras**:
- Minimum 2 GB RAM
- Minimal ruang penyimpanan 150 MB
- Perangkat Android dengan API level 21 (Lollipop) atau lebih tinggi

**Perangkat Lunak**:
- Android Studio Hedgehog (2023.1.1) atau lebih baru
- JDK 17 atau lebih tinggi
- SDK Android API Level 21 (Lollipop) sebagai minimum, dioptimalkan untuk API Level 33 (Android 13)
- Kotlin 1.8.0 atau lebih tinggi
- Jetpack Compose 1.5.0 atau lebih tinggi

**Dependensi Utama**:
- `androidx.core:core-ktx:1.12.0`
- `androidx.lifecycle:lifecycle-runtime-ktx:2.6.2`
- `androidx.activity:activity-compose:1.8.0`
- `androidx.compose.ui:ui:1.5.4`
- `androidx.compose.material3:material3:1.1.2`
- `androidx.room:room-runtime:2.6.0`
- `androidx.room:room-ktx:2.6.0`
- `androidx.hilt:hilt-navigation-compose:1.1.0`
- `com.aallam.openai:openai-client:3.3.0`
- `androidx.work:work-runtime-ktx:2.8.1`
- `com.itextpdf:itextpdf:5.5.13.3`

## 3. Instalasi dan Konfigurasi

### Langkah 1: Mengunduh dan Menginstal Dependensi

1. Pastikan Android Studio telah terpasang (versi Hedgehog atau yang lebih baru).
2. Clone repository dari GitHub atau unduh file proyek.
3. Buka proyek di Android Studio.
4. Pastikan file `build.gradle` (project level) telah dikonfigurasi dengan repository `google()` dan `mavenCentral()`.
5. Sync project untuk mengunduh semua dependensi yang diperlukan.

### Langkah 2: Pengaturan API Key (OpenAI)

1. Untuk menggunakan fitur AI (ringkasan, parafrase, terjemahan, chat AI), diperlukan API key dari OpenAI.
2. Tambahkan API key Anda ke variabel lingkungan atau file konfigurasi lokal.
3. Secara default, API key dapat ditambahkan di `AppModule.kt`:

```kotlin
@Provides
@Singleton
fun provideOpenAI(): OpenAI {
    return OpenAI(
        token = System.getenv("OPENAI_API_KEY") ?: "your-api-key-here"
    )
}
```

### Langkah 3: Menjalankan Aplikasi

1. Pilih perangkat Android atau emulator (API Level 21 atau lebih tinggi).
2. Klik tombol "Run" (▶️) di Android Studio.
3. Aplikasi akan diinstal dan dijalankan pada perangkat yang dipilih.

## 4. Struktur Direktori (Directory Structure)

Proyek SEKA diorganisir dengan struktur modular untuk memisahkan tanggung jawab dan mendukung arsitektur MVVM:

```
com.example.seka/
├── data/
│   ├── local/
│   │   ├── dao/                 # Data Access Objects untuk Room DB
│   │   ├── database/            # Konfigurasi database Room
│   │   └── entity/              # Entity classes (model data)
│   └── repository/              # Repository pattern implementation
├── di/
│   └── AppModule.kt             # Dependency Injection dengan Hilt
├── ui/
│   ├── navigation/              # Navigasi dengan Jetpack Navigation
│   ├── screens/                 # UI Screens menggunakan Compose
│   │   ├── airminum/            # Screen untuk tracking air minum
│   │   ├── enkripsi/            # Screen untuk enkripsi pesan
│   │   ├── home/                # Home screen
│   │   ├── keuangan/            # Screen untuk manajemen keuangan
│   │   ├── note/                # Screen untuk catatan
│   │   ├── paraphrase/          # Screen untuk parafrase teks
│   │   ├── sekaai/              # Screen untuk AI assistant
│   │   ├── splash/              # Splash screen
│   │   ├── summary/             # Screen untuk ringkasan teks
│   │   ├── tabungan/            # Screen untuk manajemen tabungan
│   │   ├── terjemahan/          # Screen untuk terjemahan teks
│   │   └── todo/                # Screen untuk to-do list
│   ├── theme/                   # Tema aplikasi (warna, tipografi)
│   └── utils/                   # Utilitas UI
├── util/
│   ├── api/                     # Konfigurasi API
│   ├── notification/            # Pengelolaan notifikasi
│   └── workers/                 # Background workers
├── MainActivity.kt              # Entry point aplikasi
└── SekaApplication.kt           # Application class
```

## 5. Penjelasan Tentang Kode Sumber (Source Code Explanation)

### Arsitektur MVVM
SEKA mengimplementasikan arsitektur MVVM (Model-View-ViewModel) dengan komponen berikut:

- **Model**: Representasi data dalam aplikasi, terdiri dari entity classes, database, dan repository.
- **View**: UI berbasis Jetpack Compose yang menampilkan data dan merespons interaksi pengguna.
- **ViewModel**: Perantara antara Model dan View, mengelola status UI dan logika bisnis.

### Komponen Utama:

#### 1. Database (Room)
- **AppDatabase.kt**: Database Room dengan entity untuk Todo, Note, Tabungan, Keuangan, ChatMessage, dan AirMinum.
- **Dao Interfaces**: DAO untuk setiap entity yang menyediakan metode untuk akses data.
- **Entity Classes**: Kelas yang merepresentasikan tabel database.

#### 2. Repository
- **Repository Classes**: Abstraksi untuk akses data dengan metode untuk operasi CRUD.

#### 3. ViewModel
- **ViewModel Classes**: Mengelola status UI dan logika bisnis untuk setiap fitur.
- **UiState**: Data class yang merepresentasikan status UI.

#### 4. UI (Jetpack Compose)
- **Screen Components**: Komponen UI yang terbuat dari Composable Functions.
- **Navigation**: Navigasi antar screen menggunakan NavHost.

#### 5. Notification & Workers
- **NotificationHelper**: Mengelola notifikasi untuk pengingat.
- **Worker Classes**: Background tasks untuk pengingat tugas dan tracking air minum.

#### 6. Utilities
- **DateUtils**: Utilitas untuk manipulasi tanggal.
- **PDFGenerator**: Utilitas untuk ekspor data ke PDF.
- **EnkripsiEngine**: Implementasi enkripsi/dekripsi pesan.

### Fitur Utama

#### 1. To-Do List (TodoScreen, TodoViewModel)
- Membuat, mengedit, dan menghapus tugas.
- Mengatur prioritas tugas (tinggi, sedang, rendah).
- Menambahkan tenggat waktu dan pengingat.

#### 2. Notes (NoteScreen, NoteViewModel)
- Mencatat dan menyimpan catatan dengan judul dan konten.
- Mencari catatan berdasarkan kata kunci.

#### 3. Tabungan (TabunganScreen, TabunganViewModel)
- Melacak tabungan untuk barang yang diinginkan.
- Menambahkan saldo tabungan dan melihat progress.

#### 4. Keuangan (KeuanganScreen, KeuanganViewModel)
- Mencatat pemasukan dan pengeluaran.
- Mengkategorikan transaksi.
- Laporan keuangan dengan ekspor PDF.

#### 5. Air Minum (AirMinumScreen, AirMinumViewModel)
- Melacak konsumsi air harian.
- Pengingat minum air.

#### 6. AI Features (OpenAIService)
- Ringkasan teks.
- Parafrase teks.
- Terjemahan teks.
- Asisten AI berbasis chat.

#### 7. Enkripsi (EnkripsiScreen, EnkripsiViewModel)
- Enkripsi dan dekripsi pesan dengan kode unik.

## 6. Penggunaan API

SEKA menggunakan OpenAI API untuk fitur AI-nya. Implementasi berada di `OpenAIService.kt`.

**Metode API:**
- `generateSummary`: Meringkas teks panjang.
- `generateParaphrase`: Memparafrase teks dengan kata-kata berbeda namun makna sama.
- `translate`: Menerjemahkan teks dari satu bahasa ke bahasa lain.
- `chatWithAI`: Berinteraksi dengan AI untuk jawaban teks.

**Contoh Penggunaan:**
```kotlin
// Di ViewModel
suspend fun generateSummary(text: String) {
    val summary = openAIService.generateSummary(text)
    // Process response
}
```

## 7. Contoh Penggunaan (Usage Examples)

### Menambahkan Tugas Baru
```kotlin
// Di TodoDetailScreen
Button(
    onClick = { viewModel.saveTodo() },
    modifier = Modifier.fillMaxWidth()
) {
    Text("Simpan")
}
```

### Mencatat Konsumsi Air
```kotlin
// Di AirMinumScreen
Button(
    onClick = { viewModel.tambahGelas() },
    modifier = Modifier.fillMaxWidth()
) {
    Text("Tambah Gelas")
}
```

### Menggunakan Fitur AI
```kotlin
// Di SummaryScreen
Button(
    onClick = { viewModel.generateSummary() },
    modifier = Modifier.fillMaxWidth()
) {
    Text("Ringkas Teks")
}
```

## 8. Pengujian dan Test

**Jenis Pengujian:**
- **Unit Tests**: Untuk menguji logika di ViewModel dan Repository.
- **Integration Tests**: Untuk menguji interaksi antara komponen.
- **UI Tests**: Untuk menguji komponen UI dan interaksi pengguna.

**Strategi Pengujian:**
1. Gunakan Mockito untuk mock dependencies.
2. Gunakan Room In-Memory Database untuk pengujian repository.
3. Gunakan Compose Testing untuk UI tests.

**Contoh Unit Test untuk ViewModel:**
```kotlin
@Test
fun todoViewModel_toggleTodoCompletion_updatesDatabase() {
    // Given
    val todo = TodoItem(id = 1, title = "Test Todo", isCompleted = false)
    
    // When
    viewModel.toggleTodoCompletion(todo)
    
    // Then
    verify(repository).updateTodo(any())
}
```

## 9. Troubleshooting dan Pemecahan Masalah

### Masalah: Aplikasi Crash saat Membuka Fitur AI
**Penyebab**: API key OpenAI tidak valid atau tidak tersedia.
**Solusi**: Tambahkan API key yang valid di `AppModule.kt` atau environment variable.

### Masalah: Notifikasi Tidak Muncul
**Penyebab**: Izin notifikasi tidak diberikan atau worker tidak terjadwal dengan benar.
**Solusi**: Periksa izin notifikasi dan pastikan worker terdaftar dengan benar.

### Masalah: Database Migration Error
**Penyebab**: Skema database berubah tanpa migration script.
**Solusi**: Terapkan migration script yang tepat di `AppDatabase.kt`.

### Log Error:
Gunakan Logcat dengan tag untuk debugging:
```kotlin
private const val TAG = "YourClassName"
Log.d(TAG, "Debug message")
```

## 10. Penjelasan tentang Struktur Database

SEKA menggunakan Room sebagai abstraksi di atas SQLite untuk mengelola data lokal.

### Tabel dan Entity:

#### 1. TodoItem
- **id**: Long (Primary Key, autoincrement)
- **title**: String
- **content**: String
- **date**: Date
- **dueDate**: Date (nullable)
- **urgency**: TodoUrgency (enum: HIGH, MEDIUM, LOW)
- **isCompleted**: Boolean
- **dailyReminder**: Boolean
- **dueDateReminder**: Boolean
- **dueDateReminderDays**: Int
- **intervalReminder**: Boolean
- **intervalValue**: Int
- **intervalUnit**: TimeUnit (enum: SECONDS, MINUTES, HOURS)

#### 2. NoteItem
- **id**: Long (Primary Key, autoincrement)
- **title**: String
- **content**: String
- **createdAt**: Date
- **updatedAt**: Date

#### 3. TabunganItem
- **id**: Long (Primary Key, autoincrement)
- **nama**: String
- **hargaTarget**: Double
- **tabunganTerkumpul**: Double
- **cicilanJumlah**: Double
- **kategori**: String
- **targetDate**: Date (nullable)
- **imagePath**: String (nullable)

#### 4. KeuanganItem
- **id**: Long (Primary Key, autoincrement)
- **tanggal**: Date
- **judul**: String
- **deskripsi**: String
- **jumlah**: Double
- **tipe**: TransactionType (enum: INCOME, EXPENSE)
- **kategori**: String

#### 5. AirMinumItem
- **id**: Long (Primary Key, autoincrement)
- **tanggal**: Date
- **jumlahGelas**: Int
- **targetGelas**: Int
- **ukuranGelas**: Int
- **pengingat**: Boolean
- **intervalPengingat**: Int
- **intervalUnit**: IntervalUnit (enum: MINUTES, HOURS)

#### 6. ChatMessageEntity
- **id**: String (Primary Key)
- **content**: String
- **isFromUser**: Boolean
- **timestamp**: Date

### Contoh Query:
```kotlin
// Mendapatkan todo yang belum selesai
@Query("SELECT * FROM todo_items WHERE isCompleted = 0")
fun getUncompletedTodos(): Flow<List<TodoItem>>

// Mendapatkan transaksi berdasarkan tanggal
@Query("SELECT * FROM keuangan_items WHERE tanggal BETWEEN :startDate AND :endDate")
fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<KeuanganItem>>
```

## 11. Lisensi dan Hak Cipta (License and Copyright)

**Lisensi**:  
SEKA dilisensikan di bawah MIT License.

**Hak Cipta**:  
© 2025 Muhammad Alif Qadri

## 12. Daftar Pustaka dan Referensi

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture)
- [Room Persistence Library](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt Dependency Injection](https://developer.android.com/training/dependency-injection/hilt-android)
- [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- [OpenAI API Documentation](https://platform.openai.com/docs/api-reference)

git clone https://github.com/username/seka-app.git
cd seka-app
