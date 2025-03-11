package com.agri.vision.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    // Fetching values from application.properties
    @Value("${cloudinary.cloud_name}")
    private String cloudName;

    @Value("${cloudinary.api_key}")
    private String apiKey;

    @Value("${cloudinary.api_secret}")
    private String apiSecret;

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", cloudName,
            "api_key", apiKey,
            "api_secret", apiSecret
        ));
    }

    // Upload images and videos
    public String uploadFile(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IOException("File is empty or null.");
            }

            String contentType = file.getContentType();
            if (contentType == null) {
                throw new IOException("File has no content type.");
            }

            String resourceType;
            if (contentType.startsWith("image/")) {
                resourceType = "image";
            } else if (contentType.startsWith("video/")) {
                resourceType = "video";
            } else {
                throw new IOException("Unsupported file type: " + contentType);
            }

            long timestamp = System.currentTimeMillis() / 1000;
            String signatureString = "timestamp=" + timestamp + apiSecret;
            String signature = DigestUtils.sha1Hex(signatureString);

            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("timestamp", timestamp);
            uploadParams.put("signature", signature);
            uploadParams.put("api_key", apiKey);
            uploadParams.put("resource_type", resourceType);

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

            return (String) uploadResult.get("url");
        } catch (Exception e) {
            System.out.println("Cloudinary Upload Error: " + e.getMessage());
            return null;
        }
    }

    // New Method: Upload PDF
    public String uploadPdf(File pdfFile) {
        try {
            if (pdfFile == null || !pdfFile.exists()) {
                throw new IOException("PDF file does not exist.");
            }

            long timestamp = System.currentTimeMillis() / 1000;
            String signatureString = "timestamp=" + timestamp + apiSecret;
            String signature = DigestUtils.sha1Hex(signatureString);

            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("timestamp", timestamp);
            uploadParams.put("signature", signature);
            uploadParams.put("api_key", apiKey);
            uploadParams.put("resource_type", "raw"); // PDFs are stored as "raw" files

            Map uploadResult = cloudinary.uploader().upload(pdfFile, uploadParams);

            return (String) uploadResult.get("url");
        } catch (Exception e) {
            System.out.println("PDF Upload Error: " + e.getMessage());
            return null;
        }
    }
}
