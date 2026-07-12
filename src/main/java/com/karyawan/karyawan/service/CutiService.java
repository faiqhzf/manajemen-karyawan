package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.CutiRequestDTO;
import com.karyawan.karyawan.dto.CutiResponseDTO;
import com.karyawan.karyawan.model.Cuti;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.model.StatusCuti;
import com.karyawan.karyawan.repository.CutiRepository;
import com.karyawan.karyawan.repository.KaryawanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CutiService {

    private final CutiRepository cutiRepository;
    private final KaryawanRepository karyawanRepository;

    public CutiService(CutiRepository cutiRepository, KaryawanRepository karyawanRepository) {
        this.cutiRepository = cutiRepository;
        this.karyawanRepository = karyawanRepository;
    }

    // PERBAIKAN: Parameter diubah menjadi Integer id
    private Cuti findEntityById(Integer id) { 
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

    @Transactional 
    // PERBAIKAN: Parameter diubah menjadi Integer id
    public CutiResponseDTO updateStatusCuti(Integer id, String statusBaru) { 
        Cuti cuti = cutiRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dokumen izin tidak ditemukan"));

        StatusCuti statusEnumBaru = StatusCuti.valueOf(statusBaru);

        if (statusEnumBaru == StatusCuti.DISETUJUI && cuti.getStatus() == StatusCuti.MENUNGGU) {
            Karyawan karyawan = karyawanRepository.findByUsername(cuti.getUsernameKaryawan())
                    .orElseThrow(() -> new RuntimeException("Karyawan tidak ditemukan"));

            long jumlahHari = ChronoUnit.DAYS.between(cuti.getTanggalMulai(), cuti.getTanggalSelesai()) + 1;

            if (karyawan.getKuotaCuti() < jumlahHari) {
                throw new RuntimeException("Validasi Gagal: Sisa kuota hanya " + karyawan.getKuotaCuti() + " hari, sementara permintaan sebanyak " + jumlahHari + " hari.");
            }

            karyawan.setKuotaCuti(karyawan.getKuotaCuti() - (int) jumlahHari);
            karyawanRepository.save(karyawan);
        }

        cuti.setStatus(statusEnumBaru);
        return CutiResponseDTO.fromEntity(cutiRepository.save(cuti));
    }
}