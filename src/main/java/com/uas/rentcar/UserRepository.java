package com.uas.rentcar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Query method otomatis dari Spring Data JPA
    // "SELECT * FROM users WHERE email = ?"
    User findByEmail(String email);
}