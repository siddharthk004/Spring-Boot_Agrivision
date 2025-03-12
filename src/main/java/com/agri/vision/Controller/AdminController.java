package com.agri.vision.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.agri.vision.DTO.AdminRequest;
import com.agri.vision.Model.Admin;
import com.agri.vision.Model.product;
import com.agri.vision.Repo.AdminRepo;
import com.agri.vision.Repo.productRepo;
import com.agri.vision.Service.AdminService;
import com.agri.vision.Service.productService;
import com.cloudinary.Cloudinary;

@Controller
@RestController
@CrossOrigin(origins = "/**")
@RequestMapping("/api/v1/auth")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private productService service;

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

}