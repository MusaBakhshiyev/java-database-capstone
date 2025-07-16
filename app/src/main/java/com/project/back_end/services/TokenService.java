package com.project.back_end.services;

import com.project.back_end.model.Admin;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    // Secret key from application.properties
    @Value("${jwt.secret}")
    private String secret;

    private SecretKey signingKey;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    public void init() {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 3. getSigningKey method
    private SecretKey getSigningKey() {
        return signingKey;
    }

    // 4. generateToken method
    public String generateToken(String identifier) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000); // 7 days

        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 5. extractIdentifier method (extract email or username)
    public String extractIdentifier(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    // 6. validateToken method
    public boolean validateToken(String token, String userType) {
        try {
            String identifier = extractIdentifier(token);
            if (identifier == null) return false;

            switch (userType.toLowerCase()) {
                case "admin":
                    Optional<Admin> admin = Optional.ofNullable(adminRepository.findByUsername(identifier));
                    return admin.isPresent();
                case "doctor":
                    Optional<Doctor> doctor = doctorRepository.findByEmail(identifier);
                    return doctor.isPresent();
                case "patient":
                    Optional<Patient> patient = patientRepository.findByEmail(identifier);
                    return patient.isPresent();
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
