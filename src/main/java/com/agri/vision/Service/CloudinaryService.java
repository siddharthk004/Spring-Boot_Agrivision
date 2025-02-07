package com.agri.vision.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public String uploadFile(MultipartFile file) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                throw new IOException("File is empty or null.");
            }

            // Validate content type
            String contentType = file.getContentType();
            if (contentType == null) {
                throw new IOException("File has no content type.");
            }

            // Determine resource type
            String resourceType;
            if (contentType.startsWith("image/")) {
                resourceType = "image";
            } else if (contentType.startsWith("video/")) {
                resourceType = "video";
            } else {
                throw new IOException("Unsupported file type: " + contentType);
            }

            // Generate timestamp
            long timestamp = System.currentTimeMillis() / 1000; // Convert to seconds
            
            // Generate signature
            String signatureString = "timestamp=" + timestamp + API_SECRET;
            String signature = DigestUtils.sha1Hex(signatureString);

            // Upload parameters
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("timestamp", timestamp);
            uploadParams.put("signature", signature);
            uploadParams.put("api_key", "745913769633583");
            uploadParams.put("resource_type", resourceType); // Set correct type

            // Upload file
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            
            // Extract and return the URL
            String uploadedUrl = (String) uploadResult.get("url");
            System.out.println("Uploaded File URL: " + uploadedUrl);
            return uploadedUrl;
        } catch (Exception e) {
            System.out.println("Cloudinary Upload Error: " + e.getMessage());
            return null;
        }
    }
}
