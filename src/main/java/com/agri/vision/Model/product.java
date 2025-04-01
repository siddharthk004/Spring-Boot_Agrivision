package com.agri.vision.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productname;
    private String productcompanyname;
    private String productimage;
    private String category;
    private int discount;
    private int beforediscount;
    private int quantity;
    private int afterdiscount;
    private String Status;

    // Default constructor
    public product() {
    }

    // Parameterized constructor
    public product(String productname, String productcompanyname, String productimage, String category,
            Integer discount,
            Integer beforediscount, Integer afterdiscount, Integer quantity, String status) {
        this.productname = productname;
        this.productcompanyname = productcompanyname;
        this.productimage = productimage;
        this.category = category;
        this.discount = discount;
        this.beforediscount = beforediscount;
        this.afterdiscount = afterdiscount;
        this.quantity = quantity;
        this.Status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public String getProductcompanyname() {
        return productcompanyname;
    }

    public void setProductcompanyname(String productcompanyname) {
        this.productcompanyname = productcompanyname;
    }

    public String getProductimage() {
        return productimage;
    }

    public void setProductimage(String productimage) {
        this.productimage = productimage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Integer getBeforediscount() {
        return beforediscount;
    }

    public void setBeforediscount(Integer beforediscount) {
        this.beforediscount = beforediscount;
    }

    public Integer getAfterdiscount() {
        return afterdiscount;
    }

    public void setAfterdiscount(Integer afterdiscount) {
        this.afterdiscount = afterdiscount;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }
}
