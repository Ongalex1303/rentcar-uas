package com.uas.rentcar;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MobilRepository extends JpaRepository<Mobil, Long> {
    // Fitur Search: Cari berdasarkan Merk ATAU Model
    @Query("SELECT m FROM Mobil m WHERE m.merk LIKE %?1% OR m.model LIKE %?1%")
    List<Mobil> search(String keyword);
}