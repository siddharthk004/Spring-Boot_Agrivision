package com.agri.vision.Service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;
    private static final String API_SECRET = "C1fHGogH55jLynulVH9QtIua9O8"; // Ensure this is correct!

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dmejw3uwe",
            "api_key", "745913769633583",
            "api_secret", API_SECRET
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
            String signatureString = "timestamp=" + timestamp + API_SECRET;
            String signature = DigestUtils.sha1Hex(signatureString);

            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("timestamp", timestamp);
            uploadParams.put("signature", signature);
            uploadParams.put("api_key", "745913769633583");
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
            String signatureString = "timestamp=" + timestamp + API_SECRET;
            String signature = DigestUtils.sha1Hex(signatureString);

            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("timestamp", timestamp);
            uploadParams.put("signature", signature);
            uploadParams.put("api_key", "745913769633583");
            uploadParams.put("resource_type", "raw"); // PDFs are stored as "raw" files

            Map uploadResult = cloudinary.uploader().upload(pdfFile, uploadParams);

            return (String) uploadResult.get("url");
        } catch (Exception e) {
            System.out.println("PDF Upload Error: " + e.getMessage());
            return null;
        }
    }
}
