package com.agri.vision.Controller;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
// import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import com.agri.vision.Service.UsageService;
import com.agri.vision.helper.messageHelper;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class userController {       

    private final UsageService usageService;

    public userController(UsageService usageService) {
        this.usageService = usageService;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    public void encodeExistingPasswords() {
        List<user> users = userrepo.findAll();
        for (user user : users) {
            if (!user.getPassword().startsWith("{")) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userrepo.save(user);
            }
        }
    }

    // add or save the new user here
    @PostMapping("/user/save")
    user newUser(@RequestBody user newUser) {
        return userrepo.save(newUser);
    }

    // user registration page register using /user/register
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

    // get all value from database from /user/GetAllData
    @GetMapping("/user/GetAllData")
    List<user> getAllUsers() {
        return userrepo.findAll();
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> signin(@RequestBody LoginReq loginRequest) {
        try {
            // Retrieve the user by username
            user user = userrepo.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Log stored encoded password for debugging (Remove in production)
            System.out.println("Stored password: " + user.getPassword());
            System.out.println("Stored mail: " + user.getEmail());

            // Compare the provided plain password with the hashed password
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                System.err.println("Authentication failed: Password does not match");
                return ResponseEntity.badRequest().body("Invalid username or password");
            }

            // Create the AuthenticationToken with the provided username and password
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword());

            // Authenticate using the AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            // String username = authentication.getName();
            String emailId = user.getEmail();

            // Set the Authentication object in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Here you could generate and return an authentication token (e.g., JWT)
            // For simplicity, just a success message for now
            return ResponseEntity.ok(emailId);

        } catch (UsernameNotFoundException e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.badRequest().body("Invalid username or password");

        } catch (Exception e) {
            System.err.println("Authentication failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login");
        }
    }

    @PostMapping("/user/profile")
    public ResponseEntity<?> getUserProfile(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        Optional<user> userOptional = userrepo.findByEmail(email);

        if (userOptional.isPresent()) {
            user user = userOptional.get();

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
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
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
