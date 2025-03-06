package com.agri.vision.Service;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.agri.vision.Model.product;
import com.agri.vision.Repo.productRepo;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.transaction.Transactional;

@Service
public class productService {
	@Autowired
	private productRepo repo;

	@Autowired
	private Cloudinary cloudinary;

	public List<product> getAllProduct() {
		return repo.findAll();
	}

	public product getProductById(int id) {
		return repo.findById(id).get();
	}

	public List<product> getProductByCategory(String category) {
		return repo.findByCategory(category);
	}

	@Transactional
	public product addProduct(
			String productname,
			String productcompanyname,
			MultipartFile productimage,
			String category,
			Integer discount,
			Integer beforediscount,
			Integer afterdiscount,
			Integer quantity) throws IOException {
		// Upload image to Cloudinary
		Map<?, ?> uploadResult = cloudinary.uploader().upload(productimage.getBytes(), ObjectUtils.emptyMap());
		String imageUrl = uploadResult.get("secure_url").toString();

		// Create the product
		product newProduct = new product();
		newProduct.setProductname(productname);
		newProduct.setProductcompanyname(productcompanyname);
		newProduct.setProductimage(imageUrl);
		newProduct.setCategory(category);
		newProduct.setDiscount(discount);
		;
		newProduct.setAfterdiscount(afterdiscount);
		newProduct.setBeforediscount(beforediscount);
		newProduct.setQuantity(quantity);
		if (newProduct.getQuantity() == 0) {
			newProduct.setStatus("Out Of Stock");
		} else if (newProduct.getQuantity() > 0) {
			newProduct.setStatus("Available");
		} else {
			throw new IllegalArgumentException("Quantity cannot be negative");
		}

		product savedProduct = repo.save(newProduct);

		return savedProduct;
	}

	public product updateProduct(Long id, Map<String, Object> updates) {

		product existingProduct = repo.findById(id)
				.orElseThrow(() -> new RuntimeException("Product not found"));
		updates.forEach((key, value) -> {
			try {
				switch (key) {
					case "productname":
						existingProduct.setProductname((String) value);
						break;
					case "productcompanyname":
						existingProduct.setProductcompanyname((String) value);
						break;
					case "productimage":
						if (value instanceof MultipartFile) {
							MultipartFile imageFile = (MultipartFile) value;
							try {
								// Upload the image to Cloudinary
								Map<?, ?> uploadResult = cloudinary.uploader().upload(imageFile.getBytes(),
										ObjectUtils.emptyMap());
								String imageUrl = uploadResult.get("secure_url").toString();

								// Update the productimage field with the new URL
								existingProduct.setProductimage(imageUrl);
							} catch (IOException e) {
								throw new RuntimeException("Failed to upload image to Cloudinary", e);
							}
						} else {
							throw new IllegalArgumentException(
									"Invalid type for productimage. Expected MultipartFile.");
						}
						break;
					case "category":
						existingProduct.setCategory((String) value);
						break;
					case "discount":
						existingProduct.setDiscount(Integer.parseInt((String) value));
						break;
					case "beforediscount":
						existingProduct.setBeforediscount(Integer.parseInt((String) value));
						break;
					case "afterdiscount":
						existingProduct.setAfterdiscount(Integer.parseInt((String) value));
						break;
					case "quantity":
						existingProduct.setQuantity(Integer.parseInt((String) value));
						if (existingProduct.getQuantity() == 0) {
							existingProduct.setStatus("Out Of Stock");
						} else if (existingProduct.getQuantity() > 0) {
							existingProduct.setStatus("Available");
						} else {
							throw new IllegalArgumentException("Quantity cannot be negative");
						}
						break;
					default:
						throw new IllegalArgumentException("Invalid field: " + key);
				}
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid value for field: " + key + ". Expected a number.");
			}
		});
		return repo.save(existingProduct);
	}

}