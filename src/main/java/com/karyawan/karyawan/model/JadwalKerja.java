package com.karyawan.karyawan.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "jadwal_kerja")
public class JadwalKerja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username_karyawan", nullable = false)
    private String usernameKaryawan;

    @Column(nullable = false)
    private LocalDate tanggal;

    // Shift yang ditetapkan HRD
    @Column(name = "jam_masuk_shift")
    private LocalTime jamMasukShift;

    @Column(name = "jam_pulang_shift")
    private LocalTime jamPulangShift;

    // Data aktual saat Karyawan Clock-in/out
    @Column(name = "waktu_check_in")
    private LocalTime waktuCheckIn;

    @Column(name = "waktu_check_out")
    private LocalTime waktuCheckOut;

    // Menyimpan koordinat GPS untuk validasi WFO/WFA
    @Column(name = "kordinat_lokasi")
    private String kordinatLokasi;

    @Column(nullable = false)
    private String status = "BELUM_MULAI"; // BELUM_MULAI, HADIR, TERLAMBAT, ALPA, UBAH_MENDADAK

    // ==========================================
    // GETTER DAN SETTER
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsernameKaryawan() {
        return usernameKaryawan;
    }

    public void setUsernameKaryawan(String usernameKaryawan) {
        this.usernameKaryawan = usernameKaryawan;
    }

    public LocalDate getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDate tanggal) {
        this.tanggal = tanggal;
    }

    public LocalTime getJamMasukShift() {
        return jamMasukShift;
    }

    public void setJamMasukShift(LocalTime jamMasukShift) {
        this.jamMasukShift = jamMasukShift;
    }

    public LocalTime getJamPulangShift() {
        return jamPulangShift;
    }

    public void setJamPulangShift(LocalTime jamPulangShift) {
        this.jamPulangShift = jamPulangShift;
    }

    public LocalTime getWaktuCheckIn() {
        return waktuCheckIn;
    }

    public void setWaktuCheckIn(LocalTime waktuCheckIn) {
        this.waktuCheckIn = waktuCheckIn;
    }

    public LocalTime getWaktuCheckOut() {
        return waktuCheckOut;
    }

    public void setWaktuCheckOut(LocalTime waktuCheckOut) {
        this.waktuCheckOut = waktuCheckOut;
    }

    public String getKordinatLokasi() {
        return kordinatLokasi;
    }

    public void setKordinatLokasi(String kordinatLokasi) {
        this.kordinatLokasi = kordinatLokasi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}