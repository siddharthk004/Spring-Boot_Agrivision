package com.agri.vision.DTO;

import org.springframework.web.multipart.MultipartFile;

public class CommentRequest {
    
    private Long Pid;
    private int star;
    private String message;
    private MultipartFile image; // Add this field for the image
    private MultipartFile video; // Add this field for the video

}
