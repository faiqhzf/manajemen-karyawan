package com.karyawan.karyawan.model;

public class Karyawan {
    private int id;
    private String nama;
    private String departemen;
    private double gaji;

    // Constructor
    public Karyawan(int id, String nama, String departemen, double gaji) {
        this.id = id;
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