# 📦 Database — Sistem Manajemen Karyawan

Folder ini berisi database dump asli (hasil `mysqldump`) dari aplikasi ini, supaya dosen/penguji bisa langsung setup database tanpa perlu registrasi akun manual satu per satu.

## Isi Folder

| File | Keterangan |
|---|---|
| `database.sql` | Dump lengkap (struktur tabel + data) dari database `db_karyawan`, hasil `mysqldump` langsung dari aplikasi yang sudah berjalan |

> ℹ️ File ini adalah **dump asli**, bukan skema buatan manual — sehingga dijamin 100% cocok dengan struktur yang digunakan aplikasi (termasuk seluruh kolom, tipe data, dan data karyawan/cuti yang sudah pernah diuji coba oleh penulis).

## Cara Import (Linux)

### 1. Buat database kosong terlebih dahulu

File `database.sql` **tidak** berisi perintah `CREATE DATABASE` (sesuai standar output `mysqldump` per-database), jadi database harus dibuat manual dulu:

```bash
sudo mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS db_karyawan CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 2. (Jika belum ada) Buat user & beri akses

Lewati langkah ini jika kamu sudah punya user `admin_karyawan` di MariaDB.

```bash
sudo mysql -u root -p -e "
CREATE USER IF NOT EXISTS 'admin_karyawan'@'localhost' IDENTIFIED BY 'admin123';
GRANT ALL PRIVILEGES ON db_karyawan.* TO 'admin_karyawan'@'localhost';
FLUSH PRIVILEGES;
"
```

### 3. Import dump ke database yang sudah dibuat

```bash
mysql -u admin_karyawan -p db_karyawan < database/database.sql
```

Masukkan password (`admin123`, atau sesuai yang dikonfigurasi).

### 4. Verifikasi

```bash
mysql -u admin_karyawan -p db_karyawan -e "SHOW TABLES; SELECT id, username, role FROM pengguna;"
```

Harus muncul 3 tabel (`pengguna`, `karyawan`, `cuti`) beserta akun `admin_hr` dan `okis`.

## Akun Default Setelah Import

| Role | Username | Password |
|---|---|---|
| HRD (Admin) | `admin_hr` | `pw55` |
| Karyawan | `okis` | `gg` |

Password di database sudah dalam bentuk **hash BCrypt** (bukan plain text), sesuai cara Spring Security memverifikasi login pada aplikasi ini.

## Setelah Import — Menjalankan Aplikasi

1. Pastikan `src/main/resources/application.properties` sudah mengarah ke `db_karyawan` dengan user/password yang sama seperti di atas.
2. Jalankan aplikasi:
   ```bash
   java -jar target/karyawan-0.0.1-SNAPSHOT.jar
   ```
3. Buka `http://localhost:8080/login.html` dan login dengan akun default di atas.

⚠️ **Untuk penggunaan di luar demo/tugas kuliah, segera ganti password default di atas.**
