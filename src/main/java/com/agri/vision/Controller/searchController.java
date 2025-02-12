package com.agri.vision.Controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agri.vision.Model.comment;
import com.agri.vision.Model.product;
import com.agri.vision.Model.user;
import com.agri.vision.Repo.commentRepo;
import com.agri.vision.Repo.productRepo;
import com.agri.vision.Repo.userRepo;
import com.agri.vision.Service.CloudinaryService;
import com.agri.vision.Service.JwtService;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class searchController {

    private final CloudinaryService cloudinaryService;

    public searchController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @Autowired
    private productRepo productrepo;

    @Autowired
    private commentRepo commentrepo;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private userRepo userrepo;

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Friday 24 jan 2025
    // Function : Search Query Dyanmically
    // give any query and also any case it will send the data to user
    ///////////////////////////////////////////
    @GetMapping("/user/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query) {
        List<product> pd = this.productrepo.findByproductnameContainingIgnoreCase(query);
        return ResponseEntity.ok(pd);
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Friday 31 jan 2025
    // Function : Add comment into Database
    // get id and details of user comment and add this into database
    ///////////////////////////////////////////
    @PostMapping(value = "/user/comment", consumes = "multipart/form-data")
    public ResponseEntity<?> commentAdd(
            @RequestHeader("Authorization") String token,
            @RequestParam("pid") Long pid,
            @RequestParam(value = "star", required = false, defaultValue = "0") int star,
            @RequestParam(value = "message", required = false) String message,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video) throws IOException {
        try {
            // Extract username from token
            String usernameFromToken = jwtService.extractUsername(token.substring(7));
            user existingUser = userrepo.findByUsername(usernameFromToken);

            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found.");
            }

            String UID = usernameFromToken;
            product pd = productrepo.findById(pid).orElse(null);

            if (pd != null) {
                comment cm = new comment();
                if (star >= 0) {
                    cm.setStar(star);
                }
                if (message != null && !message.isEmpty()) {
                    cm.setCommentText(message);
                }
                cm.setPid(pid);
                cm.setUname(UID);

                if (image != null && !image.isEmpty()) {
                    System.out.println("Image Content Type: " + image.getContentType());
                    if (!image.getContentType().startsWith("image/")) {
                        return ResponseEntity.badRequest().body("Invalid image file format.");
                    }
                    String fileUrl = cloudinaryService.uploadFile(image);
                    if (fileUrl == null || fileUrl.isEmpty()) {
                        return ResponseEntity.badRequest().body("Image upload failed.");
                    }
                    cm.setImage(fileUrl);
                }

                if (video != null && !video.isEmpty()) {
                    System.out.println("Video Content Type: " + video.getContentType());
                    if (!video.getContentType().startsWith("video/")) {
                        return ResponseEntity.badRequest().body("Invalid video file format.");
                    }
                    String fileUrl = cloudinaryService.uploadFile(video);
                    if (fileUrl == null || fileUrl.isEmpty()) {
                        return ResponseEntity.badRequest().body("Video upload failed.");
                    }
                    cm.setVideo(fileUrl);
                }

                commentrepo.save(cm);
                return ResponseEntity.ok("Comment added successfully.");
            } else {
                return ResponseEntity.badRequest().body("Product not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid Request: " + e.getMessage());
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Friday 31 jan 2025
    // Function : Add comment into Database
    // get id and details of user comment and add tit into database
    ///////////////////////////////////////////
    @PostMapping("/user/commentview/{id}")
    public ResponseEntity<?> commentView(@PathVariable("id") Long id) {
        List<comment> comment = commentrepo.findByPid(id);
        return ResponseEntity.ok(comment);
    }

}
