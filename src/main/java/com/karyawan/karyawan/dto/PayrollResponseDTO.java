package com.karyawan.karyawan.dto;

import java.math.BigDecimal;

public record PayrollResponseDTO(
        String namaPegawai,
        String departemen,
        String periode,
        BigDecimal gajiPokok,
        long totalHadir,
        long totalTelat,
        long totalAlpa,
        BigDecimal potonganTelat,
        BigDecimal potonganAlpa,
        BigDecimal gajiBersih
) {}