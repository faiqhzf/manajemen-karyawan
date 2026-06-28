package com.karyawan.karyawan.dto;

import java.time.LocalDate;


public record CutiRequestDTO(
    LocalDate tanggalMulai,
    LocalDate tanggalSelesai,
    String alasan
) {}