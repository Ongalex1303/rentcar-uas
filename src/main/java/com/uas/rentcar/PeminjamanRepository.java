package com.uas.rentcar;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PeminjamanRepository extends JpaRepository<Peminjaman, Long> {
    // 1. Untuk Admin: Mengambil semua data urut dari yang paling baru
    List<Peminjaman> findAllByOrderByIdDesc();
    
    // 2. Untuk User: Mengambil data khusus milik user yang sedang login
    List<Peminjaman> findByNamaPeminjamOrderByIdDesc(String namaPeminjam);
}