package com.karyawan.karyawan.controller;

import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.service.KaryawanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/karyawan")

public class KaryawanController {
   private final KaryawanService service;

    public KaryawanController(KaryawanService service) {
        this.service = service;
    }

    // Endpoint: Menampilkan semua data
    @GetMapping
    public List<Karyawan> getAll() {
        return service.getAllKaryawan();
    }

    // Endpoint: Menambah data baru
    @PostMapping
    public String addKaryawan(@RequestBody Karyawan karyawan) {
        return service.tambahKaryawan(karyawan);
    }

    // Endpoint (Stream): Filter berdasarkan Departemen
    @GetMapping("/filter/{departemen}")
    public List<Karyawan> filterDepartemen(@PathVariable String departemen) {
        return service.getKaryawanByDepartemen(departemen);
    }

    // Endpoint (Stream): Urutkan gaji tertinggi
    @GetMapping("/gaji-tertinggi")
    public List<Karyawan> sortByGaji() {
        return service.getKaryawanTermahal();
    }

    // Endpoint (Stream): Hitung total gaji per departemen
    @GetMapping("/total-gaji/{departemen}")
    public String totalGajiDept(@PathVariable String departemen) {
        double total = service.getTotalGajiByDepartemen(departemen);
        
        // Memformat notasi ilmiah menjadi angka standar dengan pemisah ribuan ala Indonesia
        String totalFormatted = String.format(new java.util.Locale("id", "ID"), "%,.0f", total);
        
        return "Total beban gaji untuk departemen " + departemen.toUpperCase() + " adalah: Rp " + totalFormatted;
    }

    // ==========================================
    // ENDPOINT UNTUK SET & MAP
    // ==========================================

    // Endpoint GET (Set): Menampilkan daftar departemen unik
    @GetMapping("/departemen")
    public Set<String> getDepartemen() {
        return service.getDepartemenUnik();
    }

    // Endpoint GET (Map): Menampilkan struktur pengelompokan data
    @GetMapping("/grup")
    public Map<String, List<Karyawan>> getGrupKaryawan() {
        return service.getKaryawanGrupByDepartemen();
    }

    // Endpoint: Menampilkan data unik (hanya yang terbaru jika ID duplikat)
    @GetMapping("/terbaru")
    public List<Karyawan> getTerbaru() {
        return service.getDaftarKaryawanTerbaru();
    }

    // Endpoint: Edit karyawan berdasarkan ID
    @PutMapping("/{id}")
    public String edit(@PathVariable int id, @RequestBody Karyawan dataBaru) {
        return service.updateKaryawan(id, dataBaru);
    }

    // Endpoint: Hapus karyawan berdasarkan ID
    @DeleteMapping("/{id}")
    public String delete(@PathVariable int id) {
        return service.deleteKaryawan(id);
    }

    // Endpoint: Menampilkan 1 karyawan berdasarkan ID
    @GetMapping("/{id}")
    public Karyawan getById(@PathVariable int id) {
        return service.getKaryawanById(id);
    }
}
