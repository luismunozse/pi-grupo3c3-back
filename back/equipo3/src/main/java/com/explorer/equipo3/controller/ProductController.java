package com.explorer.equipo3.controller;

import com.explorer.equipo3.model.Category;
import com.explorer.equipo3.model.Detail;
import com.explorer.equipo3.model.Product;
import com.explorer.equipo3.service.ICategoryService;
import com.explorer.equipo3.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/random")
    public ResponseEntity<List<Product>> getRandomProducts(){return ResponseEntity.ok(productService.getRandomProducts());}

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id){
        Optional<Product> productSearch = productService.getProductById(id);
        if(productSearch.isPresent()){
            return ResponseEntity.ok(productSearch.orElseThrow());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> addProduct(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestBody Product product) {
        try {
            // Procesa la imagen si se proporciona
            if (file != null) {
                // Maneja el archivo
            }

            // Continúa con el procesamiento del producto
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(product));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product){
        Optional<Product> productOptional = productService.updateProduct(id, product);
        if(productOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){
        Optional productOptional = productService.getProductById(id);
        if(productOptional.isPresent()){
            productService.deleteProductById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/add-details")
    public ResponseEntity<Product> addDetailsToProduct(
            @PathVariable Long id,
            @RequestBody Set<Long> detailIds) {
        System.out.println("Received detailIds: " + detailIds);
        Optional<Product> productOptional = productService.getProductById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();

            // Aquí debes convertir los IDs en objetos Detail y agregarlos al producto
            for (Long detailId : detailIds) {
                Detail detail = new Detail();
                detail.setId(detailId);
                product.getDetails().add(detail);
            }

            Product updatedProduct = productService.saveProduct(product);
            return ResponseEntity.ok(updatedProduct);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/byCategories")
    public List<Product> getProductByCategory_id(@RequestParam List<Long> category_id) {

        return productService.getProductByCategory_id(category_id);
    }


}
