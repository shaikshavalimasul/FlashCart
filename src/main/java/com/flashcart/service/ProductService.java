package com.flashcart.service;

import com.flashcart.exception.ProductNotFoundException;
import com.flashcart.model.Product;
import com.flashcart.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product)
    {
        return productRepository.save(product);
    }
    public List<Product> getAllProducts()
    {
        return productRepository.findAll();
    }

    public Product getProductById(Long id)
    {
        return productRepository.findById(id).orElseThrow(()->new ProductNotFoundException(id));
    }
    public  void deleteProduct(Long id)
    {
        getProductById(id);
        productRepository.deleteById(id);
    }
    public  Product updateProduct(Long id,Product updatedProduct)
    {
        Product existing=getProductById(id);
        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setStock(updatedProduct.getStock());
        existing.setImageUrl(updatedProduct.getImageUrl());
        existing.setFlashSaleActive(updatedProduct.isFlashSaleActive());
        existing.setFlashSalePrice(updatedProduct.getFlashSalePrice());
        return productRepository.save(existing);
    }
}
