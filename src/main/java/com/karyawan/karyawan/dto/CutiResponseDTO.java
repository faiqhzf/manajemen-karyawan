package com.karyawan.karyawan.dto;

import com.karyawan.karyawan.model.Cuti;
import com.karyawan.karyawan.model.StatusCuti;
import java.time.LocalDate;

public record CutiResponseDTO(
    long id,
    String usernameKaryawan,
    LocalDate tanggalMulai,
    LocalDate tanggalSelesai,
    String alasan,
    StatusCuti status
) {
    public static CutiResponseDTO fromEntity(Cuti cuti) {
        return new CutiResponseDTO(
            cuti.getId(),
            cuti.getUsernameKaryawan(),
            cuti.getTanggalMulai(),
            cuti.getTanggalSelesai(),
            cuti.getAlasan(),
            cuti.getStatus()
        );
    }
}