package com.karyawan.karyawan.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cuti")
public class Cuti {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String usernameKaryawan; // Mengikat cuti dengan akun karyawan

    @Column(nullable = false)
    private LocalDate tanggalMulai;

    @Column(nullable = false)
    private LocalDate tanggalSelesai;

    @Column(nullable = false)
    private String alasan;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCuti status = StatusCuti.MENUNGGU;

    public Cuti() {}

    public Cuti(String usernameKaryawan, LocalDate tanggalMulai, LocalDate tanggalSelesai, String alasan, StatusCuti status) {
        this.usernameKaryawan = usernameKaryawan;
        this.tanggalMulai = tanggalMulai;
        this.tanggalSelesai = tanggalSelesai;
        this.alasan = alasan;
        this.status = status;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsernameKaryawan() { return usernameKaryawan; }
    public void setUsernameKaryawan(String usernameKaryawan) { this.usernameKaryawan = usernameKaryawan; }

    public LocalDate getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(LocalDate tanggalMulai) { this.tanggalMulai = tanggalMulai; }

    public LocalDate getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(LocalDate tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }

    public String getAlasan() { return alasan; }
    public void setAlasan(String alasan) { this.alasan = alasan; }

    public StatusCuti getStatus() { return status; }
    public void setStatus(StatusCuti status) { this.status = status; }
}