package com.karyawan.karyawan.service;

import com.karyawan.karyawan.model.Karyawan;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.*;

@Service
public class KaryawanService {
    private final List<Karyawan> daftarKaryawan = new ArrayList<>();

    // Constructor otomatis mengisi data awal (Seeding)
    public KaryawanService() {
        daftarKaryawan.add(new Karyawan(1, "Andi", "IT", 8000000));
        daftarKaryawan.add(new Karyawan(2, "Budi", "Finance", 7500000));
        daftarKaryawan.add(new Karyawan(3, "Citra", "IT", 9500000));
        daftarKaryawan.add(new Karyawan(4, "Dewi", "HR", 6000000));
    }

    // CREATE: Tambah data
    public String tambahKaryawan(Karyawan karyawan) {
        daftarKaryawan.add(karyawan);
        return "Berhasil menambah karyawan: " + karyawan.getNama();
    }

    // READ: Tampilkan semua data (Struktur List standar)
    public List<Karyawan> getAllKaryawan() {
        return daftarKaryawan;
    }


    // ==========================================
    // IMPLEMENTASI STREAM API
    // ==========================================

    // 1. STREAM FILTER: Menampilkan data terstruktur berdasarkan Departemen
    public List<Karyawan> getKaryawanByDepartemen(String departemen) {
        return daftarKaryawan.stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .toList(); 
    }

    // 2. STREAM SORTED: Mengurutkan data berdasarkan Gaji Tertinggi ke Terendah
    public List<Karyawan> getKaryawanTermahal() {
        return daftarKaryawan.stream()
                .sorted(Comparator.comparingDouble(Karyawan::getGaji).reversed())
                .toList();
    }

    // 3. STREAM MAP & FILTER: Mencari total pengeluaran gaji untuk satu departemen
    public double getTotalGajiByDepartemen(String departemen) {
        return daftarKaryawan.stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .mapToDouble(Karyawan::getGaji)
                .sum();
    }

    // ==========================================
    // IMPLEMENTASI SET & MAP (STREAM API)
    // ==========================================

    // 4. SET: Mengambil daftar departemen yang unik (tanpa duplikat)
    public Set<String> getDepartemenUnik() {
        return daftarKaryawan.stream()
                .map(Karyawan::getDepartemen) // Ekstrak hanya nama departemennya saja
                .collect(Collectors.toSet()); // Masukkan ke dalam Set agar yang ganda dibuang
    }

    // 5. MAP: Mengelompokkan data karyawan berdasarkan departemennya
    public Map<String, List<Karyawan>> getKaryawanGrupByDepartemen() {
        return daftarKaryawan.stream()
                .collect(Collectors.groupingBy(Karyawan::getDepartemen)); 
                // Key = String (Departemen), Value = List<Karyawan>
    }

    // UPDATE: Mengubah data karyawan berdasarkan ID
    public String updateKaryawan(int id, Karyawan dataBaru) {
        for (int i = 0; i < daftarKaryawan.size(); i++) {
            if (daftarKaryawan.get(i).getId() == id) {
                daftarKaryawan.set(i, dataBaru);
                return "Berhasil memperbarui karyawan ID: " + id;
            }
        }
        return "Karyawan dengan ID " + id + " tidak ditemukan.";
    }

    // DELETE: Menghapus karyawan berdasarkan ID
    public String deleteKaryawan(int id) {
        boolean removed = daftarKaryawan.removeIf(k -> k.getId() == id);
        return removed ? "Karyawan ID " + id + " berhasil dihapus." : "Gagal menghapus: ID tidak ditemukan.";
    }

    // ==========================================
    // LOGIKA STREAM API: HANDLING DUPLICATE ID
    // ==========================================
    
    // Menampilkan daftar karyawan di mana jika ada ID yang sama, hanya yang terbaru yang muncul
    public List<Karyawan> getDaftarKaryawanTerbaru() {
        return new ArrayList<>(
            daftarKaryawan.stream()
                .collect(Collectors.toMap(
                    Karyawan::getId,           // Key: ID Karyawan
                    k -> k,                    // Value: Objek Karyawan itu sendiri
                    (existing, replacement) -> replacement // Jika ID sama, ambil yang paling baru (replacement)
                ))
                .values()
        );
    }

    // GET: Mengambil satu karyawan spesifik berdasarkan ID menggunakan Stream API
    public Karyawan getKaryawanById(int id) {
        return daftarKaryawan.stream()
                .filter(k -> k.getId() == id)
                .findFirst()
                .orElse(null); // Mengembalikan null jika ID tidak ditemukan
    }
}
