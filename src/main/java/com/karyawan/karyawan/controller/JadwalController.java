package com.karyawan.karyawan.controller;

import com.karyawan.karyawan.dto.JadwalResponseDTO;
import com.karyawan.karyawan.service.JadwalService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import java.time.LocalTime;

@RestController
@RequestMapping("/api/jadwal")
public class JadwalController {

    private final JadwalService jadwalService;

    public JadwalController(JadwalService jadwalService) {
        this.jadwalService = jadwalService;
    }

    // Endpoint untuk Front-End FullCalendar (Karyawan)
    @GetMapping("/me")
    @PreAuthorize("hasRole('KARYAWAN')")
    public ResponseEntity<List<JadwalResponseDTO>> getJadwalSaya(Authentication authentication) {
        return ResponseEntity.ok(jadwalService.getJadwalSaya(authentication.getName()));
    }

    // Endpoint menangkap klik tombol Clock-In
    @PostMapping("/clock-in")
    @PreAuthorize("hasRole('KARYAWAN')")
    public ResponseEntity<?> clockIn(Authentication authentication, @RequestBody Map<String, String> payload) {
        try {
            String koordinat = payload.get("lokasi");
            JadwalResponseDTO response = jadwalService.prosesClockIn(authentication.getName(), koordinat);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint menangkap klik tombol Clock-Out
    @PostMapping("/clock-out")
    @PreAuthorize("hasRole('KARYAWAN')")
    public ResponseEntity<?> clockOut(Authentication authentication) {
        try {
            JadwalResponseDTO response = jadwalService.prosesClockOut(authentication.getName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint khusus HRD untuk menyuntikkan jadwal mingguan ke semua karyawan
    @PostMapping("/generate")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<?> generateJadwalMingguan(@RequestBody Map<String, String> payload) {
        try {
            // HRD mengirim JSON: { "tanggalMulai": "2026-07-06" } (Contoh Senin)
            LocalDate tanggalMulai = LocalDate.parse(payload.get("tanggalMulai"));
            jadwalService.generateJadwalMingguan(tanggalMulai);
            return ResponseEntity.ok(Map.of("message", "Jadwal 5 hari kerja berhasil di-generate untuk semua karyawan"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Format tanggal harus YYYY-MM-DD"));
        }
    }

    @GetMapping("/harian")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<List<JadwalResponseDTO>> getJadwalHarian(@RequestParam(required = false) String tanggal) {
        LocalDate targetDate;
        if (tanggal != null && !tanggal.isEmpty()) {
            targetDate = LocalDate.parse(tanggal);
        } else {
            
            targetDate = LocalDate.now(ZoneId.of("Asia/Jakarta")); 
        }
        return ResponseEntity.ok(jadwalService.getJadwalHarian(targetDate));
    }


    @GetMapping("/semua")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<List<JadwalResponseDTO>> getAllJadwal() {
        return ResponseEntity.ok(jadwalService.getAllJadwal());
    }

    @PutMapping("/{id}/intervensi")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<JadwalResponseDTO> updateJadwal(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload) {
        
        LocalTime masuk = LocalTime.parse(payload.get("jamMasuk"));
        LocalTime pulang = LocalTime.parse(payload.get("jamPulang"));
        String status = payload.get("status");
        
        return ResponseEntity.ok(jadwalService.intervensiJadwal(id, masuk, pulang, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<Void> deleteJadwal(@PathVariable Long id) {
        jadwalService.hapusJadwal(id);
        return ResponseEntity.ok().build();
    }
}