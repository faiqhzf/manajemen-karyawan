package com.karyawan.karyawan.model;

import jakarta.persistence.*;

@Entity
@Table(name = "karyawan")
public class Karyawan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID akan Auto-Increment di database
    private int id;
    
    private String nama;
    private String departemen;
    private double gaji;

    // 1. WAJIB ADA: Konstruktor kosong untuk kebutuhan JPA/Hibernate
    public Karyawan() {
    }

    // 2. Konstruktor berparameter (Tanpa 'id' karena id dibuat otomatis oleh database)
    public Karyawan(String nama, String departemen, double gaji) {
        this.nama = nama;
        this.departemen = departemen;
        this.gaji = gaji;
    }

    // Getters
    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getDepartemen() { return departemen; }
    public double getGaji() { return gaji; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNama(String nama) { this.nama = nama; }
    public void setDepartemen(String departemen) { this.departemen = departemen; }
    public void setGaji(double gaji) { this.gaji = gaji; }
}