package com.karyawan.karyawan.controller;

import com.karyawan.karyawan.dto.KaryawanRequestDTO;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.service.KaryawanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/karyawan")
public class KaryawanController {

    @Autowired
    private KaryawanService service;

    // ==========================================
    // ENDPOINT UTAMA (INTEGRASI UI)
    // ==========================================

    @GetMapping
    public ResponseEntity<List<Karyawan>> getAll() {
        return ResponseEntity.ok(service.getAllKaryawan());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Karyawan> getById(@PathVariable long id) {
        return ResponseEntity.ok(service.getKaryawanById(id));
    }

    @PostMapping
    public ResponseEntity<?> addKaryawan(@RequestBody KaryawanRequestDTO dto) {
        try {
            Karyawan karyawan = service.tambahKaryawan(dto);
            return ResponseEntity.ok(karyawan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> edit(@PathVariable long id, @RequestBody KaryawanRequestDTO dto) {
        try {
            Karyawan karyawan = service.updateKaryawan(id, dto);
            return ResponseEntity.ok(karyawan);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        service.deleteKaryawan(id);
        return ResponseEntity.ok().build();
    }

    // ==========================================
    // ENDPOINT PEMBELAJARAN (STREAM, SET, MAP)
    // ==========================================

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
        double total = service.getTotalGajiByDepartemen(departemen);
        String totalFormatted = String.format(java.util.Locale.of("id", "ID"), "%,.0f", total);
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