package com.agri.vision.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class PdfGeneratorService {

    private static final String PDF_DIRECTORY = "C:/GeneratedPdfs/";

    public String generateOrderPdf(Map<String, String> orderInfo, Map<String, String> customerInfo,
            List<Map<String, String>> items) {
        try {
            // Ensure directory exists
            File directory = new File(PDF_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String filePath = PDF_DIRECTORY + "customer_order_" + System.currentTimeMillis() + ".pdf";

            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

                PdfWriter writer = new PdfWriter(outputStream);
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
                document.add(new Paragraph("Sppu Campus, Issc Dept, pune-123456")
                        .setFontSize(10)
                        .setTextAlignment(TextAlignment.LEFT));
                document.add(new Paragraph(
                        "----------------------------------------------------------------------------------------------------------------------------------"));

                document.add(new Paragraph("\nCUSTOMER ORDER FORM")
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.CENTER));
                // Define colors
                Color headerColor = new DeviceRgb(173, 216, 230); // Light Blue Background
                Color textColor = ColorConstants.BLACK; // Dark Black Text

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

                // Add Items (Example Data)
                for (Map<String, String> item : items) {
                    itemTable.addCell(new Cell().add(new Paragraph(item.get("name"))).setFontColor(textColor));
                    itemTable.addCell(new Cell().add(new Paragraph(item.get("unitPrice"))).setFontColor(textColor));
                    itemTable.addCell(new Cell().add(new Paragraph(item.get("quantity"))).setFontColor(textColor));
                    itemTable.addCell(new Cell().add(new Paragraph(item.get("price"))).setFontColor(textColor));
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

                // Save the file
                fileOutputStream.write(outputStream.toByteArray());

                return "PDF successfully saved at: " + filePath;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
