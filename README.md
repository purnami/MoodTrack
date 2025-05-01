# 📱 MoodTrack - Panduan Penggunaan Aplikasi

MoodTrack adalah aplikasi kesehatan mental yang membantu pengguna melacak suasana hati, mendapatkan rekomendasi aktivitas, melakukan self-assessment, dan menganalisis pola emosi berdasarkan data.

---

## 🔐 1. Autentikasi

### a. Login atau Registrasi
- Saat pertama kali membuka aplikasi, pengguna diarahkan ke halaman **Login**.
- Jika belum memiliki akun, klik **"Belum punya akun? Daftar"**.
- Isi formulir registrasi:
  - Email
  - Password (minimal 8 karakter)
  - Konfirmasi Password
- Setelah registrasi berhasil, pengguna diarahkan kembali ke halaman login.

### b. Masuk ke Home Screen
- Setelah login berhasil, pengguna akan langsung masuk ke halaman **Home Screen**.
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

## 📂 Dokumentasi Tambahan

- Pastikan Anda telah menginstal semua dependensi sebelum menjalankan aplikasi.
- Aplikasi dibangun dengan:
  - Flutter + Cubit + GetIt
  - Firebase Authentication & Firestore
  - Room (untuk mood offline)
  - WorkManager (notifikasi otomatis)
  - MPAndroidChart, AudioDB API, YouTube API (untuk rekomendasi multimedia)
