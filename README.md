<div align="center">

# 👥 Sistem Manajemen Karyawan

**Aplikasi web manajemen data karyawan & pengajuan cuti berbasis Java Spring Boot, JWT Authentication, dan MariaDB.**

Dibuat untuk memenuhi Ujian Akhir Semester (UAS) mata kuliah **Pemrograman Java Lanjut**.

![Java](https://img.shields.io/badge/Java-26-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen?logo=springboot&logoColor=white)
![MariaDB](https://img.shields.io/badge/MariaDB-Database-003545?logo=mariadb&logoColor=white)
![Maven](https://img.shields.io/badge/Build-Maven-C71A36?logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

[Download](#-download) • [Instalasi](#-cara-install-dari-nol) • [Dokumentasi](#-dokumentasi) • [Disclaimer](#-disclaimer)

</div>

---

## 📖 Deskripsi

**Sistem Manajemen Karyawan** adalah aplikasi web yang memungkinkan divisi HRD mengelola data karyawan (tambah, ubah, hapus, filter, rekap gaji) serta memungkinkan karyawan mengajukan cuti dan memantau statusnya secara mandiri. Autentikasi menggunakan **JSON Web Token (JWT)** dengan dua peran (*role*) pengguna: **HRD** dan **KARYAWAN**, masing-masing memiliki dasbor dan hak akses berbeda.

Project ini dibangun sebagai studi kasus penerapan Spring Boot (REST API), Spring Security, Spring Data JPA, dan MariaDB dalam sebuah aplikasi full-stack sederhana.

---

## ✨ Fitur

### 🔐 Autentikasi & Otorisasi
- Login & registrasi akun dengan JWT (stateless authentication).
- Password di-hash menggunakan **BCrypt** (tidak pernah disimpan sebagai plain text).
- Role-based access control: **HRD** dan **KARYAWAN**.

### 🧑‍💼 Modul HRD (Admin)
- Tambah, ubah, dan hapus data karyawan.
- Lihat seluruh data karyawan dan detail per ID.
- Filter karyawan berdasarkan departemen.
- Urutkan karyawan berdasarkan gaji tertinggi.
- Rekap total beban gaji per departemen (format Rupiah).
- Lihat daftar departemen unik & pengelompokan karyawan per departemen.
- Lihat daftar karyawan terbaru yang ditambahkan.
- Tinjau, setujui, atau tolak pengajuan cuti karyawan.

### 👤 Modul Karyawan
- Lihat profil diri sendiri (`/api/karyawan/me`).
- Ajukan cuti baru (tanggal mulai, tanggal selesai, alasan).
- Lihat riwayat pengajuan cuti pribadi beserta statusnya (Menunggu / Disetujui / Ditolak).

### 🌐 Antarmuka Web
- Halaman login terpisah dari dasbor.
- Dasbor eksekutif untuk HRD (`admin-hr.html`).
- Portal pegawai untuk karyawan (`karyawan.html`).
- Dokumentasi API otomatis via **Swagger UI** (springdoc-openapi).

---

## 🛠️ Teknologi

| Kategori | Teknologi |
|---|---|
| Bahasa | Java 26 |
| Framework | Spring Boot 4.0.6 |
| Keamanan | Spring Security + JWT (`jjwt` 0.12.5) |
| Database | MariaDB |
| ORM | Spring Data JPA (Hibernate) |
| Build Tool | Maven (Maven Wrapper disertakan) |
| Frontend | HTML + Tailwind CSS (CDN) |
| Dokumentasi API | springdoc-openapi (Swagger UI) |
| Template Engine | Thymeleaf |

---

## 🚀 Cara Install (Dari Nol)

Panduan ini diasumsikan menggunakan **Linux** (Ubuntu/Debian-based).

### 1. Prasyarat

Install Java Development Kit, Git, dan MariaDB terlebih dahulu.

```bash
# Update package list
sudo apt update

# Install JDK (sesuaikan versi yang tersedia di distro Anda, minimal JDK 21+)
sudo apt install openjdk-21-jdk -y

# Install Git
sudo apt install git -y

# Install MariaDB Server
sudo apt install mariadb-server -y
sudo systemctl enable mariadb
sudo systemctl start mariadb
```

Cek versi yang terpasang:

```bash
java -version
git --version
mariadb --version
```

> 💡 Project ini tidak memerlukan instalasi Maven secara terpisah karena sudah disertakan **Maven Wrapper** (`mvnw`).

### 2. Clone Repository

```bash
git clone https://github.com/faiqhzf/manajemen-karyawan.git
cd manajemen-karyawan
```

### 3. Berikan Izin Eksekusi pada Maven Wrapper (Linux)

```bash
chmod +x mvnw
```

### 4. Lanjutkan ke Konfigurasi Database

Ikuti langkah pada bagian [Konfigurasi Database](#-konfigurasi-database) di bawah sebelum menjalankan aplikasi.

---

## 🗄️ Konfigurasi Database

### Nama Database

```
db_karyawan
```

### 1. Amankan MariaDB & Buat User (Opsional tapi Direkomendasikan)

```bash
sudo mysql_secure_installation
```

### 2. Buat Database dan User Khusus Aplikasi

Login ke MariaDB:

```bash
sudo mysql -u root -p
```

Jalankan perintah berikut di dalam prompt MariaDB:

```sql
CREATE DATABASE IF NOT EXISTS db_karyawan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'admin_karyawan'@'localhost' IDENTIFIED BY 'admin123';
GRANT ALL PRIVILEGES ON db_karyawan.* TO 'admin_karyawan'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

### 3. Import File SQL

Repository ini menyertakan file skema & data awal di folder [`/database`](./database). Import dengan perintah:

```bash
mysql -u admin_karyawan -p db_karyawan < database/database.sql
```

Masukkan password `admin123` (atau password yang Anda tentukan sendiri) saat diminta.

> ℹ️ Detail lengkap dan penjelasan isi file SQL tersedia di [`database/README.md`](./database/README.md). Perlu dicatat bahwa aplikasi juga mendukung **auto-generate tabel** melalui Hibernate (`ddl-auto=update`), sehingga import manual ini sifatnya opsional namun disarankan agar akun default & data contoh langsung tersedia.

### 4. Konfigurasi `application.properties`

File konfigurasi berada di:

```
src/main/resources/application.properties
```

Isinya sudah disiapkan sebagai berikut — sesuaikan `username` dan `password` jika Anda menggunakan kredensial database yang berbeda dari langkah di atas:

```properties
spring.application.name=karyawan

# Konfigurasi Koneksi MariaDB
spring.datasource.url=jdbc:mariadb://localhost:3306/db_karyawan
spring.datasource.username=admin_karyawan
spring.datasource.password=admin123
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

# Konfigurasi Hibernate / JPA (Otomatis membuat tabel dari Model)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=8a2b5c7d9e1f3g5h7j9k2l4m6n8p0q2r4s6t8u0v2w4x6y8z0A2B4C6D8E0F2G4H
jwt.expiration=86400000
# 86400000 ms = 24 Jam masa aktif token
```

> ⚠️ **Penting:** Nilai `spring.datasource.password` dan `jwt.secret` pada file ini hanya contoh untuk keperluan demo/tugas kuliah. **Jangan gunakan nilai yang sama pada lingkungan produksi.** Sebaiknya pindahkan nilai sensitif ke *environment variable* atau file konfigurasi terpisah yang tidak ikut di-commit (lihat `.gitignore`).

---

## 🔑 Login Default

Setelah mengimpor `database/database.sql`, dua akun berikut sudah tersedia untuk keperluan demo:

| Role | Username | Password | Akses |
|---|---|---|---|
| **HRD (Admin)** | `admin_hr` | `pw55` | Dasbor eksekutif — kelola karyawan & cuti |
| **Karyawan** | `okis` | `gg` | Portal pegawai — profil & pengajuan cuti |

> ⚠️ Akun di atas adalah **akun dummy untuk demonstrasi/penilaian tugas**. Password ini sengaja dibuat sederhana agar mudah diuji oleh dosen/penguji, sehingga **tidak direkomendasikan untuk digunakan di lingkungan nyata (production)**.

Jika ingin membuat akun baru, gunakan endpoint registrasi:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user_baru","password":"passwordAman123","role":"KARYAWAN"}'
```

---

## ▶️ Cara Menjalankan Project

Pastikan MariaDB sudah berjalan dan database sudah dikonfigurasi (lihat bagian sebelumnya).

### Menjalankan via Maven Wrapper (Development)

```bash
./mvnw spring-boot:run
```

### Atau, Build Lalu Jalankan sebagai JAR

```bash
./mvnw clean package -DskipTests
java -jar target/karyawan-0.0.1-SNAPSHOT.jar
```

Setelah aplikasi berjalan, akses melalui browser:

| Halaman/Endpoint | URL |
|---|---|
| Halaman Login | `http://localhost:8080/login.html` |
| Dasbor HRD | `http://localhost:8080/admin-hr.html` |
| Portal Karyawan | `http://localhost:8080/karyawan.html` |
| Swagger UI (Dokumentasi API) | `http://localhost:8080/swagger-ui/index.html` |

Untuk menghentikan aplikasi, tekan `Ctrl + C` pada terminal.

---

## 📚 Dokumentasi

- 📄 **PDF Laporan UAS**: [Lihat/Unduh Laporan](../../releases/latest) *(lampirkan pada GitHub Release — lihat bagian [Download](#-download))*
- 📑 **Dokumentasi API (Swagger/OpenAPI)**: tersedia otomatis saat aplikasi berjalan di `/swagger-ui/index.html`
- 🗄️ **Dokumentasi Database**: [`database/README.md`](./database/README.md)

> Untuk mengunggah PDF laporan, gunakan fitur **GitHub Release** (lihat panduan di bagian Download di bawah) agar tautan tetap stabil dan tidak membebani ukuran repository.

---

## 📥 Download

<div align="center">

[![Source Code](https://img.shields.io/badge/⬇️_Source_Code-.zip-informational?style=for-the-badge)](https://github.com/faiqhzf/manajemen-karyawan/archive/refs/heads/main.zip)
[![Latest Release](https://img.shields.io/github/v/release/faiqhzf/manajemen-karyawan?style=for-the-badge&label=Latest%20Release)](https://github.com/faiqhzf/manajemen-karyawan/releases/latest)
[![PDF Dokumentasi](https://img.shields.io/badge/📄_PDF-Laporan_UAS-red?style=for-the-badge)](https://github.com/faiqhzf/manajemen-karyawan/releases/latest)

</div>

| Jenis Unduhan | Tautan | Keterangan |
|---|---|---|
| Source Code (.zip) | [Download ZIP](https://github.com/faiqhzf/manajemen-karyawan/archive/refs/heads/main.zip) | Kode sumber terbaru dari branch `main` |
| GitHub Release | [Lihat Releases](https://github.com/faiqhzf/manajemen-karyawan/releases) | Versi rilis + JAR/PDF terlampir (jika tersedia) |
| PDF Laporan | *lihat halaman Release* | Dokumentasi/laporan UAS dalam format PDF |

### Cara Membuat GitHub Release (untuk pemilik repo)

1. Buka repo di GitHub → tab **Releases** → **Draft a new release**.
2. Isi **Tag version** (contoh: `v1.0.0`) dan **Release title**.
3. Pada bagian **Assets**, unggah file tambahan seperti:
   - `laporan-uas.pdf` (laporan/dokumentasi)
   - `manajemen-karyawan.jar` (JAR executable hasil build, opsional)
4. Klik **Publish release**. Tautan pada badge "Latest Release" dan "PDF Dokumentasi" di atas akan otomatis mengarah ke rilis tersebut.

---

## 📦 Packaging Project

Berikut perbandingan beberapa opsi packaging untuk mendistribusikan project ini, beserta rekomendasi kapan masing-masing cocok digunakan:

| Opsi | Kelebihan | Kekurangan | Cocok Untuk |
|---|---|---|---|
| **1. Source Code saja** (`git clone`) | Ukuran kecil, mudah dikembangkan lebih lanjut, versi selalu terbaru | Butuh setup manual (JDK, MariaDB, Maven), tidak langsung bisa dijalankan | Developer lain / evaluasi kode oleh dosen |
| **2. ZIP Project** (source tanpa `.git`) | Praktis dibagikan lewat email/LMS, tidak butuh Git | Tetap butuh setup environment sebelum dijalankan | Pengumpulan tugas via platform kampus (SIAKAD/LMS) |
| **3. JAR Executable** (`mvnw clean package`) | Satu file `.jar`, tinggal `java -jar` tanpa perlu build ulang | Tetap butuh JDK & MariaDB terinstal di mesin target | Demo langsung ke penguji tanpa perlu build dari source |
| **4. Installer** (native installer via `jpackage`) | Pengalaman instalasi seperti aplikasi desktop pada umumnya | Proses pembuatan lebih kompleks, installer spesifik per OS (Linux/Windows/Mac berbeda) | Distribusi ke pengguna non-teknis (jarang dipakai untuk tugas kuliah backend/API) |
| **5. Portable Application** (JAR + JRE bundel / Docker image) | Tidak butuh instalasi JDK terpisah di mesin target, konsisten di berbagai environment | Ukuran besar (menyertakan runtime), butuh Docker/JRE bundling tool | Deployment ke server, atau dibagikan ke penguji tanpa JDK terpasang |

### Rekomendasi

- **Untuk pengumpulan tugas kuliah**: gunakan kombinasi **Source Code (ZIP)** + **JAR Executable** + **PDF Laporan**, diunggah sebagai **GitHub Release**. Ini paling praktis: dosen bisa langsung menjalankan JAR tanpa build, atau meninjau source code bila diperlukan.
- **Untuk portofolio/demo publik**: cukup **Source Code** di repo + `README.md` yang jelas (seperti ini).
- **Installer** dan **Docker/portable image** umumnya berlebihan untuk skala project UAS, kecuali diminta khusus oleh dosen sebagai bagian penilaian DevOps.

Contoh membuat JAR executable:

```bash
./mvnw clean package -DskipTests
# hasil ada di: target/karyawan-0.0.1-SNAPSHOT.jar
java -jar target/karyawan-0.0.1-SNAPSHOT.jar
```

---

## ⚠️ Disclaimer

- Project ini dibuat **khusus untuk memenuhi tugas akhir/mata kuliah Pemrograman Java Lanjut** dan bersifat akademis.
- Aplikasi ini **tidak ditujukan untuk penggunaan production** — konfigurasi keamanan (seperti `jwt.secret`, kredensial database, dan akun default) masih dalam bentuk sederhana untuk keperluan demonstrasi.
- Sebagian data yang digunakan (contoh karyawan, pengajuan cuti, akun login) merupakan **data dummy/fiktif** dan tidak merepresentasikan individu atau organisasi nyata.
- Kode sumber disediakan **apa adanya (as-is)** tanpa jaminan bebas dari bug atau kerentanan keamanan.
- Hak cipta atas kode sumber ini dimiliki oleh penulis, dan penggunaan lebih lanjut tunduk pada ketentuan [Lisensi MIT](#-lisensi) di bawah.

---

## 📄 Lisensi

Project ini dilisensikan di bawah **MIT License**. Lihat file [`LICENSE`](./LICENSE) untuk detail lengkap.

```
MIT License — Copyright (c) 2026 Faiq Hudzaifah
```

---

<div align="center">

Dibuat dengan ☕ dan Spring Boot — untuk memenuhi tugas UAS Pemrograman Java Lanjut.

</div>
