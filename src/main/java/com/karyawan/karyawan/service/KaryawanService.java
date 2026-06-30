package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.KaryawanRequestDTO;
import com.karyawan.karyawan.exception.ResourceNotFoundException;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.model.Pengguna;
import com.karyawan.karyawan.repository.KaryawanRepository;
import com.karyawan.karyawan.repository.PenggunaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KaryawanService {

    
    private static final Logger log = LoggerFactory.getLogger(KaryawanService.class);

    @Autowired
    private KaryawanRepository karyawanRepository;

    @Autowired
    private PenggunaRepository penggunaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Karyawan> getAllKaryawan() {
        log.info("Mengambil seluruh data karyawan dari database");
        return karyawanRepository.findAll();
    }

    public Karyawan getKaryawanById(long id) {
        return karyawanRepository.findById((int) id)
                // Menggunakan Custom Exception
                .orElseThrow(() -> new ResourceNotFoundException("Data karyawan dengan ID " + id + " tidak ditemukan"));
    }

    public Karyawan getKaryawanByUsername(String username) {
        return karyawanRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Profil karyawan tidak ditemukan untuk username: " + username));
    }

    @Transactional
    public Karyawan tambahKaryawan(KaryawanRequestDTO dto) {
        log.info("Memulai proses penambahan karyawan baru: {}", dto.getNama());
        
        Karyawan karyawan = new Karyawan();
        karyawan.setNama(dto.getNama());
        karyawan.setDepartemen(dto.getDepartemen());
        karyawan.setGaji(dto.getGaji());
        karyawan.setUsername(dto.getUsername()); 
        
        Karyawan savedKaryawan = karyawanRepository.save(karyawan);

        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty() && dto.getPassword() != null) {
            if (penggunaRepository.findByUsername(dto.getUsername()).isPresent()) {
                log.warn("Gagal membuat akun, username {} sudah terdaftar", dto.getUsername());
                throw new RuntimeException("Username sudah terdaftar.");
            }
            Pengguna pengguna = new Pengguna();
            pengguna.setUsername(dto.getUsername());
            pengguna.setPassword(passwordEncoder.encode(dto.getPassword()));
            pengguna.setRole("ROLE_KARYAWAN");
            penggunaRepository.save(pengguna);
            log.info("Akun pengguna berhasil dibuat untuk username: {}", dto.getUsername());
        }
        return savedKaryawan;
    }

    @Transactional
    public Karyawan updateKaryawan(long id, KaryawanRequestDTO dto) {
        log.info("Memperbarui data karyawan dengan ID: {}", id);
        
        Karyawan karyawan = getKaryawanById(id);
        karyawan.setNama(dto.getNama());
        karyawan.setDepartemen(dto.getDepartemen());
        karyawan.setGaji(dto.getGaji());
        karyawan.setUsername(dto.getUsername());
        
        Karyawan savedKaryawan = karyawanRepository.save(karyawan);

        if (dto.getUsername() != null && !dto.getUsername().trim().isEmpty() && dto.getPassword() != null && !dto.getPassword().trim().isEmpty()) {
            penggunaRepository.findByUsername(dto.getUsername()).ifPresentOrElse(
                penggunaExist -> {
                    penggunaExist.setPassword(passwordEncoder.encode(dto.getPassword()));
                    penggunaRepository.save(penggunaExist);
                    log.info("Kata sandi berhasil diperbarui untuk username: {}", dto.getUsername());
                },
                () -> {
                    Pengguna penggunaBaru = new Pengguna();
                    penggunaBaru.setUsername(dto.getUsername());
                    penggunaBaru.setPassword(passwordEncoder.encode(dto.getPassword()));
                    penggunaBaru.setRole("ROLE_KARYAWAN");
                    penggunaRepository.save(penggunaBaru);
                    log.info("Akun baru berhasil diregistrasi saat update untuk username: {}", dto.getUsername());
                }
            );
        }
        return savedKaryawan;
    }

    public void deleteKaryawan(long id) {
        log.info("Menghapus data karyawan dengan ID: {}", id);
        Karyawan karyawan = getKaryawanById(id); 
        karyawanRepository.delete(karyawan);
    }

    
    public List<Karyawan> getKaryawanByDepartemen(String departemen) {
        return karyawanRepository.findAll().stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .toList();
    }

    public List<Karyawan> getKaryawanTermahal() {
        return karyawanRepository.findAll().stream()
                .sorted((k1, k2) -> k2.getGaji().compareTo(k1.getGaji()))
                .toList();
    }

    public BigDecimal getTotalGajiByDepartemen(String departemen) {
        return karyawanRepository.findAll().stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .map(k -> k.getGaji())
                .reduce(BigDecimal.ZERO, (a, b) -> a.add(b));
    }

    public Set<String> getDepartemenUnik() {
        return karyawanRepository.findAll().stream()
                .map(k -> k.getDepartemen())
                .collect(Collectors.toSet());
    }

    public Map<String, List<Karyawan>> getKaryawanGrupByDepartemen() {
        return karyawanRepository.findAll().stream()
                .collect(Collectors.groupingBy(k -> k.getDepartemen()));
    }

    public List<Karyawan> getDaftarKaryawanTerbaru() {
        return new ArrayList<>(karyawanRepository.findAll().stream()
                .collect(Collectors.toMap(k -> k.getId(), k -> k, (exist, replace) -> replace))
                .values());
    }
}