package com.karyawan.karyawan.service;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.repository.KaryawanRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class KaryawanService {

    private final KaryawanRepository repository;

    public KaryawanService(KaryawanRepository repository) {
        this.repository = repository;
    }

    public List<Karyawan> getAllKaryawan() { return repository.findAll(); }

    public Karyawan getKaryawanById(int id) { return repository.findById(id).orElse(null); }

    public String tambahKaryawan(Karyawan karyawan) {
        repository.save(karyawan);
        return "Berhasil menambah karyawan ke database: " + karyawan.getNama();
    }

    public String updateKaryawan(int id, Karyawan dataBaru) {
        return repository.findById(id).map(k -> {
            k.setNama(dataBaru.getNama());
            k.setDepartemen(dataBaru.getDepartemen());
            k.setGaji(dataBaru.getGaji());
            repository.save(k);
            return "Berhasil memperbarui karyawan ID: " + id;
        }).orElse("Karyawan dengan ID " + id + " tidak ditemukan.");
    }

    public String deleteKaryawan(int id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "Karyawan ID " + id + " berhasil dihapus dari database.";
        }
        return "Gagal menghapus: ID tidak ditemukan.";
    }

    // --- IMPLEMENTASI STREAM API ---
    public List<Karyawan> getKaryawanByDepartemen(String departemen) {
        return repository.findAll().stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .toList();
    }

    public List<Karyawan> getKaryawanTermahal() {
        return repository.findAll().stream()
                .sorted(Comparator.comparingDouble(Karyawan::getGaji).reversed())
                .toList();
    }

    public double getTotalGajiByDepartemen(String departemen) {
        return repository.findAll().stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .mapToDouble(Karyawan::getGaji)
                .sum();
    }

    public Set<String> getDepartemenUnik() {
        return repository.findAll().stream().map(Karyawan::getDepartemen).collect(Collectors.toSet());
    }

    public Map<String, List<Karyawan>> getKaryawanGrupByDepartemen() {
        return repository.findAll().stream().collect(Collectors.groupingBy(Karyawan::getDepartemen));
    }

    public List<Karyawan> getDaftarKaryawanTerbaru() {
        return new ArrayList<>(
            repository.findAll().stream()
                .collect(Collectors.toMap(Karyawan::getId, k -> k, (existing, replacement) -> replacement))
                .values()
        );
    }
}