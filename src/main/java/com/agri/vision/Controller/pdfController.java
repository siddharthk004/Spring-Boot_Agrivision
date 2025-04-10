package com.agri.vision.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.DTO.LabelRequest;
import com.agri.vision.Model.Pdf;
import com.agri.vision.Model.user;
import com.agri.vision.Repo.PdfRepo;
import com.agri.vision.Repo.userRepo;
import com.agri.vision.Service.JwtService;
import com.agri.vision.Service.PdfGeneratorService;

@RestController
@RequestMapping("/api/v1/auth/user/")
public class PdfController {
    private final PdfGeneratorService pdfGeneratorService;

    public PdfController(PdfGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @Autowired
    private PdfGeneratorService pdfService;

    @Autowired
    private PdfRepo pdfrepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private userRepo userrepo;

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Thursday 20 Feb 2025
    // Function : Generate Order
    // give token And Data To Generate Order Pdf
    ///////////////////////////////////////////
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
        String mailID = user.getEmail();

        try {
            Map<String, String> orderInfo = (Map<String, String>) requestData.get("orderInfo");
            Map<String, String> customerInfo = (Map<String, String>) requestData.get("customerInfo");
            List<Map<String, String>> items = (List<Map<String, String>>) requestData.get("items");

            String message = pdfGeneratorService.generateOrderPdf(UID, orderInfo, customerInfo, items, mailID);

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate PDF: " + e.getMessage());
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Saturday 22 Feb 2025
    // Function : Download PDF
    // give token And download the order pdf
    ///////////////////////////////////////////
    @GetMapping("/Download-Pdf")
    public ResponseEntity<List<Pdf>> downloadPdf(@RequestHeader("Authorization") String token) {
        try {
            // Extract username from JWT token (remove 'Bearer ' prefix)
            String usernameFromToken = jwtService.extractUsername(token.substring(7));

            // Find user from repository
            user user = userrepo.findByUsername(usernameFromToken);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Get User ID
            Long userId = user.getId();

            // Fetch all PDFs for the user
            List<Pdf> pdfFiles = pdfrepo.findAllByUserid(userId);
            if (pdfFiles.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(pdfFiles);
            }

            // Return PDFs as JSON response
            return ResponseEntity.ok(pdfFiles);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 18 March 2025
    // Function : Download Invoice
    ///////////////////////////////////////////
    @PostMapping("/generate-label")
    public ResponseEntity<String> generateLabel(@RequestBody LabelRequest request) {
        try {
            String fileUrl = pdfService.generateAndUploadLabel(request);
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating PDF: " + e.getMessage());
        }
    }

}
