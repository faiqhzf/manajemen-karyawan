package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.CutiRequestDTO;
import com.karyawan.karyawan.model.Cuti;
import com.karyawan.karyawan.repository.CutiRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CutiService {

    private final CutiRepository cutiRepository;

    public CutiService(CutiRepository cutiRepository) {
        this.cutiRepository = cutiRepository;
    }

    // Fungsi Karyawan: Mengajukan cuti baru
    public Cuti ajukanCuti(String username, CutiRequestDTO dto) {
        if (dto.tanggalMulai().isAfter(dto.tanggalSelesai())) {
            throw new RuntimeException("Tanggal selesai tidak boleh mendahului tanggal mulai.");
        }

        Cuti cuti = new Cuti(
            username, 
            dto.tanggalMulai(), 
            dto.tanggalSelesai(), 
            dto.alasan(), 
            "MENUNGGU" // Status default
        );
        return cutiRepository.save(cuti);
    }

    // Fungsi Karyawan: Melihat riwayat cuti sendiri
    public List<Cuti> getRiwayatCutiSaya(String username) {
        return cutiRepository.findByUsernameKaryawan(username);
    }

    // Fungsi HRD: Melihat semua pengajuan cuti
    public List<Cuti> getAllCuti() {
        return cutiRepository.findAll();
    }

    // Fungsi HRD: Menerima atau menolak cuti
    public Cuti updateStatusCuti(int id, String statusBaru) {
        Cuti cuti = cutiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data cuti tidak ditemukan"));
        
        cuti.setStatus(statusBaru.toUpperCase());
        return cutiRepository.save(cuti);
    }
}