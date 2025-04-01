package com.agri.vision.Controller;

import java.io.ByteArrayOutputStream;
// import java.net.URL;
import java.util.List;

import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.Repo.OrderRepo;

@RestController
@CrossOrigin(origins = "/**")
@RequestMapping("/api/v1/auth")
public class InvoiceController {

    private final OrderRepo orderRepository;

    public InvoiceController(OrderRepo orderRepository) {
        this.orderRepository = orderRepository;
    }

    // @GetMapping("/admin/merge-invoices")
    // public ResponseEntity<Resource> mergeInvoices(@RequestParam String column) {
<<<<<<< HEAD
    // try {
    // List<String> pdfUrls;

    // // Fetch URLs based on the column requested
    // if ("invoice_url".equals(column)) {
    // pdfUrls = orderRepository.findAllUserInvoices();
    // } else if ("shipment_invoice_url".equals(column)) {
    // pdfUrls = orderRepository.findAllShipmentInvoices();
    // } else {
    // return ResponseEntity.badRequest().body(null);
    // }

    // if (pdfUrls.isEmpty()) {
    // return ResponseEntity.noContent().build();
    // }

    // // Merge PDFs
    // ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
    // PDFMergerUtility pdfMerger = new PDFMergerUtility();
    // pdfMerger.setDestinationStream(mergedOutputStream);

    // // for (String url : pdfUrls) {
    // // pdfMerger.addSource(new URL(url).openStream());
    // // }

    // pdfMerger.mergeDocuments(null);
    // ByteArrayResource resource = new
    // ByteArrayResource(mergedOutputStream.toByteArray());

    // return ResponseEntity.ok()
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Merged_" +
    // column + ".pdf")
    // .contentType(MediaType.APPLICATION_PDF)
    // .body(resource);
    // } catch (Exception e) {
    // e.printStackTrace();
    // return ResponseEntity.internalServerError().build();
    // }
=======
    //     try {
    //         List<String> pdfUrls;
            
    //         // Fetch URLs based on the column requested
    //         if ("invoice_url".equals(column)) {
    //             pdfUrls = orderRepository.findAllUserInvoices();
    //         } else if ("shipment_invoice_url".equals(column)) {
    //             pdfUrls = orderRepository.findAllShipmentInvoices();
    //         } else {
    //             return ResponseEntity.badRequest().body(null);
    //         }

    //         if (pdfUrls.isEmpty()) {
    //             return ResponseEntity.noContent().build();
    //         }

    //         // Merge PDFs
    //         ByteArrayOutputStream mergedOutputStream = new ByteArrayOutputStream();
    //         PDFMergerUtility pdfMerger = new PDFMergerUtility();
    //         pdfMerger.setDestinationStream(mergedOutputStream);

    //         // for (String url : pdfUrls) {
    //         //     pdfMerger.addSource(new URL(url).openStream());
    //         // }

    //         pdfMerger.mergeDocuments(null);
    //         ByteArrayResource resource = new ByteArrayResource(mergedOutputStream.toByteArray());

    //         return ResponseEntity.ok()
    //                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Merged_" + column + ".pdf")
    //                 .contentType(MediaType.APPLICATION_PDF)
    //                 .body(resource);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.internalServerError().build();
    //     }
>>>>>>> 585740887e5f96ec3be9e63fca9b05e8a93f87ef
    // }
}
