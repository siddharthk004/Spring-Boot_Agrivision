package com.agri.vision.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agri.vision.Model.product;
import com.agri.vision.Repo.productRepo;

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
}