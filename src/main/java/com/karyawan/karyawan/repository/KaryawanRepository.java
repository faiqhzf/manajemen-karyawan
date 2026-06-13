package com.karyawan.karyawan.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.karyawan.karyawan.model.Karyawan;

@Repository
public interface KaryawanRepository extends JpaRepository<Karyawan, Integer> {
    // JpaRepository otomatis menyediakan fungsi bawaan seperti save(), findAll(), findById(), deleteById()
}