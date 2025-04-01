package com.agri.vision.DTO;

import com.agri.vision.Model.product;

public class WishlistProductDTO {
private Long id;
private Long pId;
private String productname;
private String productcompanyname;
private String productimage;
private Integer beforediscount;
private Integer afterdiscount;
private Integer discount;

public WishlistProductDTO(Long wishlistId,product p) {
    this.id = wishlistId;
    this.pId = p.getId();
    this.productname = p.getProductname();
    this.productcompanyname = p.getProductcompanyname();
    this.productimage = p.getProductimage();
    this.beforediscount = p.getBeforediscount();
    this.afterdiscount = p.getAfterdiscount();
    this.discount = p.getDiscount();
}
// Getters and setters
public Long getId() {
    return id;
}
public void setId(Long id) {
    this.id = id;
}
public Long getpId() {
    return pId;
}
public void setpId(Long pId) {
    this.pId = pId;
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
public Integer getDiscount() {
    return discount;
}
public void setDiscount(Integer discount) {
    this.discount = discount;
}

}
