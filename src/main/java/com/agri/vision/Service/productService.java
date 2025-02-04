package com.agri.vision.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agri.vision.Model.product;
import com.agri.vision.Repo.productRepo;

import jakarta.transaction.Transactional;

@Service
public class productService
{
    @Autowired
   private productRepo repo;
	
	public List<product> getAllProduct()
	{
		return repo.findAll();
	}
	
	public product getProductById(int id)
	{
		return repo.findById(id).get();
	}
	public List<product> getProductByCategory(String category)
	{
		return repo.findByCategory(category);
	}

	
	@Transactional
	public product addproduct(product product)
	{
		return repo.save(product);
	}
    
	
	@Transactional
    public product updateproduct(int id, product product) {
		
        product existingProduct =repo.findById(id).get();
        
        existingProduct.setProductname(product.getProductname());
        existingProduct.setAfterdiscount(product.getAfterdiscount());
        existingProduct.setBeforediscount(product.getBeforediscount());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setProductimage(product.getProductimage());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setProductcompanyname(product.getProductcompanyname());
        
        return repo.save(existingProduct);
    }
	

	@Transactional
	public void deleteProduct(int id) 
	{
	 repo.deleteById((long) id);	
	}
}