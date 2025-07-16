package com.project.back_end.mvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
public class DashboardController {

    // 2. Autowire the Service that handles token validation
    @Autowired
    private TokenValidationService tokenValidationService;

    // 3. Admin dashboard endpoint with token validation
    @GetMapping("/adminDashboard/{token}")
    public Object adminDashboard(@PathVariable("token") String token) {
        // Validate token for admin role
        Map<String, Object> validationResult = tokenValidationService.validateToken(token, "admin");
        
        // If validationResult is empty, token is valid
        if (validationResult.isEmpty()) {
            return "admin/adminDashboard"; // Thymeleaf template path
        } else {
            // Token invalid, redirect to login/root page
            return new RedirectView("/");
        }
    }

    // 4. Doctor dashboard endpoint with token validation
    @GetMapping("/doctorDashboard/{token}")
    public Object doctorDashboard(@PathVariable("token") String token) {
        // Validate token for doctor role
        Map<String, Object> validationResult = tokenValidationService.validateToken(token, "doctor");

        // If token valid, return doctor dashboard view
        if (validationResult.isEmpty()) {
            return "doctor/doctorDashboard";
        } else {
            // Redirect to login/root page if invalid
            return new RedirectView("/");
        }
    }
}
