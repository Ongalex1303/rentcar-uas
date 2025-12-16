package com.uas.rentcar;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Method wajib untuk mencari user saat login & saat makeAdmin
    User findByEmail(String email);
}