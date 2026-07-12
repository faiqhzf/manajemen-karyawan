package com.karyawan.karyawan.controller;

import com.karyawan.karyawan.dto.PayrollResponseDTO;
import com.karyawan.karyawan.service.PayrollService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    private final PayrollService payrollService;

    public PayrollController(PayrollService payrollService) {
        this.payrollService = payrollService;
    }

    // Endpoint untuk Karyawan
    @GetMapping("/me")
    @PreAuthorize("hasRole('KARYAWAN')")
    public ResponseEntity<PayrollResponseDTO> getMyPayslip(
            @RequestParam int tahun,
            @RequestParam int bulan,
            Principal principal) {
        return ResponseEntity.ok(payrollService.generateSlipGaji(principal.getName(), tahun, bulan));
    }

    // Endpoint untuk HRD
    @GetMapping("/admin/{username}")
    @PreAuthorize("hasRole('HRD')")
    public ResponseEntity<PayrollResponseDTO> getEmployeePayslip(
            @PathVariable String username,
            @RequestParam int tahun,
            @RequestParam int bulan) {
        return ResponseEntity.ok(payrollService.generateSlipGaji(username, tahun, bulan));
    }
}