package com.flashcart.controller;

import com.flashcart.exception.ProductNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.flashcart.model.Product;
@RestController
@RequestMapping("api/products")
public class ProductController {
@GetMapping
    public Product getProduct()
{
   Product product=new Product();
   product.setId(1L);
   product.setName("iPhone 15 Pro");
   product.setDescription("Latest iPhone with titanium design");
   product.setPrice(79999.0);
   product.setStock(10);
   product.setImageUrl("https://images.flashcart.com/iphone15.jpg");
   product.setFlashSaleActive(false);
   product.setFlashSalePrice(49999.0);
   return  product;
}

@GetMapping("/{id}")
public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    if (id == 1L) {
        Product product = new Product();
        product.setId(id);
        product.setName("Product #" + id);
        product.setPrice(9999.0);
        product.setStock(10);
        product.setFlashSaleActive(false);
        return ResponseEntity.ok(product);
    }
    else {
         throw new ProductNotFoundException(id);
    }

}

    @GetMapping("/search")
    public String searchProducts(@RequestParam String keyword, @RequestParam(required = false, defaultValue = "price")
    String sortBy)
    {
        return "Searching for: " + keyword +
                " | Sorted by: " + sortBy;

    }

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product) {
        product.setId(System.currentTimeMillis());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(product);
    }

}

