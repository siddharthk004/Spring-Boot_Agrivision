package com.agri.vision.Controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
// import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.agri.vision.DTO.UserRegistrationRequest;
import com.agri.vision.Model.Cart;
import com.agri.vision.Model.LoginReq;
import com.agri.vision.Model.user;
import com.agri.vision.Model.wishlist;
import com.agri.vision.Repo.CartRepo;
import com.agri.vision.Repo.userRepo;
import com.agri.vision.Repo.wishlistRepo;
import com.agri.vision.Service.JwtService;
import com.agri.vision.Service.UsageService;
import com.agri.vision.helper.messageHelper;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class userController {

    @Autowired
    private JwtService jwtService;

    private final UsageService usageService;

    private static final Logger logger = LoggerFactory.getLogger(userController.class);

    public userController(UsageService usageService) {
        this.usageService = usageService;
        // this.jwtService = jwtService;
    }

    // autowiring the user repository
    @Autowired
    private userRepo userrepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private wishlistRepo wishlistrepo;

    @Autowired
    private CartRepo cartrepo;

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
    // day , Date : Tuesday 14 jan 2025
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

    // get all value from database from /user/GetAllData
    @GetMapping("/user/GetAllData")
    List<user> getAllUsers() {
        return userrepo.findAll();
    }

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

    @PostMapping("/user/mail")
    public ResponseEntity<?> profileCheck(@RequestParam String email) {
        try {
            // Retrieve the user by email
            user user = userrepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Email Id Not Found"));

            System.out.println(user);

            // If user exists, return success message
            return ResponseEntity.ok("success");

        } catch (RuntimeException e) {
            // Handle case where email was not found
            System.err.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("login please | Email Id Not Found");

        } catch (Exception e) {
            // Handle other general exceptions
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/user/editprofile")
    public ResponseEntity<?> editProfile(
            @RequestPart("data") UserRegistrationRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        Optional<user> existingUserOpt = userrepo.findByEmail(request.getEmail());
        if (existingUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        user existingUser = existingUserOpt.get();

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

        // Handle image upload
        if (image != null && !image.isEmpty()) {
            try {
                existingUser.setProfileImage(image.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error saving image: " + e.getMessage());
            }
        }

        userrepo.save(existingUser);
        return ResponseEntity.ok("Profile updated successfully!");
    }

    @PostMapping("/user/wishlist")
    public ResponseEntity<?> addToWishlist(
            @RequestParam String email,
            @RequestBody wishlist request) {
        try {

            // Check if the product already exists in the wishlist for the given email
            boolean exists = wishlistrepo.existsByEmailAndProductname(email, request.getProductname());
            if (exists) {
                return ResponseEntity.badRequest().body("Product already exists in the wishlist!");
            }

            // Create and populate a Wishlist object
            wishlist wishlist = new wishlist();
            wishlist.setEmail(email);
            wishlist.setProductname(request.getProductname());
            wishlist.setProductcompanyname(request.getProductcompanyname());
            wishlist.setProductimage(request.getProductimage());
            wishlist.setBeforediscount(request.getBeforediscount());
            wishlist.setAfterdiscount(request.getAfterdiscount());
            wishlist.setDiscount(request.getDiscount());

            // Save to the wishlist repository
            wishlistrepo.save(wishlist);

            // Return success response
            return ResponseEntity.ok("Product added to wishlist successfully!");

        } catch (RuntimeException e) {
            // Handle case where email was not found
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Login please | Email Id Not Found");

        } catch (Exception e) {
            // Handle other general exceptions
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    // get all value from database from /user/GetAllData
    @GetMapping("/user/ViewAllWishlist")
    public ResponseEntity<?> getAllWishlists(@RequestParam String email) {
        try {
            // Fetch wishlists by email
            List<wishlist> wishlists = wishlistrepo.findByEmail(email);

            if (wishlists.isEmpty()) {
                // Return a 404 response if no data is found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No wishlist found for this email.");
            }

            // Return the list of wishlists
            return ResponseEntity.ok(wishlists);
        } catch (Exception e) {
            // Handle any errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching wishlists.");
        }
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<Integer> getUserCount(@PathVariable String email) {
        int count = usageService.countByEmail(email);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/cart/{email}")
    public ResponseEntity<Integer> getCartCount(@PathVariable String email) {
        int count = usageService.countByCartEmail(email);
        return ResponseEntity.ok(count);
    }

    @PostMapping("/user/cart")
    public ResponseEntity<?> addToCart(
            @RequestParam String email,
            @RequestBody wishlist request) {
        try {

            // Check if the product already exists in the wishlist for the given email
            boolean exists = cartrepo.existsByEmailAndProductname(email, request.getProductname());
            if (exists) {
                return ResponseEntity.badRequest().body("Product already exists in the cart!");
            }

            // Create and populate a Wishlist object
            Cart cart = new Cart();
            cart.setEmail(email);
            cart.setProductname(request.getProductname());
            cart.setProductcompanyname(request.getProductcompanyname());
            cart.setProductimage(request.getProductimage());
            cart.setBeforediscount(request.getBeforediscount());
            cart.setAfterdiscount(request.getAfterdiscount());
            cart.setDiscount(request.getDiscount());

            // Save to the wishlist repository
            cartrepo.save(cart);

            // Return success response
            return ResponseEntity.ok("Product added to cart successfully!");

        } catch (RuntimeException e) {
            // Handle case where email was not found
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Login please | Email Id Not Found");

        } catch (Exception e) {
            // Handle other general exceptions
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    // get all value from database from /user/GetAllData
    @GetMapping("/user/ViewAllcart")
    public ResponseEntity<?> getAllCart(@RequestParam String email) {
        try {
            // Fetch wishlists by email
            List<Cart> carts = cartrepo.findByEmail(email);

            if (carts.isEmpty()) {
                // Return a 404 response if no data is found
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cart found for this email.");
            }

            // Return the list of wishlists
            return ResponseEntity.ok(carts);
        } catch (Exception e) {
            // Handle any errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching cart.");
        }
    }

    // Delete wishlist details by ID
    @DeleteMapping("/user/DeleteWishList/{id}")
    public ResponseEntity<?> deleteWishlistById(@PathVariable Integer id) {
        try {
            // Check if the wishlist exists
            wishlist wishlistItem = wishlistrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Wishlist not found with ID: " + id));

            // Delete the wishlist item
            wishlistrepo.delete(wishlistItem);

            // Return success response
            return ResponseEntity.ok(new messageHelper(true, "Wishlist item deleted successfully with ID: " + id));
        } catch (RuntimeException e) {
            // Handle specific exception when wishlist item is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, e.getMessage()));
        } catch (Exception e) {
            // Handle generic exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new messageHelper(false, "An error occurred: " + e.getMessage()));
        }
    }

    // Delete wishlist details by ID
    @DeleteMapping("/user/DeleteCart/{id}")
    public ResponseEntity<?> deleteCartById(@PathVariable Long id) {
        try {
            // Check if the wishlist exists
            Cart cart = cartrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("cart not found with ID: " + id));

            // Delete the wishlist item
            cartrepo.delete(cart);

            // Return success response
            return ResponseEntity.ok(new messageHelper(true, "cart item deleted successfully with ID: " + id));
        } catch (RuntimeException e) {
            // Handle specific exception when wishlist item is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new messageHelper(false, e.getMessage()));
        } catch (Exception e) {
            // Handle generic exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new messageHelper(false, "An error occurred: " + e.getMessage()));
        }
    }

}
