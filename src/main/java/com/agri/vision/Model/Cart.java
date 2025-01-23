package com.agri.vision.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productname;
    private String username;
    private String productcompanyname;
    private String productimage;
    private int discount;
    private int beforediscount;
    private int afterdiscount;

    // Getters and setters
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
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getBeforediscount() {
        return beforediscount;
    }

    public void setBeforediscount(int beforediscount) {
        this.beforediscount = beforediscount;
    }

    public int getAfterdiscount() {
        return afterdiscount;
    }

    public void setAfterdiscount(int afterdiscount) {
        this.afterdiscount = afterdiscount;
    }
}

