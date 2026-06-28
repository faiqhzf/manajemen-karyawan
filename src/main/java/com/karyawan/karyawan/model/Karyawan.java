package com.karyawan.karyawan.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "karyawan")
public class Karyawan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    private String nama;
    private String departemen;
    private BigDecimal gaji;
    
    // Properti baru
    private String username;

    public Karyawan() {
    }

    public Karyawan(String nama, String departemen, double gaji, String username) {
        this.nama = nama;
        this.departemen = departemen;
        this.gaji = BigDecimal.valueOf(gaji);
        this.username = username;
    }

    // Getters
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getDepartemen() { return departemen; }
    public BigDecimal getGaji() { return gaji; }
    public String getUsername() { return username; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setDepartemen(String departemen) { this.departemen = departemen; }
    public void setGaji(BigDecimal gaji) { this.gaji = gaji; }
    
    // WAJIB DITAMBAHKAN AGAR ERROR 67108964 HILANG:
    public void setUsername(String username) { this.username = username; }
}