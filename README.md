# SEKA - Sekretaris Pribadi Android

SEKA (Sekretaris Pribadi berbasis Android) adalah aplikasi asisten digital pribadi yang membantu pengguna mengatur berbagai aspek kehidupan sehari-hari dalam satu platform terintegrasi.

## Fitur Utama

### Overview
<div>
  <img src="https://github.com/user-attachments/assets/6623e6f7-cf38-4ca3-bbe6-7baac6290df5" width="300" alt="Screenshot 1">
  <img src="https://github.com/user-attachments/assets/6da1548f-71be-4a82-a6a1-cc7756d5b6bd" width="300" alt="Screenshot 2">
</div>

### 1. To-Do List
Pengelolaan tugas dengan prioritas, pengingat, dan tenggat waktu

<div>
  <img src="https://github.com/user-attachments/assets/706972ea-c081-4a79-ba60-c9d71cbb198b" width="200" alt="Screenshot 3">
  <img src="https://github.com/user-attachments/assets/aea085d4-68ec-4ba4-b27e-786cbf1f576f" width="200" alt="Screenshot 4">
  <img src="https://github.com/user-attachments/assets/0b6fa543-3a70-4777-af1f-20f0414a0b1f" width="200" alt="Screenshot 5">
</div>

### 2. Catatan
Pencatatan informasi penting dengan kemampuan pencarian

<div>
  <img src="https://github.com/user-attachments/assets/0338d5a5-b135-4537-94f9-632873f13dcf" width="200" alt="Screenshot 6">
  <img src="https://github.com/user-attachments/assets/349c5bfa-d5a7-4860-8df3-282066b8101e" width="200" alt="Screenshot 7">
</div>

### 3. Tabungan
Pelacakan progress tabungan dengan visualisasi

<div>
  <img src="https://github.com/user-attachments/assets/b480b2e4-7f0f-4f89-9793-3e5ae7c73d14" width="200" alt="Screenshot 8">
  <img src="https://github.com/user-attachments/assets/862c96bd-2650-4941-a597-cca799615ded" width="200" alt="Screenshot 9">
  <img src="https://github.com/user-attachments/assets/772b1cb7-514c-4ad1-85ae-73bc69c3bcac" width="200" alt="Screenshot 10">
</div>

### 4. Keuangan
Pencatatan pemasukan dan pengeluaran dengan kategori dan ekspor PDF

<div>
  <img src="https://github.com/user-attachments/assets/b3deac92-bdba-4a70-a102-3aa0f6786baf" width="200" alt="Screenshot 11">
  <img src="https://github.com/user-attachments/assets/f57772e7-dbfa-4f2b-aea7-42a1da7fc905" width="200" alt="Screenshot 12">
  <img src="https://github.com/user-attachments/assets/97a1d1de-596e-435f-b2dd-064866444653" width="200" alt="Screenshot 13">
</div>

### 5. Tracking Air Minum
Pemantauan konsumsi air dengan pengingat

<div>
  <img src="https://github.com/user-attachments/assets/4c6ba24b-1ae3-413a-a484-6f77d5705b11" width="200" alt="Screenshot 14">
  <img src="https://github.com/user-attachments/assets/30670602-8a5d-4bd4-a58b-afe5adf58ec6" width="200" alt="Screenshot 15">
  <img src="https://github.com/user-attachments/assets/021c1514-d03e-408d-a854-2eeaec445590" width="200" alt="Screenshot 16">
</div>

### 6. Ringkasan Teks
AI untuk meringkas teks panjang 

<div>
  <img src="https://github.com/user-attachments/assets/86b0d6a4-5767-4337-9701-012d4fe9023a" width="200" alt="Screenshot 17">
</div>

### 7. Parafrase
AI untuk mengubah kalimat dengan makna sama

<div>
  <img src="https://github.com/user-attachments/assets/186ca92a-26ee-4488-8f63-04816f70115c" width="200" alt="Screenshot 18">
</div>

### 8. Terjemahan
AI untuk menerjemahkan antar bahasa

<div>
  <img src="https://github.com/user-attachments/assets/eed86ea6-2252-486e-a143-49259e329dbd" width="200" alt="Screenshot 30">
</div>

### 9. SEKA AI
Asisten berbasis AI untuk menjawab pertanyaan

<div>
  <img src="https://github.com/user-attachments/assets/47d47d4e-f246-47a7-8eea-7b7a3008410e" width="200" alt="Screenshot 19">
  <img src="https://github.com/user-attachments/assets/a73a858a-e2f5-4a72-b6d3-241848bf653f" width="200" alt="Screenshot 20">
</div>

### 10. Enkripsi Pesan
Utility untuk enkripsi dan dekripsi pesan

<div>
  <img src="https://github.com/user-attachments/assets/add455d1-0b01-4e7c-b5ec-1847db68daf3" width="200" alt="Screenshot 21">
</div>

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

3. Konfigurasi API Key OpenAI di AppModule.kt atau dengan environment variable

4. Build dan jalankan aplikasi

## Struktur Proyek

Proyek menggunakan struktur modular dengan arsitektur MVVM:

- **data/**: Model data, database, dan repository
- **di/**: Dependency injection
- **ui/**: Screens dan komponan UI Compose
- **util/**: Utilities, services, dan workers

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
