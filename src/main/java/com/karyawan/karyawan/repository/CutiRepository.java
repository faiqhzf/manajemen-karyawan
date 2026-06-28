package com.karyawan.karyawan.repository;

import com.karyawan.karyawan.model.Cuti;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CutiRepository extends JpaRepository<Cuti, Integer> {
    // Mencari riwayat cuti berdasarkan username spesifik (untuk portal karyawan)
    List<Cuti> findByUsernameKaryawan(String usernameKaryawan);
    
    // Mencari cuti berdasarkan status tertentu (misal: HRD ingin melihat yang "MENUNGGU")
    List<Cuti> findByStatus(String status);
}