package com.karyawan.karyawan.service;

import com.karyawan.karyawan.dto.PayrollResponseDTO;
import com.karyawan.karyawan.model.JadwalKerja;
import com.karyawan.karyawan.model.Karyawan;
import com.karyawan.karyawan.repository.JadwalRepository;
import com.karyawan.karyawan.repository.KaryawanRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class PayrollService {

    private final KaryawanRepository karyawanRepository;
    private final JadwalRepository jadwalRepository;

    public PayrollService(KaryawanRepository karyawanRepository, JadwalRepository jadwalRepository) {
        this.karyawanRepository = karyawanRepository;
        this.jadwalRepository = jadwalRepository;
    }

    public PayrollResponseDTO generateSlipGaji(String username, int tahun, int bulan) {
        Karyawan karyawan = karyawanRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profil karyawan tidak ditemukan"));

        YearMonth periode = YearMonth.of(tahun, bulan);
        LocalDate tanggalMulai = periode.atDay(1);
        LocalDate tanggalSelesai = periode.atEndOfMonth();

        // Agregasi Kehadiran Bulanan
        List<JadwalKerja> jadwalBulanIni = jadwalRepository.findByUsernameKaryawanAndTanggalBetween(
                username, tanggalMulai, tanggalSelesai
        );

        long hadir = jadwalBulanIni.stream().filter(j -> j.getStatus().equals("HADIR")).count();
        long telat = jadwalBulanIni.stream().filter(j -> j.getStatus().equals("TERLAMBAT")).count();
        long alpa = jadwalBulanIni.stream().filter(j -> j.getStatus().equals("ALPA")).count();

        // Kalkulasi Denda
        BigDecimal dendaTelat = BigDecimal.valueOf(50000).multiply(BigDecimal.valueOf(telat));
        BigDecimal dendaAlpa = BigDecimal.valueOf(150000).multiply(BigDecimal.valueOf(alpa));
        BigDecimal totalPotongan = dendaTelat.add(dendaAlpa);

        // Kalkulasi Gaji Bersih
        BigDecimal gajiBersih = karyawan.getGaji().subtract(totalPotongan);

        // Mitigasi Gaji Minus
        if (gajiBersih.compareTo(BigDecimal.ZERO) < 0) {
            gajiBersih = BigDecimal.ZERO;
        }

        return new PayrollResponseDTO(
                karyawan.getNama(),
                karyawan.getDepartemen(),
                periode.toString(),
                karyawan.getGaji(),
                hadir, telat, alpa,
                dendaTelat, dendaAlpa,
                gajiBersih
        );
    }
}