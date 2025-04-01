package com.agri.vision.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret));
    }

    // Upload images and videos
    public String uploadFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IOException("File is empty or null.");
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");

        } catch (Exception e) {
            System.out.println("Cloudinary Upload Error: " + e.getMessage());
            return null;
        }
    }

    // Upload PDF
    public String uploadPdf(File pdfFile) {
        try {
            if (pdfFile == null || !pdfFile.exists()) {
                throw new IOException("PDF file does not exist.");
            }
            Map uploadResult = cloudinary.uploader().upload(pdfFile, ObjectUtils.asMap(
                    "resource_type", "auto"));

            return (String) uploadResult.get("url");

        } catch (Exception e) {
            System.out.println("PDF Upload Error: " + e.getMessage());
            return null;
        }
    }
}
