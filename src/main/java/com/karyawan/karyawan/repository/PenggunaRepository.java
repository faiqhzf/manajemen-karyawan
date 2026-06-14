package com.karyawan.karyawan.repository;

import com.karyawan.karyawan.model.Pengguna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PenggunaRepository extends JpaRepository<Pengguna, Integer> {
    // Fungsi khusus untuk mencari pengguna berdasarkan username saat login
    Optional<Pengguna> findByUsername(String username);
}