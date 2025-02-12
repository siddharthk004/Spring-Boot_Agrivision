package com.agri.vision.Controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.Model.Cart;
import com.agri.vision.Model.product;
import com.agri.vision.Model.wishlist;
import com.agri.vision.Repo.CartRepo;
import com.agri.vision.Repo.wishlistRepo;
import com.agri.vision.Service.JwtService;
import com.agri.vision.Service.UsageService;
import com.agri.vision.Service.productService;
import com.agri.vision.helper.messageHelper;

@Controller
@RestController
@CrossOrigin(origins = "/**") // this url is react only this will be accept here
@RequestMapping("/api/v1/auth") // base url http://localhost:8080/ onwards
public class productController {

    private final UsageService usageService;

    public productController(UsageService usageService) {
        this.usageService = usageService;
    }

    @Autowired
    private JwtService jwtService;

    @Autowired
    private wishlistRepo wishlistrepo;

    @Autowired
    private CartRepo cartrepo;

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Thursday 23 jan 2025
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
    // day , Date : Thursday 23 jan 2025
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
    // day , Date : Thursday 23 jan 2025
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
    // day , Date : Thursday 23 jan 2025
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
    // day , Date : Thursday 23 jan 2025
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
    // day , Date : Thursday 23 jan 2025
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
    // day , Date : friday 24 jan 2025
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
    // day , Date : friday 24 jan 2025
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

    /////////////////////////////////////////////////////////////
    // Name : Sakshi Ladkat
    // day , Date :Tuesday 29 jan 2025
    // Function : product Api
    // view all product , product by id , product by category
    ////////////////////////////////////////////////////////////

    @Autowired
    private productService service;

    @PostMapping("/user/product")
    public ResponseEntity<List<product>> getAllProduct() {
        return new ResponseEntity<>(service.getAllProduct(), HttpStatus.OK);
    }

    @PostMapping("/user/product/id/{id}")
    public ResponseEntity<product> getProductById(@PathVariable("id") int id) {
        product product = service.getProductById(id);
        if (product != null) {
            return new ResponseEntity<>(service.getProductById(id), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/user/product/category/{category}")
    public ResponseEntity<List<product>> getProductsByCategory(@PathVariable("category") String category) {
        return new ResponseEntity<>(service.getProductByCategory(category), HttpStatus.OK);
    }
}
