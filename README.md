# SEKA - Sekretaris Pribadi Android

SEKA (Sekretaris Pribadi berbasis Android) adalah aplikasi asisten digital pribadi yang membantu pengguna mengatur berbagai aspek kehidupan sehari-hari dalam satu platform terintegrasi.

## Fitur Utama
<img src="https://github.com/user-attachments/assets/6623e6f7-cf38-4ca3-bbe6-7baac6290df5" width="300" alt="Screenshot 1"> <img src="https://github.com/user-attachments/assets/6da1548f-71be-4a82-a6a1-cc7756d5b6bd" width="300" alt="Screenshot 2">


1. **To-Do List**: Pengelolaan tugas dengan prioritas, pengingat, dan tenggat waktu
2. **Catatan**: Pencatatan informasi penting dengan kemampuan pencarian
3. **Tabungan**: Pelacakan progress tabungan dengan visualisasi
4. **Keuangan**: Pencatatan pemasukan dan pengeluaran dengan kategori dan ekspor PDF
5. **Tracking Air Minum**: Pemantauan konsumsi air dengan pengingat
6. **Ringkasan Teks**: AI untuk meringkas teks panjang
7. **Parafrase**: AI untuk mengubah kalimat dengan makna sama
8. **Terjemahan**: AI untuk menerjemahkan antar bahasa
9. **SEKA AI**: Asisten berbasis AI untuk menjawab pertanyaan
10. **Enkripsi Pesan**: Utility untuk enkripsi dan dekripsi pesan

## Teknologi

- Kotlin 1.8+
- Jetpack Compose 1.5+
- Arsitektur MVVM
- Room Database
- Flow & StateFlow
- WorkManager
- OpenAI API
- Hilt Dependency Injection
- Material 3 Design

## Persyaratan Sistem

- Android 5.0 (API 21) atau lebih tinggi
- Minimal 2GB RAM
- Minimal 150MB ruang penyimpanan

## Instalasi

1. Clone repository:
```
git clone https://github.com/V7-Lippyy/Seka-App.git
```

2. Buka dengan Android Studio Hedgehog (2023.1.1) atau lebih baru

3. Konfigurasi API Key OpenAI di `AppModule.kt` atau dengan environment variable

4. Build dan jalankan aplikasi

## Struktur Proyek

Proyek menggunakan struktur modular dengan arsitektur MVVM:

- `data/`: Model data, database, dan repository
- `di/`: Dependency injection
- `ui/`: Screens dan komponan UI Compose
- `util/`: Utilities, services, dan workers

## Database

SEKA menggunakan Room Database dengan entity:
- TodoItem
- NoteItem
- TabunganItem
- KeuanganItem
- AirMinumItem
- ChatMessageEntity

## Pengembang

© 2025 Muhammad Alif Qadri
