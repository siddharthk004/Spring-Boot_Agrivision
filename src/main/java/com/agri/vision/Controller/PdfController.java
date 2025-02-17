package com.agri.vision.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.Model.user;
import com.agri.vision.Repo.userRepo;
import com.agri.vision.Service.JwtService;
import com.agri.vision.Service.PdfGeneratorService;

@RestController
@RequestMapping("/pdf")
public class PdfController {
    private final PdfGeneratorService pdfGeneratorService;

    public PdfController(PdfGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }
    
    @Autowired
    private JwtService jwtService;

    @Autowired
    private userRepo userrepo;

    @PostMapping("/generate-order")
    public ResponseEntity<String> generateOrderPdf(
        @RequestHeader("Authorization") String token,        
        @RequestBody Map<String, Object> requestData) {
            
            // Extract username from token
            String usernameFromToken = jwtService.extractUsername(token.substring(7));
            user user = userrepo.findByUsername(usernameFromToken);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            Long UID = user.getId(); 

        try {
            Map<String, String> orderInfo = (Map<String, String>) requestData.get("orderInfo");
            Map<String, String> customerInfo = (Map<String, String>) requestData.get("customerInfo");
            List<Map<String, String>> items = (List<Map<String, String>>) requestData.get("items");

            String message = pdfGeneratorService.generateOrderPdf(UID,orderInfo, customerInfo, items);

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate PDF: " + e.getMessage());
        }
    }
}
