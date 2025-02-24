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

    // Default constructor
    public product() {
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Constructor
    public product(Long id, String productname, String productcompanyname, String productimage, String category,
            int discount, int beforediscount, int afterdiscount, int quantity) {
        this.id = id;
        this.productname = productname;
        this.productcompanyname = productcompanyname;
        this.productimage = productimage;
        this.category = category;
        this.discount = discount;
        this.beforediscount = beforediscount;
        this.afterdiscount = afterdiscount;
        this.quantity = quantity;
    }

    // to-string
    @Override
    public String toString() {
        return "product [id=" + id + ", productname=" + productname + ", productcompanyname=" + productcompanyname
                + ", productimage=" + productimage + ", category=" + category + ", discount=" + discount
                + ", beforediscount=" + beforediscount + ", afterdiscount=" + afterdiscount + ", quantity=" + quantity
                + ", getId()=" + getId() + ", getProductname()=" + getProductname()
                + ", getProductcompanyname()=" + getProductcompanyname() + ", getProductimage()=" + getProductimage()
                + ", getCategory()=" + getCategory() + ", getClass()=" + getClass() + ", getDiscount()=" + getDiscount()
                + ", getBeforediscount()=" + getBeforediscount() + ", getAfterdiscount()=" + getAfterdiscount()
                + ", getQuantity()=" + getQuantity() + ", hashCode()="
                + hashCode() + ", toString()=" + super.toString() + "]";
    }

}