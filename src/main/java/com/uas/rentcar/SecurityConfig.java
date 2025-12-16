package com.uas.rentcar;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((requests) -> requests
                // Halaman yang boleh diakses TANPA LOGIN
                .requestMatchers("/", "/register", "/saveUser", "/login", "/photos/**", "/css/**", "/js/**", "/images/**").permitAll()
                
                // Halaman khusus ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Halaman khusus CUSTOMER (USER)
                .requestMatchers("/sewa/**").hasRole("USER")
                
                // Sisanya wajib login
                .anyRequest().authenticated()
            )
            .formLogin((form) -> form
                .loginPage("/login")           // Lokasi file login.html
                .usernameParameter("email")    // Login menggunakan field 'email' bukan 'username'
                .defaultSuccessUrl("/", true)  // Kalau sukses, ke beranda
                .permitAll()
            )
            .logout((logout) -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Algoritma enkripsi standar industri
    }
}