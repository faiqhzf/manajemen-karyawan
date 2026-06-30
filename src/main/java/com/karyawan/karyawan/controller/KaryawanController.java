package com.karyawan.karyawan.controller;

import com.karyawan.karyawan.dto.KaryawanRequestDTO;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.service.KaryawanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/karyawan")
public class KaryawanController {

    private static final Logger log = LoggerFactory.getLogger(KaryawanController.class);

    @Autowired
    private KaryawanService service;

    @GetMapping
    public ResponseEntity<List<Karyawan>> getAll() {
        return ResponseEntity.ok(service.getAllKaryawan());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Karyawan> getById(@PathVariable long id) {
        return ResponseEntity.ok(service.getKaryawanById(id));
    }

    @PostMapping
    public ResponseEntity<Karyawan> addKaryawan(@RequestBody KaryawanRequestDTO dto) {
        log.info("Menerima request penambahan karyawan: {}", dto.getNama());
        Karyawan karyawan = service.tambahKaryawan(dto);
        return ResponseEntity.ok(karyawan);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Karyawan> edit(@PathVariable long id, @RequestBody KaryawanRequestDTO dto) {
        log.info("Menerima request update karyawan ID: {}", id);
        Karyawan karyawan = service.updateKaryawan(id, dto);
        return ResponseEntity.ok(karyawan);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        log.info("Menerima request hapus karyawan ID: {}", id);
        service.deleteKaryawan(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/me")
    public ResponseEntity<Karyawan> getMyProfile(Authentication authentication) {
        String currentUsername = authentication.getName();
        log.info("Menerima request cek profil untuk user: {}", currentUsername);
        Karyawan karyawan = service.getKaryawanByUsername(currentUsername);
        return ResponseEntity.ok(karyawan);
    }

    @GetMapping("/filter/{departemen}")
    public List<Karyawan> filterDepartemen(@PathVariable String departemen) {
        return service.getKaryawanByDepartemen(departemen);
    }

    @GetMapping("/gaji-tertinggi")
    public List<Karyawan> sortByGaji() {
        return service.getKaryawanTermahal();
    }

    @GetMapping("/total-gaji/{departemen}")
    public String totalGajiDept(@PathVariable String departemen) {
        BigDecimal total = service.getTotalGajiByDepartemen(departemen);
        NumberFormat formatRupiah = NumberFormat.getInstance(Locale.of("id", "ID"));
        String totalFormatted = formatRupiah.format(total);
        return "Total beban gaji untuk departemen " + departemen.toUpperCase() + " adalah: Rp " + totalFormatted;
    }

    @GetMapping("/departemen")
    public Set<String> getDepartemen() {
        return service.getDepartemenUnik();
    }

    @GetMapping("/grup")
    public Map<String, List<Karyawan>> getGrupKaryawan() {
        return service.getKaryawanGrupByDepartemen();
    }

    @GetMapping("/terbaru")
    public List<Karyawan> getTerbaru() {
        return service.getDaftarKaryawanTerbaru();
    }
}