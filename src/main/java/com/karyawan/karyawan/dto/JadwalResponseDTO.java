package com.karyawan.karyawan.dto;

import com.karyawan.karyawan.model.JadwalKerja;

public record JadwalResponseDTO(
        Long id,
        String usernameKaryawan,
        String tanggal,
        String jamMasukShift,
        String jamPulangShift,
        String waktuCheckIn,
        String waktuCheckOut,
        String status,
        String kordinatLokasi
) {
    public static JadwalResponseDTO fromEntity(JadwalKerja entity) {
        return new JadwalResponseDTO(
                entity.getId(),
                entity.getUsernameKaryawan(),
                entity.getTanggal() != null ? entity.getTanggal().toString() : null,
                entity.getJamMasukShift() != null ? entity.getJamMasukShift().toString() : null,
                entity.getJamPulangShift() != null ? entity.getJamPulangShift().toString() : null,
                entity.getWaktuCheckIn() != null ? entity.getWaktuCheckIn().toString() : null,
                entity.getWaktuCheckOut() != null ? entity.getWaktuCheckOut().toString() : null,
                entity.getStatus(),
                entity.getKordinatLokasi()
        );
    }
}