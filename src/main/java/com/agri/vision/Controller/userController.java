package com.agri.vision.Controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
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
    }

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
    // day , Date : Tuesday 23 jan 2025
    // Function : Add Product To Cart
    // Add Product To Cart User Give Token and Detils of product it will add to cart
    ///////////////////////////////////////////
    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(
            @RequestHeader("Authorization") String token, // Get the token from the request header
            @RequestBody wishlist request) {
        try {

            // Extract the username from the token
            String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix in
                                                                                       // token

            // Check if the product already exists in the wishlist for the given email
            boolean exists = cartrepo.existsByUsernameAndProductname(usernameFromToken, request.getProductname());
            if (exists) {
                return ResponseEntity.badRequest().body("Product already exists in the cart!");
            }

            // Create and populate a Wishlist object
            Cart cart = new Cart();
            cart.setUsername(usernameFromToken);
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
            return ResponseEntity.badRequest().body("Login please | username Id Not Found");

        } catch (Exception e) {
            // Handle other general exceptions
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 23 jan 2025
    // Function : View All Cart Products
    // Give Token and get All product
    ///////////////////////////////////////////
    @GetMapping("/viewAllCart")
    public ResponseEntity<?> getAllCart(@RequestHeader("Authorization") String token) {
        try {
            // Extract the username from the token
            String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix in

            // Fetch wishlists by email
            List<Cart> carts = cartrepo.findByUsername(usernameFromToken);

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

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 23 jan 2025
    // Function : Delete Cart Product by id
    // Give id and delete that id product
    ///////////////////////////////////////////
    @DeleteMapping("/user/deleteCart/{id}")
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

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 23 jan 2025
    // Function : Add Product To Wishlist
    // Add Product To Wishlist User Give Token and Detils of product it will add to
    /////////////////////////////////////////// 
    @PostMapping("/addToWishlist")
    public ResponseEntity<?> addToWishlist(
            @RequestHeader("Authorization") String token, // Get the token from the request header
            @RequestBody wishlist request) {
        try {
            // Extract the username from the token
            String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix in

            // Check if the product already exists in the wishlist for the given email
            boolean exists = wishlistrepo.existsByUsernameAndProductname(usernameFromToken, request.getProductname());
            if (exists) {
                return ResponseEntity.badRequest().body("Product already exists in the wishlist!");
            }

            // Create and populate a Wishlist object
            wishlist wishlist = new wishlist();
            wishlist.setUsername(usernameFromToken);
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
            return ResponseEntity.badRequest().body("Login please | username Id Not Found");

        } catch (Exception e) {
            // Handle other general exceptions
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 23 jan 2025
    // Function : View All Wishlist Products
    // Give Token and get All product
    ///////////////////////////////////////////
    @GetMapping("/viewAllWishlist")
    public ResponseEntity<?> getAllWishlists(@RequestHeader("Authorization") String token) {
        try {
            // Extract the username from the token
            String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix in

            // Fetch wishlists by email
            List<wishlist> wishlists = wishlistrepo.findByUsername(usernameFromToken);

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

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 23 jan 2025
    // Function : Delete Wishlist Product by id
    // Give id and delete that id product
    ///////////////////////////////////////////
    @DeleteMapping("/user/deleteWishlist/{id}")
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

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 25 jan 2025
    // Function : Total number of user repeated
    // give token and it will return the number of repetation
    ///////////////////////////////////////////
    @GetMapping("/user/wishlistCount")
    public ResponseEntity<Integer> getUserCount(@RequestHeader("Authorization") String token) {

        // Extract the username from the token
        String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix in

        int count = usageService.countByWishlistUsername(usernameFromToken);
        return ResponseEntity.ok(count);
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Tuesday 25 jan 2025
    // Function : Total number of user repeated
    // give token and it will return the number of repetation
    ///////////////////////////////////////////
    @GetMapping("/user/cartCount")
    public ResponseEntity<Integer> getCartCount(@RequestHeader("Authorization") String token) {
        
        // Extract the username from the token
        String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix in

        int count = usageService.countByCartUsername(usernameFromToken);
        return ResponseEntity.ok(count);
    }






    // get all value from database from /user/GetAllData
    @GetMapping("/user/GetAllData")
    List<user> getAllUsers() {
        return userrepo.findAll();
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

}
