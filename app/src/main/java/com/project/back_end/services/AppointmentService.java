package com.project.back_end.services;

import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.utils.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final TokenService tokenService;
    private final ServiceHelper serviceHelper;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              TokenService tokenService,
                              ServiceHelper serviceHelper) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.tokenService = tokenService;
        this.serviceHelper = serviceHelper;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());

        if (existingOpt.isEmpty()) {
            response.put("error", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existing = existingOpt.get();
        if (!existing.getPatient().getId().equals(appointment.getPatient().getId())) {
            response.put("error", "Unauthorized update attempt.");
            return ResponseEntity.status(403).body(response);
        }

        // Validate new appointment slot availability
        String validationError = serviceHelper.validateAppointment(appointment);
        if (validationError != null) {
            response.put("error", validationError);
            return ResponseEntity.badRequest().body(response);
        }

        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        appointmentRepository.save(existing);

        response.put("message", "Appointment updated successfully.");
        return ResponseEntity.ok(response);
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        Long patientId = tokenService.getIdFromToken(token);

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty()) {
            response.put("error", "Appointment not found.");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = appointmentOpt.get();
        if (!appointment.getPatient().getId().equals(patientId)) {
            response.put("error", "Unauthorized cancellation.");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment cancelled.");
        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();
        Long doctorId = tokenService.getIdFromToken(token);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments;
        if (pname == null || pname.isBlank()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            appointments = appointmentRepository
                .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, pname, start, end);
        }

        result.put("appointments", appointments);
        return result;
    }
}
