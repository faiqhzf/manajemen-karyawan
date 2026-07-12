package com.karyawan.karyawan.dto;

import com.karyawan.karyawan.model.Karyawan;
import java.math.BigDecimal;

public record KaryawanResponseDTO(
    long id,
    String nama,
    String departemen,
    BigDecimal gaji,
    String username,
    String noTelepon,
    String fotoUrl,
    Integer kuotaCuti
) {
    public static KaryawanResponseDTO fromEntity(Karyawan karyawan) {
        return new KaryawanResponseDTO(
            karyawan.getId(),
            karyawan.getNama(),
            karyawan.getDepartemen(),
            karyawan.getGaji(),
            karyawan.getUsername(),
            karyawan.getNoTelepon(),
            karyawan.getFotoUrl(),
            karyawan.getKuotaCuti()
        );
    }
}