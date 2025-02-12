package com.agri.vision.Controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.Service.PdfGeneratorService;

@RestController
@RequestMapping("/pdf")
public class PdfController {
    private final PdfGeneratorService pdfGeneratorService;

    public PdfController(PdfGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @PostMapping("/generate-order")
    public ResponseEntity<String> generateOrderPdf(@RequestBody Map<String, Object> requestData) {
        try {
            Map<String, String> orderInfo = (Map<String, String>) requestData.get("orderInfo");
            Map<String, String> customerInfo = (Map<String, String>) requestData.get("customerInfo");
            List<Map<String, String>> items = (List<Map<String, String>>) requestData.get("items");

            String message = pdfGeneratorService.generateOrderPdf(orderInfo, customerInfo, items);

            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to generate PDF: " + e.getMessage());
        }
    }
}
