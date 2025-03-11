package com.agri.vision.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agri.vision.DTO.AdminRequest;
import com.agri.vision.Model.Admin;
import com.agri.vision.Model.product;
import com.agri.vision.Repo.AdminRepo;
import com.agri.vision.Service.AdminService;
import com.agri.vision.Service.productService;

@RestController
@CrossOrigin(origins = "/**")
@RequestMapping("/api/v1/auth")
public class AdminLoginController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminRepo adminRepo;

    @Autowired
    private productService service;

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
    public ResponseEntity<product> addproduct(@RequestBody product product) {
        return new ResponseEntity<>(service.addproduct(product), HttpStatus.OK);

    }

    @PutMapping("/admin/updateproduct/{id}")
    public ResponseEntity<product> updateProduct(@PathVariable("id") int id, @RequestBody product product) {
        return new ResponseEntity<product>(service.updateproduct(id, product), HttpStatus.OK);
    }

    @DeleteMapping("/admin/deleteproduct/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") int id) {
        product product = service.getProductById(id);
        if (product != null) {
            service.deleteProduct(id);
            return new ResponseEntity<>("Deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Product Not Found", HttpStatus.NOT_FOUND);
        }
    }

}
