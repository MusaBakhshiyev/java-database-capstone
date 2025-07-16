package com.project.back_end.controllers;

import com.project.back_end.model.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService, Service service, AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    // 1. Save Prescription
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(@PathVariable String token, 
                                                                @Valid @RequestBody Prescription prescription) {
        // Validate token for "doctor" role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        // Update appointment status before saving prescription
        appointmentService.updateAppointmentStatus(prescription.getAppointment().getId(), "Prescription Added");

        // Save prescription
        return prescriptionService.savePrescription(prescription);
    }

    // 2. Get Prescription by Appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId, @PathVariable String token) {
        // Validate token for "doctor" role
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode().isError()) {
            return ResponseEntity.status(validationResponse.getStatusCode())
                    .body(Map.of("error", "Invalid or expired token"));
        }

        // Get prescription details
        return prescriptionService.getPrescription(appointmentId);
    }


    // Validation exception handler for @Valid
    @RestControllerAdvice
    public static class ValidationFailed {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : ex.getBindingResult().getFieldErrors()) {
                errors.put("message", error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }
    }
}
