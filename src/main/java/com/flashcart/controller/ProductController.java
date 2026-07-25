package com.flashcart.controller;

//import com.flashcart.exception.ProductNotFoundException;
import com.flashcart.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.flashcart.model.Product;
import java.util.List;
@RestController
@RequestMapping("api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
@GetMapping
    public ResponseEntity<List<Product>> getAllProducts()
{
//   Product product=new Product();
//   product.setId(1L);
//   product.setName("iPhone 15 Pro");
//   product.setDescription("Latest iPhone with titanium design");
//   product.setPrice(79999.0);
//   product.setStock(10);
//   product.setImageUrl("https://images.flashcart.com/iphone15.jpg");
//   product.setFlashSaleActive(false);
//   product.setFlashSalePrice(49999.0);
//   return  product;
    return ResponseEntity.ok(productService.getAllProducts());
}

@GetMapping("/{id}")
public ResponseEntity<Product> getProductById(@PathVariable Long id) {
//    if (id == 1L) {
//        Product product = new Product();
//        product.setId(id);
//        product.setName("Product #" + id);
//        product.setPrice(9999.0);
//        product.setStock(10);
//        product.setFlashSaleActive(false);
//        return ResponseEntity.ok(product);
//    }
//    else {
//         throw new ProductNotFoundException(id);
//    }
    return ResponseEntity.ok(productService.getProductById(id));
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
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.createProduct(product));
    }
   @PostMapping("/{id}")
    public ResponseEntity<Product> updateProduct( @PathVariable Long id,@RequestBody Product product)
    {
        return ResponseEntity.ok(productService.updateProduct(id,product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id)
    {
        productService.deleteProduct(id);
        return  ResponseEntity.noContent().build();
    }

}

