package com.karyawan.karyawan.controller;

import com.karyawan.karyawan.dto.ChangePasswordRequestDTO;
import com.karyawan.karyawan.dto.KaryawanRequestDTO;
import com.karyawan.karyawan.dto.KaryawanResponseDTO;
import com.karyawan.karyawan.dto.UpdateProfilRequestDTO;
import com.karyawan.karyawan.service.KaryawanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/karyawan")
public class KaryawanController {

    private static final Logger log = LoggerFactory.getLogger(KaryawanController.class);

    private final KaryawanService service;

    public KaryawanController(KaryawanService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<List<KaryawanResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAllKaryawan());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<KaryawanResponseDTO> getById(@PathVariable long id) {
        return ResponseEntity.ok(service.getKaryawanById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<KaryawanResponseDTO> addKaryawan(@Valid @RequestBody KaryawanRequestDTO dto) {
        log.info("Menerima request penambahan karyawan: {}", dto.getNama());
        KaryawanResponseDTO karyawan = service.tambahKaryawan(dto);
        return ResponseEntity.ok(karyawan);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<KaryawanResponseDTO> edit(@PathVariable long id, @Valid @RequestBody KaryawanRequestDTO dto) {
        log.info("Menerima request update karyawan ID: {}", id);
        KaryawanResponseDTO karyawan = service.updateKaryawan(id, dto);
        return ResponseEntity.ok(karyawan);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("Menerima request hapus karyawan ID: {}", id);
        service.deleteKaryawan(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/me")
    public ResponseEntity<KaryawanResponseDTO> getMyProfile(Authentication authentication) {
        String currentUsername = authentication.getName();
        log.info("Menerima request cek profil untuk user: {}", currentUsername);
        KaryawanResponseDTO karyawan = service.getKaryawanByUsername(currentUsername);
        return ResponseEntity.ok(karyawan);
    }

    @GetMapping("/filter/{departemen}")
    @PreAuthorize("hasRole('HRD')")
    public List<KaryawanResponseDTO> filterDepartemen(@PathVariable String departemen) {
        return service.getKaryawanByDepartemen(departemen);
    }

    @GetMapping("/gaji-tertinggi")
    @PreAuthorize("hasRole('HRD')")
    public List<KaryawanResponseDTO> sortByGaji() {
        return service.getKaryawanTermahal();
    }

    @GetMapping("/total-gaji/{departemen}")
    @PreAuthorize("hasRole('HRD')")
    public String totalGajiDept(@PathVariable String departemen) {
        BigDecimal total = service.getTotalGajiByDepartemen(departemen);
        NumberFormat formatRupiah = NumberFormat.getInstance(Locale.of("id", "ID"));
        String totalFormatted = formatRupiah.format(total);
        return "Total beban gaji untuk departemen " + departemen.toUpperCase() + " adalah: Rp " + totalFormatted;
    }

    @GetMapping("/departemen")
    @PreAuthorize("hasRole('HRD')")
    public Set<String> getDepartemen() {
        return service.getDepartemenUnik();
    }

    @GetMapping("/grup")
    @PreAuthorize("hasRole('HRD')")
    public Map<String, List<KaryawanResponseDTO>> getGrupKaryawan() {
        return service.getKaryawanGrupByDepartemen();
    }

    @GetMapping("/terbaru")
    @PreAuthorize("hasRole('HRD')")
    public List<KaryawanResponseDTO> getTerbaru() {
        return service.getDaftarKaryawanTerbaru();
    }

    @PutMapping("/me")
public ResponseEntity<KaryawanResponseDTO> updateMyProfile(Authentication authentication, @Valid @RequestBody UpdateProfilRequestDTO dto) {
    return ResponseEntity.ok(service.updateProfilSendiri(authentication.getName(), dto));
}

@PutMapping("/me/password")
public ResponseEntity<?> changeMyPassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequestDTO dto) {
    service.gantiPasswordSendiri(authentication.getName(), dto);
    return ResponseEntity.ok(Map.of("message", "Password berhasil diperbarui"));
}

@PostMapping("/me/foto")
public ResponseEntity<KaryawanResponseDTO> uploadFotoProfil(Authentication authentication, @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
    return ResponseEntity.ok(service.uploadFotoProfil(authentication.getName(), file));
}
}