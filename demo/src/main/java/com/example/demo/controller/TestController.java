package com.example.demo.controller;

import com.example.demo.dao.ProductDao;
import com.example.demo.model.Product;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.model.response.CartResponse;
import com.example.demo.model.response.CatalogSizeResponse;
import com.example.demo.model.response.ProductByIdResponse;
import com.example.demo.service.ProductService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/demo")
public class TestController {

    private final ProductService productService;

    @Autowired
    public TestController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("size")
    public ResponseEntity<CatalogSizeResponse> getCatalogSize(){
        CatalogSizeResponse response = new CatalogSizeResponse();
        int size = productService.getCatalogSize();
        response.count = size;
        response.success = size > 0;
        if(!response.success){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("all")
    public ResponseEntity<List<Product>> getAll(){
        List<Product> products = productService.getAll();
        return  ResponseEntity.ok(products);
    }


    @GetMapping("/healthCheck/{id}")
    public ResponseEntity<ApiResponse> healthCheck(@PathVariable String id) {
        ProductByIdResponse res = new ProductByIdResponse();
        var product = productService.getById(id);
        product.ifPresent(value -> res.products.add(value));
        res.success = product.isPresent();
        if(!res.success) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping("cart")
    public ResponseEntity<CartResponse> getCart(){
        try{

            CartResponse response = productService.getCart();
            response.success = true;
            return ResponseEntity.ok(response);
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new CartResponse());
        }
    }

    @GetMapping("cart/item/{id}")
    public ResponseEntity<ProductByIdResponse> itemFromCart(@PathVariable String id){
        try{
            ProductByIdResponse response = new ProductByIdResponse();
            Product product = productService.itemFromCart(id);
            if(product != null){
                response.products.add(product);
                response.success = true;
                return ResponseEntity.ok(response);
            }
            response.success = false;
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new ProductByIdResponse());
        }
    }

    @DeleteMapping("cart/item/{id}")
    public  ResponseEntity<ApiResponse> deleteItem(@PathVariable String id){
        ApiResponse response = new ApiResponse();
        try{
            if(productService.getCartDetails().containsKey(id)){
                productService.getCartDetails().remove(id);
                //re-add back item to the catalog with quantity as 1
             //   productService.reAddItemBacktoCatalog(id);
                response.success = true;
                return ResponseEntity.ok(response);
            }
            response.success = false;
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(response);
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new ApiResponse(){});
        }
    }


    @PostMapping("cart/checkout")
    public  ResponseEntity<ApiResponse> checkout() {
        try{
            if(productService.getCartDetails().isEmpty()){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(new ApiResponse(){});
            }
            return null;
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(){});
        }
    }

    @PostMapping("cart/item/{id}")
    public  ResponseEntity<ApiResponse> addItemtoCart(@PathVariable String id){
        try{
            ApiResponse res = new ApiResponse();
            Set<String> response  = productService.addItem(id);
            if(!response.isEmpty() && response.contains(id)){
                res.success = true;
                return ResponseEntity.ok(res);
            }
            else{
                res.success = false;
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(res);
            }

        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new ApiResponse(){});
        }
    }

}
