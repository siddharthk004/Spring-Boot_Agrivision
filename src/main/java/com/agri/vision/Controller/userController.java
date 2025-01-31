package com.agri.vision.Controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.agri.vision.DTO.UserRegistrationRequest;
import com.agri.vision.Model.LoginReq;
import com.agri.vision.Model.userotp;
import com.agri.vision.Model.user;
import com.agri.vision.Repo.userOtpRepo;
import com.agri.vision.Repo.userRepo;
import com.agri.vision.Service.EmailService;
import com.agri.vision.Service.GenerateOTPService;
import com.agri.vision.Service.JwtService;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class userController {

    private static final Logger logger = LoggerFactory.getLogger(userController.class);

    @Autowired
    private GenerateOTPService generateOTPService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private userRepo userrepo;

    @Autowired
    private userOtpRepo userotprepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 14 jan 2025
    // Function : Encode the Password
    ///////////////////////////////////////////
    public void encodeExistingPasswords() {
        List<user> users = userrepo.findAll();
        for (user user : users) {
            if (!user.getPassword().startsWith("{")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userrepo.save(user);
            }
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 14 jan 2025
    // Function : Register New User
    // take username , email, endname, address, contact, occupation,password
    ///////////////////////////////////////////
    @PostMapping("/user/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationRequest request) {
        // Check if usernameis null
        if (request.getUsername() == null) {
            return ResponseEntity.badRequest().body("Username is Must!");
        }
        // Check if email is null
        if (request.getEmail() == null) {
            return ResponseEntity.badRequest().body("Email is Must!");
        }
        // Check if username already exists
        if (userrepo.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username is already taken!");
        }
        // Check if email already exists
        if (userrepo.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken please choose different one!");
        }

        // Create a new user
        user newUser = new user();
        newUser.setUsername(request.getUsername());
        System.out.println("username " + request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setEndname(request.getEndname());
        newUser.setAddress(request.getAddress());
        newUser.setContact(request.getContact());
        newUser.setOccupation(request.getOccupation());
        System.out.println("mail id " + request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Encrypt the password

        // Save to database
        userrepo.save(newUser);

        return ResponseEntity.ok("User registered successfully!");
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Thursday 14 jan 2025
    // Function : Login User
    // take usename and password and return the email username and token
    ///////////////////////////////////////////
    @PostMapping("/user/login")
    public ResponseEntity<?> signin(@RequestBody LoginReq loginRequest) {
        try {
            // Retrieve the user by username
            user user = userrepo.findByUsername(loginRequest.getUsername());

            // Compare the provided plain password with the hashed password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("Invalid username or password");
            }

            // Generate a JWT token using JwtService
            String token = jwtService.generateToken(user.getUsername());

            // Return a success response with the generated token
            Map<String, Object> response = new HashMap<>();
            response.put("email", user.getEmail());
            response.put("username", user.getUsername());
            response.put("token", token);
            logger.debug("Generated JWT Token: " + token);

            return ResponseEntity.ok(response);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 23 jan 2025
    // Function : View Profile Of USer
    // take token and return all the details of user
    ///////////////////////////////////////////
    @PostMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        // Correctly get the username from the Authentication object
        String username = authentication.getName();
        System.out.println(username);
        System.out.println(authentication);

        // Fetch the user from the database
        user user = userrepo.findByUsername(username);

        // If the user is not found, handle it appropriately
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Convert the image byte array to Base64 if the profile image exists
        String base64Image = null;
        if (user.getProfileImage() != null) {
            base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(user.getProfileImage());
        }

        // Build the response with user details and Base64 image
        Map<String, Object> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("image", base64Image); // Send Base64 image string
        response.put("email", user.getEmail());
        response.put("endname", user.getEndname());
        response.put("address", user.getAddress());
        response.put("contact", user.getContact());
        response.put("occupation", user.getOccupation());

        return ResponseEntity.ok(response);
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 23 jan 2025
    // Function : edit Profile Of USer
    // give token and any field that we want to update and return success
    ///////////////////////////////////////////
    @PostMapping("/editProfile")
    public ResponseEntity<?> editProfile(
            @RequestHeader("Authorization") String token, // Get the token from the request header
            @RequestBody UserRegistrationRequest request) { // Accept data in JSON format

        // Extract the username from the token
        String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix in token

        // Find the existing user by username (from the token)
        user existingUser = userrepo.findByUsername(usernameFromToken);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Only allow certain fields to be updated (excluding username, email, and
        // password)
        if (request.getEndname() != null && !request.getEndname().isEmpty()) {
            existingUser.setEndname(request.getEndname());
        }
        if (request.getAddress() != null && !request.getAddress().isEmpty()) {
            existingUser.setAddress(request.getAddress());
        }
        if (request.getContact() != null && !request.getContact().isEmpty()) {
            existingUser.setContact(request.getContact());
        }
        if (request.getOccupation() != null && !request.getOccupation().isEmpty()) {
            existingUser.setOccupation(request.getOccupation());
        }

        // Save the updated user to the database
        userrepo.save(existingUser);

        // Return the updated user profile (excluding password, email, username)
        Map<String, Object> response = new HashMap<>();
        response.put("username", existingUser.getUsername());
        response.put("endname", existingUser.getEndname());
        response.put("address", existingUser.getAddress());
        response.put("contact", existingUser.getContact());
        response.put("occupation", existingUser.getOccupation());
        response.put("profileImage",
                existingUser.getProfileImage() != null ? "Profile image updated" : "No profile image");

        return ResponseEntity.ok(response); // Return updated details
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date :Tuesday  24 jan 2025
    // Function : edit Profile picture Of USer
    // give token and Profile Picture that we want to update and return success
    ///////////////////////////////////////////
    @PostMapping("/editProfilePic")
    public ResponseEntity<?> editProfilePic(
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        // Extract the username from the token
        String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix in token

        // Find the existing user by username (from the token)
        user existingUser = userrepo.findByUsername(usernameFromToken);
        if (existingUser == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Check if an image is provided
        if (image == null || image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No image file provided");
        }

        try {
            // Update the profile image
            existingUser.setProfileImage(image.getBytes());
            userrepo.save(existingUser);

            return ResponseEntity.ok("Profile picture updated successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving image: " + e.getMessage());
        }
    }
 
    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Wednesday 28 jan 2025
    // Function : Sent Otp Through Email and Save it
    // give token and generate the otp and sent email
    ///////////////////////////////////////////
    @PostMapping("/user/forgotPassword/MailOTP")
    public ResponseEntity<?> forgotPasswordSendOtp(@RequestHeader("Authorization") String token) {
        try {
            // Extract the username from the token (assuming "Bearer " prefix)
            String usernameFromToken = jwtService.extractUsername(token.substring(7));

            // Fetch the email using the username
            String email = userrepo.getEmailByUsername(usernameFromToken);

            // Check if email exists
            if (email == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found for the user.");
            }

            String otp = generateOTPService.generateOtp(6);
            System.out.println("otp " + otp);

            String subject = "AgriVision OTP - Email";
            String message = "Your OTP is: "+otp;

            boolean success = emailService.sendEmail(email, subject ,message);
            if (success) {
                // Find the existing user by username (from the token)
                userotp existingUser = userotprepo.findByUsername(usernameFromToken);
                if (existingUser == null) {
                    userotp newotp = new userotp();
                    newotp.setUsername(usernameFromToken);
                    newotp.setOtp(otp);
                    userotprepo.save(newotp);
                } else {
                    existingUser.setUsername(usernameFromToken);
                    existingUser.setOtp(otp);
                    userotprepo.save(existingUser);
                }

                return ResponseEntity.ok("OTP sent successfully");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send OTP");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Wednesday 28 jan 2025
    // Function : get otp and cheak it is correct  ?
    ///////////////////////////////////////////
    @PostMapping("/user/forgotPassword/OTP")
    public ResponseEntity<?> forgotPasswordValidateOtp(@RequestHeader("Authorization") String token,
            @RequestBody Long userotp) {
        try {
            // Extract the username from the token (assuming "Bearer " prefix)
            String usernameFromToken = jwtService.extractUsername(token.substring(7));

            // Fetch the email using the username
            Long DBotp = userrepo.getOtpByUsername(usernameFromToken);
            
            userotp existingUser = userotprepo.findByUsername(usernameFromToken);
            
            if (DBotp == userotp) {
                // delete that id row from database
                userotprepo.deleteById(existingUser.getId());                
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.ok(false);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }
    
    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Thursday 29 jan 2025
    // Function : New PassWord
    // give token and password and it will update the pasword of that specific user
    ///////////////////////////////////////////
    @PostMapping("/user/forgotPassword/NewPassword")
    public ResponseEntity<?> newPassword(@RequestHeader("Authorization") String token,
            @RequestBody String password) {
        try {
            // Extract the username from the token (assuming "Bearer " prefix)
            String usernameFromToken = jwtService.extractUsername(token.substring(7));
            
            // Find the user password for update by username (from the token)
            user user = userrepo.getUserByUserName(usernameFromToken);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            } 

            // Update the user password
            user.setPassword(passwordEncoder.encode(password));
            userrepo.save(user);

            return ResponseEntity.ok(true);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

}
