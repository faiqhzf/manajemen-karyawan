package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.JadwalResponseDTO;
import com.karyawan.karyawan.model.JadwalKerja;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.repository.JadwalRepository;
import com.karyawan.karyawan.repository.KaryawanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class JadwalService {

    private final JadwalRepository jadwalRepository;
    private final KaryawanRepository karyawanRepository;

    public JadwalService(JadwalRepository jadwalRepository, KaryawanRepository karyawanRepository) {
        this.jadwalRepository = jadwalRepository;
        this.karyawanRepository = karyawanRepository;
    }

    public List<JadwalResponseDTO> getJadwalSaya(String username) {
        return jadwalRepository.findByUsernameKaryawan(username)
                .stream()
                .map(JadwalResponseDTO::fromEntity)
                .toList();
    }
    
    public List<JadwalResponseDTO> getJadwalHarian(LocalDate tanggal) {
        return jadwalRepository.findByTanggal(tanggal)
                .stream()
                .map(JadwalResponseDTO::fromEntity)
                .toList();
    }

    // --- TAMBAHAN BARU 1: Tarik Semua Jadwal untuk Master Calendar HRD ---
    public List<JadwalResponseDTO> getAllJadwal() {
        return jadwalRepository.findAll().stream()
                .map(JadwalResponseDTO::fromEntity)
                .toList();
    }

    // --- TAMBAHAN BARU 2: Intervensi (Ubah Jam / Status Jadwal) ---
    @Transactional
    public JadwalResponseDTO intervensiJadwal(Long id, LocalTime jamMasuk, LocalTime jamPulang, String status) {
        JadwalKerja jadwal = jadwalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jadwal dengan ID tersebut tidak ditemukan"));
        
        jadwal.setJamMasukShift(jamMasuk);
        jadwal.setJamPulangShift(jamPulang);
        
        if (status != null && !status.trim().isEmpty()) {
            jadwal.setStatus(status);
        }
        
        return JadwalResponseDTO.fromEntity(jadwalRepository.save(jadwal));
    }

    // --- TAMBAHAN BARU 3: Intervensi (Hapus Jadwal Individu) ---
    @Transactional
    public void hapusJadwal(Long id) {
        jadwalRepository.deleteById(id);
    }

    // Fungsi HRD: Membuat jadwal default seminggu (Senin-Jumat) untuk semua karyawan
    @Transactional
    public void generateJadwalMingguan(LocalDate tanggalMulai) {
        List<Karyawan> semuaKaryawan = karyawanRepository.findAll();
        LocalTime shiftMasuk = LocalTime.of(8, 0);   // Jam 08:00
        LocalTime shiftPulang = LocalTime.of(17, 0); // Jam 17:00

        for (Karyawan k : semuaKaryawan) {
            for (int i = 0; i < 5; i++) { // Generate untuk 5 hari (Senin-Jumat)
                LocalDate tanggal = tanggalMulai.plusDays(i);
                

                if (jadwalRepository.findByUsernameKaryawanAndTanggal(k.getUsername(), tanggal).isEmpty()) {
                    JadwalKerja jadwal = new JadwalKerja();
                    jadwal.setUsernameKaryawan(k.getUsername());
                    jadwal.setTanggal(tanggal);
                    jadwal.setJamMasukShift(shiftMasuk);
                    jadwal.setJamPulangShift(shiftPulang);
                    jadwal.setStatus("BELUM_MULAI");
                    jadwalRepository.save(jadwal);
                }
            }
        }
    }

    @Transactional
    public JadwalResponseDTO prosesClockIn(String username, String koordinat) {
        // Menggunakan Timezone WIB agar stabil meski di-deploy di cloud internasional
        ZoneId zonaWib = ZoneId.of("Asia/Jakarta");
        LocalDate hariIni = LocalDate.now(zonaWib);
        LocalTime waktuSekarang = LocalTime.now(zonaWib);

        JadwalKerja jadwalHariIni = jadwalRepository.findByUsernameKaryawanAndTanggal(username, hariIni)
                .orElseThrow(() -> new RuntimeException("Tidak ada jadwal shift kerja untuk Anda hari ini."));

        if (!jadwalHariIni.getStatus().equals("BELUM_MULAI")) {
            throw new RuntimeException("Anda sudah merekam absensi masuk hari ini.");
        }

        // Kalkulasi Keterlambatan (Toleransi 15 Menit)
        LocalTime batasToleransi = jadwalHariIni.getJamMasukShift().plusMinutes(15);
        if (waktuSekarang.isAfter(batasToleransi)) {
            jadwalHariIni.setStatus("TERLAMBAT");
        } else {
            jadwalHariIni.setStatus("HADIR");
        }

        jadwalHariIni.setWaktuCheckIn(waktuSekarang);
        jadwalHariIni.setKordinatLokasi(koordinat);

        return JadwalResponseDTO.fromEntity(jadwalRepository.save(jadwalHariIni));
    }

    @Transactional
    public JadwalResponseDTO prosesClockOut(String username) {
        ZoneId zonaWib = ZoneId.of("Asia/Jakarta");
        LocalDate hariIni = LocalDate.now(zonaWib);
        LocalTime waktuSekarang = LocalTime.now(zonaWib);

        JadwalKerja jadwalHariIni = jadwalRepository.findByUsernameKaryawanAndTanggal(username, hariIni)
                .orElseThrow(() -> new RuntimeException("Tidak ada jadwal shift kerja untuk Anda hari ini."));

        if (jadwalHariIni.getWaktuCheckIn() == null) {
            throw new RuntimeException("Anda belum melakukan Clock-In hari ini.");
        }

        jadwalHariIni.setWaktuCheckOut(waktuSekarang);
        return JadwalResponseDTO.fromEntity(jadwalRepository.save(jadwalHariIni));
    }
}