package com.agri.vision.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agri.vision.DTO.AdminRequest;
import com.agri.vision.Model.AddManage;
import com.agri.vision.Model.Admin;
import com.agri.vision.Model.product;
import com.agri.vision.Repo.AddManageRepo;
import com.agri.vision.Repo.AdminRepo;
import com.agri.vision.Repo.productRepo;
import com.agri.vision.Service.AdminService;
import com.agri.vision.Service.CloudinaryService;
import com.agri.vision.Service.productService;
import com.cloudinary.Cloudinary;

@Controller
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1/auth")
public class AdminController {
    private final CloudinaryService cloudinaryService;

    public AdminController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }
    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private productService service;

    @Autowired
    private AddManageRepo Addrepo;

    @Autowired
    private productRepo repo;

    @Autowired
    private Cloudinary cloudinary;

    /////////////////////////////////////////////////////////////
    // Name : Sakshi Ladkat
    // day , Date :Wednsday 19 feb 2025
    // Function : Admin Login
    // Admin login credentials are username=Admin password=admin@123
    // if Admin Table is Empty it will create Admin with Above credentials
    ////////////////////////////////////////////////////////////
    @PostMapping("admin/login")
    public ResponseEntity<?> login(@RequestBody AdminRequest loginrequest) {
        Admin admin = adminRepo.findByUsername(loginrequest.getUsername());

        if (admin == null) {
            adminService.createadmin();
            return new ResponseEntity<>("Admin Created", HttpStatus.OK);
        }

        if (loginrequest.getPassword().equals(admin.getPassword())) {
            return new ResponseEntity<>("Login Successful", HttpStatus.OK);
        }

        return ResponseEntity.badRequest().body("Invalid username or password");
    }

    /////////////////////////////////////////////////////////////
    // Name : Sakshi Ladkat
    // day , Date :Tuesday 4 feb 2025
    // Function : product Api
    // add product , update product , delete product
    ////////////////////////////////////////////////////////////

    @PostMapping("/admin/addproduct")
    public ResponseEntity<?> addProduct(
            @RequestParam("productname") String productname,
            @RequestParam("productcompanyname") String productcompanyname,
            @RequestParam("productimage") MultipartFile productimage,
            @RequestParam("category") String category,
            @RequestParam("discount") int discount,
            @RequestParam("beforediscount") int beforediscount,
            @RequestParam("afterdiscount") int afterdiscount,
            @RequestParam("quantity") int quantity) {
        try {
            product newProduct = service.addProduct(
                    productname,
                    productcompanyname,
                    productimage,
                    category,
                    discount,
                    beforediscount,
                    afterdiscount,
                    quantity);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update product: " + e.getMessage());
        }
    }

    @PostMapping("/admin/updateproduct/{id}")
    public ResponseEntity<?> updateProductFields(
            @PathVariable Long id,
            @RequestParam(value = "productimage", required = false) MultipartFile productimage,
            @RequestParam Map<String, String> updates) {
        try {
            Map<String, Object> updateMap = new HashMap<>(updates);
            if (productimage != null) {
                updateMap.put("productimage", productimage);
            }

            product updatedProduct = service.updateProduct(id, updateMap);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update product: " + e.getMessage());
        }
    }

    @GetMapping("/admin/add")
    public List<AddManage> getAllAdd() {
        return Addrepo.findAll();
    }

    @PostMapping("/admin/Deleteadd/{id}")
    public ResponseEntity<String> Deleteadd(@PathVariable Long id) {
        try {
            Addrepo.deleteById(id);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete");
        }
    }

    @PostMapping("/admin/Updateadd/{id}")
    public ResponseEntity<String> updateImage(@PathVariable Long id,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            return Addrepo.findById(id).map(existingRecord -> {
                if (image != null && !image.isEmpty()) {
                    System.out.println("Image Content Type: " + image.getContentType());

                    // Validate file type
                    if (!image.getContentType().startsWith("image/")) {
                        return ResponseEntity.badRequest().body("Invalid image file format.");
                    }

                    // Upload new image
                    String newImageUrl = cloudinaryService.uploadFile(image);
                    if (newImageUrl == null || newImageUrl.isEmpty()) {
                        return ResponseEntity.badRequest().body("Image upload failed.");
                    }

                    // Update image URL
                    existingRecord.setAddurl(newImageUrl);
                    Addrepo.save(existingRecord);

                    return ResponseEntity.ok("Image updated successfully.");
                } else {
                    return ResponseEntity.badRequest().body("No image provided.");
                }
            }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update image.");
        }
    }
    @PostMapping("/admin/addImage")
    public ResponseEntity<String> addImage(@RequestPart("image") MultipartFile image) {
        try {
            if (image == null || image.isEmpty()) {
                return ResponseEntity.badRequest().body("No image provided.");
            }
    
            if (!image.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().body("Invalid image file format.");
            }
    
            // Upload image to Cloudinary
            String imageUrl = cloudinaryService.uploadFile(image);
            if (imageUrl == null || imageUrl.isEmpty()) {
                return ResponseEntity.badRequest().body("Image upload failed.");
            }
    
            // Create a new record
            AddManage newImageRecord = new AddManage();
            newImageRecord.setAddurl(imageUrl);  // Assuming 'addurl' stores the image URL
    
            // Save to database
            Addrepo.save(newImageRecord);
    
            return ResponseEntity.ok("Image added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add image.");
        }
    }
    
}