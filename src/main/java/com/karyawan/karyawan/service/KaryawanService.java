package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.KaryawanRequestDTO;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.model.Pengguna;
import com.karyawan.karyawan.repository.KaryawanRepository;
import com.karyawan.karyawan.repository.PenggunaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KaryawanService {

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==========================================
    // CRUD & INTEGRASI AKUN (DTO)
    // ==========================================

    public List<Karyawan> getAllKaryawan() {
        return karyawanRepository.findAll();
    }

    public Karyawan getKaryawanById(long id) {
        return karyawanRepository.findById((int) id)
                .orElseThrow(() -> new RuntimeException("Karyawan tidak ditemukan"));
    }

    @Transactional
    public Karyawan tambahKaryawan(KaryawanRequestDTO dto) {
        // 1. Simpan Data Profil
        Karyawan karyawan = new Karyawan();
        karyawan.setNama(dto.getNama());
        karyawan.setDepartemen(dto.getDepartemen());
        karyawan.setGaji(dto.getGaji());
        Karyawan savedKaryawan = karyawanRepository.save(karyawan);

        // 2. Simpan Akun
        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty() && dto.getPassword() != null) {
            if (penggunaRepository.findByUsername(dto.getUsername()).isPresent()) {
                throw new RuntimeException("Username sudah terdaftar.");
            }
            Pengguna pengguna = new Pengguna();
            pengguna.setUsername(dto.getUsername());
            pengguna.setPassword(passwordEncoder.encode(dto.getPassword()));
            pengguna.setRole("ROLE_KARYAWAN");
            penggunaRepository.save(pengguna);
        }
        return savedKaryawan;
    }

    @Transactional
    public Karyawan updateKaryawan(long id, KaryawanRequestDTO dto) {
        // 1. Update Profil Karyawan
        Karyawan karyawan = getKaryawanById(id);
        karyawan.setNama(dto.getNama());
        karyawan.setDepartemen(dto.getDepartemen());
        karyawan.setGaji(dto.getGaji());
        Karyawan savedKaryawan = karyawanRepository.save(karyawan);

        // 2. Logika Penambahan atau Pembaruan Akun Pengguna
        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty() && dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            
            // Cek apakah akun dengan username tersebut sudah ada
            penggunaRepository.findByUsername(dto.getUsername()).ifPresentOrElse(
                penggunaExist -> {
                    // Jika username sudah ada, perbarui password-nya
                    penggunaExist.setPassword(passwordEncoder.encode(dto.getPassword()));
                    penggunaRepository.save(penggunaExist);
                },
                () -> {
                    // Jika username belum ada, buat akun baru
                    Pengguna penggunaBaru = new Pengguna();
                    penggunaBaru.setUsername(dto.getUsername());
                    penggunaBaru.setPassword(passwordEncoder.encode(dto.getPassword()));
                    penggunaBaru.setRole("ROLE_KARYAWAN"); // Asumsi default role dari dashboard
                    penggunaRepository.save(penggunaBaru);
                }
            );
        }
        return savedKaryawan;
    }

    public void deleteKaryawan(long id) {
        karyawanRepository.deleteById((int) id);
    }

    // ==========================================
    // LOGIKA STREAM, SET, DAN MAP (DIKEMBALIKAN)
    // ==========================================

    public List<Karyawan> getKaryawanByDepartemen(String departemen) {
        return karyawanRepository.findAll().stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .toList();
    }

    public List<Karyawan> getKaryawanTermahal() {
        return karyawanRepository.findAll().stream()
                .sorted((k1, k2) -> Double.compare(k2.getGaji(), k1.getGaji()))
                .toList();
    }

    public double getTotalGajiByDepartemen(String departemen) {
        return karyawanRepository.findAll().stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .mapToDouble(Karyawan::getGaji)
                .sum();
    }

    public Set<String> getDepartemenUnik() {
        return karyawanRepository.findAll().stream()
                .map(Karyawan::getDepartemen)
                .collect(Collectors.toSet());
    }

    public Map<String, List<Karyawan>> getKaryawanGrupByDepartemen() {
        return karyawanRepository.findAll().stream()
                .collect(Collectors.groupingBy(Karyawan::getDepartemen));
    }

    public List<Karyawan> getDaftarKaryawanTerbaru() {
        return new ArrayList<>(karyawanRepository.findAll().stream()
                .collect(Collectors.toMap(Karyawan::getId, k -> k, (exist, replace) -> replace))
                .values());
    }
}