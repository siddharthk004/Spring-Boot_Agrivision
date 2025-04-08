package com.agri.vision.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

import com.agri.vision.DTO.WishlistProductDTO;
import com.agri.vision.Model.Cart;
import com.agri.vision.Model.product;
import com.agri.vision.Model.wishlist;
import com.agri.vision.Repo.CartRepo;
import com.agri.vision.Repo.productRepo;
import com.agri.vision.Repo.wishlistRepo;
import com.agri.vision.Service.JwtService;
import com.agri.vision.Service.UsageService;
import com.agri.vision.Service.productService;
import com.agri.vision.helper.messageHelper;

import jakarta.transaction.Transactional;

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
    private productRepo productRepo;

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
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Long> request) {
        try {
            // Extract the username from the token
            String usernameFromToken = jwtService.extractUsername(token.substring(7)); // Assuming "Bearer " prefix

            // Extract productId from request
            Long productId = request.get("productId");
            if (productId == null) {
                return ResponseEntity.badRequest().body("Product ID is required!");
            }

            // Check if the product already exists in the cart for the given user
            Cart existingCartItem = cartrepo.findByUsernameAndProductId(usernameFromToken, productId);
            if (existingCartItem != null) {
                return ResponseEntity.badRequest()
                        .body("Product already exists in the cart! Use update API to modify quantity.");
            }

            // Create and populate a Cart object with initial quantity 1
            Cart cart = new Cart();
            cart.setUsername(usernameFromToken);
            cart.setProductId(productId);
            cart.setQuantity(1); // Setting initial quantity to 1

            // Save to the cart repository
            cartrepo.save(cart);

            return ResponseEntity.ok("Product added to cart successfully!");
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Login please | Username ID not found");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // day , Date : Sunday 23 Mar 2025
    // Function : Update Cart Quantity
    ///////////////////////////////////////////
    @PostMapping("/updateCartQuantity")
    public ResponseEntity<?> updateCartQuantity(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Integer> request) {
        try {
            // Extract the username from the token
            String usernameFromToken = jwtService.extractUsername(token.substring(7));

            // Extract productId and new quantity from request
            Long productId = Long.valueOf(request.get("productId"));
            Integer quantity = request.get("quantity");

            if (productId == null || quantity == null || quantity <= 0) {
                return ResponseEntity.badRequest().body("Valid product ID and quantity are required!");
            }

            // Find the cart item
            Cart cartItem = cartrepo.findByUsernameAndProductId(usernameFromToken, productId);
            if (cartItem == null) {
                return ResponseEntity.badRequest().body("Product not found in the cart!");
            }

            // Update quantity
            cartItem.setQuantity(quantity);
            cartrepo.save(cartItem);

            return ResponseEntity.ok("Cart quantity updated successfully!");
        } catch (RuntimeException e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body("Login please | Username ID not found");
        } catch (Exception e) {
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
            String usernameFromToken = jwtService.extractUsername(token.substring(7));

            // Fetch cart items by username
            List<Cart> carts = cartrepo.findByUsername(usernameFromToken);

            if (carts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No cart found for this user.");
            }

            // Fetch product details along with quantity for each productId in the cart
            List<Map<String, Object>> cartProducts = carts.stream().map(cart -> {
                product product = productRepo.findById(cart.getProductId()).orElse(null);
                if (product != null) {
                    Map<String, Object> productDetails = new HashMap<>();
                    productDetails.put("product", product);
                    productDetails.put("quantity", cart.getQuantity());
                    productDetails.put("id", cart.getId());
                    return productDetails;
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toList());

            return ResponseEntity.ok(cartProducts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching the cart.");
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
    @PostMapping("/wishlist/add")
    public ResponseEntity<?> addToWishlist(
            @RequestHeader("Authorization") String token,
            @RequestBody Map<String, Long> request) {
        try {
            String username = jwtService.extractUsername(token.substring(7));
            Long productId = request.get("productId");

            if (productId == null) {
                return ResponseEntity.badRequest().body("Product ID is required!");
            }

            // Check if product already exists in wishlist
            if (wishlistrepo.existsByUsernameAndProductId(username, productId)) {
                return ResponseEntity.badRequest().body("Product already exists in the wishlist!");
            }

            // Add product to wishlist
            wishlist wish = new wishlist();
            wish.setUsername(username);
            wish.setProductId(productId);
            wishlistrepo.save(wish);

            return ResponseEntity.ok("Product added to wishlist successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // Date : Thursday, 23 Jan 2025
    // Function : View All Wishlist Products
    // Input: Token
    ///////////////////////////////////////////
    @GetMapping("/wishlist/view")
public ResponseEntity<?> getAllWishlistItems(@RequestHeader("Authorization") String token) {
    try {
        String username = jwtService.extractUsername(token.substring(7));

        // Fetch wishlist items by username
        List<wishlist> wishlists = wishlistrepo.findByUsername(username);

        if (wishlists.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No wishlist found for this user.");
        }

        // Convert wishlist items to WishlistProductDTO
        List<WishlistProductDTO> wishlistProducts = wishlists.stream()
                .map(w -> productRepo.findById(w.getProductId())
                        .map(p -> new WishlistProductDTO(w.getId(), p))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return ResponseEntity.ok(wishlistProducts);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An error occurred while fetching wishlists.");
    }
}


    ///////////////////////////////////////////
    // Name : Siddharth Kardile
    // Date : Thursday, 23 Jan 2025
    // Function : Delete Wishlist Product by ID
    // Input: Wishlist ID
    ///////////////////////////////////////////
    @DeleteMapping("/user/wishlist/delete/{id}")
    @Transactional
    public ResponseEntity<?> deleteWishlistById(@PathVariable Long id) {
        try {
            wishlist wish = wishlistrepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Wishlist item not found with ID: " + id));

            wishlistrepo.delete(wish);
            return ResponseEntity.ok("Wishlist item deleted successfully with ID: " + id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
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
