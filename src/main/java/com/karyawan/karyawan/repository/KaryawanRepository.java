package com.karyawan.karyawan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.karyawan.karyawan.model.Karyawan;
import java.util.Optional;

@Repository
public interface KaryawanRepository extends JpaRepository<Karyawan, Integer> {
    Optional<Karyawan> findByUsername(String username);
}