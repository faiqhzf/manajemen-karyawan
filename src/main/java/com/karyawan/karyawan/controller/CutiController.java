package com.karyawan.karyawan.controller;

import com.karyawan.karyawan.dto.CutiRequestDTO;
import com.karyawan.karyawan.model.Cuti;
import com.karyawan.karyawan.service.CutiService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cuti")
public class CutiController {

    private final CutiService cutiService;

    public CutiController(CutiService cutiService) {
        this.cutiService = cutiService;
    }

    // =========================================
    // ENDPOINT UNTUK KARYAWAN
    // =========================================

    @PostMapping
    @PreAuthorize("hasRole('KARYAWAN')")
    public ResponseEntity<?> ajukanCuti(@Valid @RequestBody CutiRequestDTO dto, Authentication authentication) {
        try {
            String username = authentication.getName();
            Cuti cuti = cutiService.ajukanCuti(username, dto);
            return ResponseEntity.ok(cuti);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('KARYAWAN')")
    public ResponseEntity<List<Cuti>> getCutiSaya(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(cutiService.getRiwayatCutiSaya(username));
    }

    // =========================================
    // ENDPOINT UNTUK HRD
    // =========================================

    @GetMapping
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<List<Cuti>> getAllCuti() {
        return ResponseEntity.ok(cutiService.getAllCuti());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<?> updateStatus(@PathVariable int id, @Valid @RequestBody Map<String, String> payload) {
        try {
            String status = payload.get("status");
            Cuti cuti = cutiService.updateStatusCuti(id, status);
            return ResponseEntity.ok(cuti);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}