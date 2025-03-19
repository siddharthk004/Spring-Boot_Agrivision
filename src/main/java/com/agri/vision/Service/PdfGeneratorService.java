package com.agri.vision.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agri.vision.DTO.LabelRequest;
import com.agri.vision.Model.Pdf;
import com.agri.vision.Repo.PdfRepo;
import com.itextpdf.barcodes.Barcode128;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class PdfGeneratorService {

    private final CloudinaryService cloudinaryService;

    @Autowired
    private PdfRepo pdfrepo;
    
    @Autowired
    private EmailService emailService;

    private static final String PDF_DIRECTORY = "C:/GeneratedPdfs/";

    public PdfGeneratorService(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    public String generateOrderPdf(Long UID, Map<String, String> orderInfo, Map<String, String> customerInfo,
            List<Map<String, String>> items,String mailID) {
        try {
            // Ensure directory exists
            File directory = new File(PDF_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = PDF_DIRECTORY + "customer_order_" + System.currentTimeMillis() + ".pdf";
            File pdfFile = new File(filePath);

            try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {

                PdfWriter writer = new PdfWriter(fileOutputStream);
                PdfDocument pdfDocument = new PdfDocument(writer);
                Document document = new Document(pdfDocument);

                // Company Header
                document.add(new Paragraph("Agrivision")
                        .setFontSize(16)
                        .setTextAlignment(TextAlignment.LEFT));
                document.add(new Paragraph("Green Better | Farming Better")
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT));
                document.add(new Paragraph("+12 123 456 789 | info@agrivision.net")
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT));
                document.add(new Paragraph("Sppu Campus, Issc Dept, Pune-123456")
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT));
                document.add(new Paragraph(
                        "----------------------------------------------------------------------------------------------------------------------------------"));

                document.add(new Paragraph("\nCUSTOMER ORDER FORM")
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER));

                // Define colors
                Color headerColor = new DeviceRgb(173, 216, 230); // Light Blue Background
                Color textColor = ColorConstants.BLACK; // Black Text

                // Order Info and Customer Info Table
                Table infoTable = new Table(2);
                infoTable.setWidth(UnitValue.createPercentValue(100));
                infoTable.addCell(new Cell().add(new Paragraph("ORDER INFO").setFontColor(textColor))
                        .setBackgroundColor(headerColor));
                infoTable.addCell(new Cell().add(new Paragraph("CUSTOMER INFO").setFontColor(textColor))
                        .setBackgroundColor(headerColor));

                infoTable.addCell(new Cell().add(new Paragraph("Order No: " + orderInfo.get("Order No"))));
                infoTable.addCell(new Cell().add(new Paragraph("Customer Name: " + customerInfo.get("Customer Name"))));

                infoTable.addCell(new Cell().add(new Paragraph("Order Date: " + orderInfo.get("Order Date"))));
                infoTable.addCell(new Cell().add(new Paragraph("Phone: " + customerInfo.get("Phone"))));

                infoTable.addCell(new Cell().add(new Paragraph("Order Type: " + orderInfo.get("Order Type"))));
                infoTable.addCell(new Cell().add(new Paragraph("Email: " + customerInfo.get("Email"))));

                document.add(infoTable);
                document.add(new Paragraph("\n"));

                // Create Item Table
                Table itemTable = new Table(4);
                itemTable.setWidth(UnitValue.createPercentValue(100));

                // Table Headers with Background Color
                itemTable.addCell(new Cell().add(new Paragraph("ITEM DESCRIPTION").setFontColor(textColor))
                        .setBackgroundColor(headerColor));
                itemTable.addCell(new Cell().add(new Paragraph("UNIT PRICE").setFontColor(textColor))
                        .setBackgroundColor(headerColor));
                itemTable.addCell(new Cell().add(new Paragraph("QUANTITY").setFontColor(textColor))
                        .setBackgroundColor(headerColor));
                itemTable.addCell(new Cell().add(new Paragraph("PRICE").setFontColor(textColor))
                        .setBackgroundColor(headerColor));

                // Add Items
                for (Map<String, String> item : items) {
                    itemTable.addCell(new Cell().add(new Paragraph(item.get("name"))));
                    itemTable.addCell(new Cell().add(new Paragraph(item.get("unitPrice"))));
                    itemTable.addCell(new Cell().add(new Paragraph(item.get("quantity"))));
                    itemTable.addCell(new Cell().add(new Paragraph(item.get("price"))));
                }

                // Add the table to the document
                document.add(itemTable);
                document.add(new Paragraph("\n"));

                // Payment Summary Table
                Table summaryTable = new Table(2);
                summaryTable.setWidth(UnitValue.createPercentValue(100));

                summaryTable.addCell(new Cell().add(new Paragraph("Sub Total:")));
                summaryTable.addCell(new Cell().add(new Paragraph(orderInfo.get("Sub Total"))));

                summaryTable.addCell(new Cell().add(new Paragraph("Discount: 10%")));
                summaryTable.addCell(new Cell().add(new Paragraph(orderInfo.get("Discount"))));

                summaryTable.addCell(new Cell().add(new Paragraph("Tax (VAT 15%):")));
                summaryTable.addCell(new Cell().add(new Paragraph(orderInfo.get("Tax"))));

                summaryTable.addCell(new Cell().add(new Paragraph("GRAND TOTAL:")));
                summaryTable.addCell(new Cell().add(new Paragraph(orderInfo.get("Grand Total"))));

                document.add(summaryTable);
                document.add(new Paragraph("\n"));

                // Signature
                document.add(new Paragraph("__________________________").setTextAlignment(TextAlignment.RIGHT));
                document.add(new Paragraph("Signature With Date").setTextAlignment(TextAlignment.RIGHT));

                // Footer
                document.add(new Paragraph("\nTHANK YOU FOR YOUR BUSINESS")
                        .setFontSize(12)
                        .setTextAlignment(TextAlignment.CENTER));

                document.close();

                // Upload the file to Cloudinary
                String fileUrl = cloudinaryService.uploadPdf(pdfFile);

                if (fileUrl == null || fileUrl.isEmpty()) {
                    throw new RuntimeException("PDF upload to Cloudinary failed.");
                }

                // Save PDF details in the database
                Pdf pf = new Pdf();
                pf.setUserid(UID);
                pf.setPdfurl(fileUrl);
                pdfrepo.save(pf);
                
                String subject = "AgriVision OTP - Order PDF";
                String message = "Your Order Details in the pdf format..";

                boolean success = emailService.sendEmailWithAttachment(mailID, subject, message, pdfFile);

                return "PDF successfully generated and saved.";
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    public String generateAndUploadLabel(LabelRequest request) throws Exception {
        // Generate a unique filename
        String uniqueFileName = "shipping_label_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(uniqueFileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(pdfFile)) {
            PdfWriter writer = new PdfWriter(fileOutputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Load font
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Add Header
            Table headerTable = new Table(1).useAllAvailableWidth();
            headerTable.addCell(new Cell().add(new Paragraph("IN DHL AND FEES CASH")
                    .setFont(boldFont).setFontSize(10))
                    .setBorder(Border.NO_BORDER));
            headerTable.addCell(new Cell().add(new Paragraph(
                    "No Date Required         REPLY POSTAGE\n1 lb Priority Mail Rate Local\nCommercial Base Pricing"))
                    .setFont(regularFont).setFontSize(8))
                    .setBorder(Border.NO_BORDER);

            document.add(headerTable);
            document.add(new Paragraph("**VOID - DO NOT MAIL**")
                    .setFont(boldFont).setFontSize(10).setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("USPS PRIORITY MAIL®")
                    .setFont(boldFont).setFontSize(16).setTextAlignment(TextAlignment.CENTER));

            // Sender & Receiver Info
            document.add(new Paragraph("From: Agrivision\nStore No :- 5421A\nAhmednagar 414616\nMaharashtra, IN 98524-51542")
                    .setFont(regularFont).setFontSize(10));
            document.add(new Paragraph("SHIP TO:").setFont(boldFont).setFontSize(10));
            document.add(new Paragraph(request.getName() + "\n" + request.getAddress())
                    .setFont(regularFont).setFontSize(10));

            document.add(new Paragraph("ZIP - e/ IN DELIVERY CONFIRM")
                    .setFont(boldFont).setFontSize(12).setTextAlignment(TextAlignment.CENTER));

            // Generate Barcode
            Barcode128 barcode = new Barcode128(pdf);
            barcode.setCode("4205354591123412341234123412341234");
            Image barcodeImage = new Image(barcode.createFormXObject(pdf));
            barcodeImage.setWidth(250);
            barcodeImage.setHorizontalAlignment(HorizontalAlignment.CENTER);
            document.add(barcodeImage);

            // Pricing Info
            document.add(new Paragraph("Total Price: RS " + request.getTotalPrice())
                    .setFont(boldFont).setFontSize(12).setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph("ELECTRONIC RATE APPROVED # 805213907")
                    .setFont(regularFont).setFontSize(8).setTextAlignment(TextAlignment.CENTER));

            document.close();
        }

        // Upload PDF to Cloudinary
        String fileUrl = cloudinaryService.uploadPdf(pdfFile);
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new RuntimeException("PDF upload to Cloudinary failed.");
        }

        // // Save PDF details in the database
        // Pdf pdfRecord = new Pdf();
        // pdfRecord.setUserid((long) request.getTotalPrice());
        // pdfRecord.setPdfurl(fileUrl);
        // PdfRepo.save(pdfRecord);

        return fileUrl;
    }
}
