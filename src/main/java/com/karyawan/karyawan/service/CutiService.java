package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.CutiRequestDTO;
import com.karyawan.karyawan.dto.CutiResponseDTO;
import com.karyawan.karyawan.model.Cuti;
import com.karyawan.karyawan.model.StatusCuti;
import com.karyawan.karyawan.repository.CutiRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class CutiService {

    private final CutiRepository cutiRepository;

    public CutiService(CutiRepository cutiRepository) {
        this.cutiRepository = cutiRepository;
    }

    // Metode internal untuk akses Entitas murni (tidak diekspos ke Controller)
    private Cuti findEntityById(int id) {
        return cutiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data cuti tidak ditemukan"));
    }

    public CutiResponseDTO ajukanCuti(String username, CutiRequestDTO dto) {
        LocalDate tglMulai;
        LocalDate tglSelesai;
        
        try {
            tglMulai = LocalDate.parse(dto.getTanggalMulai());
            tglSelesai = LocalDate.parse(dto.getTanggalSelesai());

            if (tglMulai.isAfter(tglSelesai)) {
                throw new RuntimeException("Tanggal selesai tidak boleh mendahului tanggal mulai.");
            }
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Format tanggal tidak valid. Sistem menolak permintaan.");
        }

        Cuti cuti = new Cuti();
        cuti.setUsernameKaryawan(username);
        cuti.setTanggalMulai(tglMulai);
        cuti.setTanggalSelesai(tglSelesai);
        cuti.setAlasan(dto.getAlasan());
        cuti.setStatus(StatusCuti.MENUNGGU);
        
        Cuti savedCuti = cutiRepository.save(cuti);
        return CutiResponseDTO.fromEntity(savedCuti);
    }

    public List<CutiResponseDTO> getRiwayatCutiSaya(String username) {
        return cutiRepository.findByUsernameKaryawan(username)
                .stream()
                .map(CutiResponseDTO::fromEntity)
                .toList();
    }

    public List<CutiResponseDTO> getAllCuti() {
        return cutiRepository.findAll()
                .stream()
                .map(CutiResponseDTO::fromEntity)
                .toList();
    }

    public CutiResponseDTO updateStatusCuti(int id, String statusBaru) {
        Cuti cuti = findEntityById(id);
        
        // Konversi String dari request menjadi nilai Enum StatusCuti
        cuti.setStatus(StatusCuti.valueOf(statusBaru.toUpperCase()));
        
        Cuti updatedCuti = cutiRepository.save(cuti);
        return CutiResponseDTO.fromEntity(updatedCuti);
    }
}