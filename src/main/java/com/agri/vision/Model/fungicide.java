package com.agri.vision.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class fungicide {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productname;
    private String productcompanyname;
    private String productimage;
    private int discount;
    private int beforediscount;
    private int afterdiscount;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getProductName() {
        return productname;
    }
    public void setProductName(String productName) {
        productname = productName;
    }
    public String getProductCompanyName() {
        return productcompanyname;
    }
    public void setProductCompanyName(String productCompanyName) {
        productcompanyname = productCompanyName;
    }
    public String getProductImage() {
        return productimage;
    }
    public void setProductImage(String productImage) {
        productimage = productImage;
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
