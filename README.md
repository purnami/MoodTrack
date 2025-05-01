
# 📱 MoodTrack - Panduan Penggunaan Aplikasi

MoodTrack adalah aplikasi kesehatan mental yang membantu pengguna melacak suasana hati, mendapatkan rekomendasi aktivitas, melakukan self-assessment, dan menganalisis pola emosi berdasarkan data.

---

## 📦 Disediakan README lengkap berisi:

- ✅ Petunjuk instalasi  
- ✅ Struktur proyek  
- ✅ Teknologi yang digunakan  
- ✅ Alur pengembangan  

---

## 🔧 Petunjuk Instalasi

1. Clone repository ini:
   ```bash
   git clone https://github.com/purnami/MoodTrack.git
   ```
2. Buka dengan Android Studio (direkomendasikan versi terbaru).
3. Sinkronisasi Gradle dan pastikan koneksi internet aktif.
4. Jalankan aplikasi di emulator atau perangkat fisik Android minimal API 26.

---

## 🗂️ Struktur Proyek

```
MoodTrack/
├── app/                     # Modul utama
│   ├── data/                # Layer data (Room, Firestore, API)
│   ├── domain/              # Layer domain (use case, model)
│   ├── presentation/        # Layer UI (screen, ViewModel)
│   ├── di/                  # Dependency Injection (Hilt)
│   ├── utils/               # Utilitas umum
│   └── MainActivity.kt
├── build.gradle
└── README.md
```

---

## 💡 Teknologi yang Digunakan

| Teknologi | Keterangan |
|----------|------------|
| **Kotlin** | Bahasa pemrograman utama |
| **Jetpack Compose** | Untuk membangun UI modern |
| **MVVM + Clean Architecture** | Struktur proyek yang modular dan maintainable |
| **Room Database** | Penyimpanan data mood secara lokal |
| **Firebase Firestore** | Penyimpanan data pengguna di cloud |
| **Ktor** | Client HTTP untuk OpenAI & YouTube API |
| **Hilt** | Dependency injection |
| **Coroutines + Flow** | Pengelolaan asynchronous dan state |
| **WorkManager** | Penjadwalan notifikasi berbasis mood |

---

## 🔁 Alur Pengembangan

1. **Perencanaan**:
   - Menentukan fitur utama: Mood Tracker, Statistik, Self Test, Rekomendasi.
   - Memilih arsitektur dan teknologi pendukung.
2. **Pengembangan**:
   - Implementasi MVVM + Clean Architecture.
   - Integrasi Room, Firebase, OpenAI API, dan YouTube API.
   - Pembuatan UI dengan Jetpack Compose.
3. **Pengujian**:
   - Melakukan uji coba fitur-fitur utama.
   - Perbaikan UI berdasarkan feedback mentor.
4. **Dokumentasi & Deployment**:
   - Dokumentasi fitur dan struktur proyek di README.
   - Aplikasi diuji pada perangkat fisik dan emulator.

---

## 🔐 1. Autentikasi

### a. Login atau Registrasi
- Saat pertama kali membuka aplikasi, pengguna diarahkan ke halaman **Login**.
- Jika belum memiliki akun, klik **"Belum punya akun? Daftar"**.
- Isi formulir registrasi:
  - Email
  - Password (minimal 8 karakter)
  - Konfirmasi Password
- Tekan tombol Daftar untuk melanjutkan. 
- Jika sudah pernah melakukan registrasi, maka pengguna silahkan melakukan login dengan memasukkan alamat email dan password yang telah terdaftar. Tekan tombol Login untuk melanjutkan.


### b. Masuk ke Home Screen
- Setelah registrasi atau login berhasil, pengguna akan langsung masuk ke halaman **Home Screen**.
- Menu navigasi bawah terdiri dari:
  - Mood
  - Statistik
  - Self Test
  - Profile

---

## 😊 2. Mood Tracker

### a. Input Mood
- Pilih emoji yang sesuai dengan suasana hati Anda.
- (Opsional) Tambahkan catatan.
- Tekan tombol **"Catat Mood"** untuk menyimpan.

### b. Lihat Rekomendasi Aktivitas
- Setelah mencatat mood, pilih:
  - **"Lihat Rekomendasi"** → Menampilkan aktivitas sesuai mood
  - **"Tutup"** → Menutup tanpa melihat rekomendasi

### c. Tampilkan Kegiatan
- Di halaman rekomendasi:
  - Lihat aktivitas
  - Putar video meditasi
  - Dengarkan musik relaksasi

---

## 📊 3. Statistik Mood

### a. Melihat Grafik Mood
- Buka tab **Statistik** untuk melihat perubahan suasana hati dalam grafik.

### b. Pilih Rentang Waktu
- Tersedia opsi tampilan harian, mingguan, dan bulanan.

### c. Insight Statistik
- Aplikasi menampilkan insight berbasis data mood untuk membantu memahami pola emosi.

---

## 🧠 4. Self-Assessment

### a. Mengisi Pertanyaan
- Terdiri dari **10 pertanyaan** terkait kesehatan mental.
- Jawab seluruh pertanyaan dengan jujur.

### b. Lihat Hasil
- Setelah selesai, tekan **"Lihat Hasil"**.
- Tunggu beberapa saat untuk analisis.

### c. Hasil Analisis
- Hasil ditampilkan di bawah tombol, memberikan gambaran kondisi emosional Anda.

---

## 👤 5. Profile

### a. Mengedit Foto Profil dan Nama
- Di halaman **Profile**:
  - Tekan ikon kamera → Pilih foto dari galeri / kamera
  - Tekan ikon pensil → Ubah nama → Tekan **"Simpan"**

### b. Rekomendasi Aktivitas
- Tersedia tombol **"Rekomendasi"** berdasarkan mood terakhir.

---

## 🔔 6. Notifikasi Mood-Based

### a. Aktivasi Notifikasi
- Aplikasi memberikan:
  - **Pengingat harian**: Jam 09.00, 17.00, dan 21.00
  - **Notifikasi mingguan**: Setiap 7 hari berdasarkan pola mood

---

## 🤝 Kontribusi

Pull request dan masukan sangat terbuka. Silakan fork dan kontribusikan peningkatan!

---

## 📄 Lisensi

MoodTrack dirilis dengan lisensi MIT. Silakan gunakan dan modifikasi untuk keperluan pembelajaran dan sosial.
