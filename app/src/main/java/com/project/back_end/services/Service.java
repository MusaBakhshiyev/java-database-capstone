package com.project.back_end.services;

import com.project.back_end.model.*;
import com.project.back_end.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 1. Validate Token
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        try {
            boolean valid = tokenService.validateToken(token, user);
            if (!valid) {
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            response.put("message", "Token valid");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error validating token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 2. Validate Admin Login
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null) {
                response.put("message", "Admin not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            if (!admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 3. Filter Doctor
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        Map<String, Object> response = new HashMap<>();
        try {
            if ((name == null || name.isEmpty()) && (specialty == null || specialty.isEmpty()) && (time == null || time.isEmpty())) {
                // Return all doctors if no filter provided
                List<Doctor> doctors = doctorService.getDoctors();
                response.put("doctors", doctors);
                return response;
            }
            List<Doctor> filteredDoctors = doctorService.filterDoctorsByNameSpecilityandTime(
                    name == null ? "" : name,
                    specialty == null ? "" : specialty,
                    time == null ? "" : time
            );
            response.put("doctors", filteredDoctors);
            return response;
        } catch (Exception e) {
            response.put("message", "Error filtering doctors");
            return response;
        }
    }

    // 4. Validate Appointment
    public int validateAppointment(Appointment appointment) {
        try {
            Optional<Doctor> optDoctor = doctorRepository.findById(appointment.getDoctorId());
            if (optDoctor.isEmpty()) {
                return -1; // doctor doesn't exist
            }
            List<String> availableSlots = doctorService.getDoctorAvailability(appointment.getDoctorId(), appointment.getAppointmentTime().toLocalDate());
            String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();
            for (String slot : availableSlots) {
                if (slot.equals(requestedTime)) {
                    return 1; // valid appointment time
                }
            }
            return 0; // time unavailable
        } catch (Exception e) {
            return 0;
        }
    }

    // 5. Validate Patient (whether patient does NOT exist, returns true if valid to create)
    public boolean validatePatient(Patient patient) {
        try {
            Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
            return existing == null;
        } catch (Exception e) {
            return false; // treat errors as existing patient (safe side)
        }
    }

    // 6. Validate Patient Login
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            if (!patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 7. Filter Patient
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            if (email == null) {
                response.put("message", "Invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            if ((condition == null || condition.isEmpty()) && (name == null || name.isEmpty())) {
                // No filters: return all appointments
                return patientService.getPatientAppointment(patient.getId(), token);
            } else if (condition != null && !condition.isEmpty() && (name == null || name.isEmpty())) {
                // Filter by condition only
                return patientService.filterByCondition(condition, patient.getId());
            } else if ((condition == null || condition.isEmpty()) && name != null && !name.isEmpty()) {
                // Filter by doctor name only
                return patientService.filterByDoctor(name, patient.getId());
            } else {
                // Filter by both condition and doctor name
                return patientService.filterByDoctorAndCondition(condition, name, patient.getId());
            }
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
