package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.KaryawanRequestDTO;
import com.karyawan.karyawan.dto.KaryawanResponseDTO;
import com.karyawan.karyawan.exception.ResourceNotFoundException;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.model.Pengguna;
import com.karyawan.karyawan.model.Role;
import com.karyawan.karyawan.repository.KaryawanRepository;
import com.karyawan.karyawan.repository.PenggunaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class KaryawanService {

    private static final Logger log = LoggerFactory.getLogger(KaryawanService.class);

    private final KaryawanRepository karyawanRepository;
    private final PenggunaRepository penggunaRepository;
    private final PasswordEncoder passwordEncoder;

    // Penerapan Constructor Injection
    public KaryawanService(KaryawanRepository karyawanRepository, 
                           PenggunaRepository penggunaRepository, 
                           PasswordEncoder passwordEncoder) {
        this.karyawanRepository = karyawanRepository;
        this.penggunaRepository = penggunaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Metode internal untuk mengambil Entity asli dari database (Digunakan oleh Update & Delete)
    private Karyawan findEntityById(long id) {
        return karyawanRepository.findById((int) id)
                .orElseThrow(() -> new ResourceNotFoundException("Data karyawan dengan ID " + id + " tidak ditemukan"));
    }

    public List<KaryawanResponseDTO> getAllKaryawan() {
        log.info("Mengambil seluruh data karyawan dari database");
        return karyawanRepository.findAll()
                .stream()
                .map(KaryawanResponseDTO::fromEntity) 
                .toList();
    }

    public KaryawanResponseDTO getKaryawanById(long id) {
        return KaryawanResponseDTO.fromEntity(findEntityById(id));
    }

    public KaryawanResponseDTO getKaryawanByUsername(String username) {
        Karyawan karyawan = karyawanRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Profil karyawan tidak ditemukan untuk username: " + username));
        return KaryawanResponseDTO.fromEntity(karyawan);
    }

    @Transactional
    public KaryawanResponseDTO tambahKaryawan(KaryawanRequestDTO dto) {
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
            pengguna.setRole(Role.KARYAWAN);
            penggunaRepository.save(pengguna);
            log.info("Akun pengguna berhasil dibuat untuk username: {}", dto.getUsername());
        }
        
        // Kembalikan DTO
        return KaryawanResponseDTO.fromEntity(savedKaryawan);
    }

    @Transactional
    public KaryawanResponseDTO updateKaryawan(long id, KaryawanRequestDTO dto) {
        log.info("Memperbarui data karyawan dengan ID: {}", id);
        
        // Memanggil fungsi internal untuk memanipulasi entitas
        Karyawan karyawan = findEntityById(id);
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
                    penggunaBaru.setRole(Role.KARYAWAN);
                    penggunaRepository.save(penggunaBaru);
                    log.info("Akun baru berhasil diregistrasi saat update untuk username: {}", dto.getUsername());
                }
            );
        }
        
        // Kembalikan DTO
        return KaryawanResponseDTO.fromEntity(savedKaryawan);
    }

    public void deleteKaryawan(long id) {
        log.info("Menghapus data karyawan dengan ID: {}", id);
        Karyawan karyawan = findEntityById(id); 
        karyawanRepository.delete(karyawan);
    }
    
    // --- FUNGSI ANALITIK (Dikonversi ke DTO) ---

    public List<KaryawanResponseDTO> getKaryawanByDepartemen(String departemen) {
        return karyawanRepository.findAll().stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .map(KaryawanResponseDTO::fromEntity)
                .toList();
    }

    public List<KaryawanResponseDTO> getKaryawanTermahal() {
        return karyawanRepository.findAll().stream()
                .sorted((k1, k2) -> k2.getGaji().compareTo(k1.getGaji()))
                .map(KaryawanResponseDTO::fromEntity)
                .toList();
    }

    public BigDecimal getTotalGajiByDepartemen(String departemen) {
        return karyawanRepository.findAll().stream()
                .filter(k -> k.getDepartemen().equalsIgnoreCase(departemen))
                .map(Karyawan::getGaji)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Set<String> getDepartemenUnik() {
        return karyawanRepository.findAll().stream()
                .map(Karyawan::getDepartemen)
                .collect(Collectors.toSet());
    }

    public Map<String, List<KaryawanResponseDTO>> getKaryawanGrupByDepartemen() {
        return karyawanRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        Karyawan::getDepartemen,
                        Collectors.mapping(KaryawanResponseDTO::fromEntity, Collectors.toList())
                ));
    }

    public List<KaryawanResponseDTO> getDaftarKaryawanTerbaru() {
        return karyawanRepository.findAll().stream()
                .collect(Collectors.toMap(Karyawan::getId, k -> k, (exist, replace) -> replace))
                .values()
                .stream()
                .map(KaryawanResponseDTO::fromEntity)
                .toList();
    }
}