package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.CutiRequestDTO;
import com.karyawan.karyawan.model.Cuti;
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

    public Cuti ajukanCuti(String username, CutiRequestDTO dto) {
        
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
        cuti.setStatus("MENUNGGU");
        
        return cutiRepository.save(cuti);
    }


    public List<Cuti> getRiwayatCutiSaya(String username) {
        return cutiRepository.findByUsernameKaryawan(username);
    }

    public List<Cuti> getAllCuti() {
        return cutiRepository.findAll();
    }

    public Cuti updateStatusCuti(int id, String statusBaru) {
        Cuti cuti = cutiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data cuti tidak ditemukan"));
        
        cuti.setStatus(statusBaru.toUpperCase());
        return cutiRepository.save(cuti);
    }
}