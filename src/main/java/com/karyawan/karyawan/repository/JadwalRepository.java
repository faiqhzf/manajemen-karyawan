package com.karyawan.karyawan.repository;

import com.karyawan.karyawan.model.JadwalKerja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JadwalRepository extends JpaRepository<JadwalKerja, Long> {
    List<JadwalKerja> findByUsernameKaryawan(String usernameKaryawan);
    Optional<JadwalKerja> findByUsernameKaryawanAndTanggal(String usernameKaryawan, LocalDate tanggal);

    List<JadwalKerja> findByTanggal(LocalDate tanggal);

    // Tarik data jadwal berdasarkan rentang tanggal untuk kalkulasi slip gaji
    List<JadwalKerja> findByUsernameKaryawanAndTanggalBetween(String username, LocalDate startDate, LocalDate endDate);
}