package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.response.*;
import com.example.demo.service.ProductService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        try{
            int size = productService.getCatalogSize();
            boolean isSuccess = size > 0;
            if(!isSuccess){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new CatalogSizeResponse(isSuccess));
            }
            return ResponseEntity.ok(new CatalogSizeResponse(isSuccess, size));
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CatalogSizeResponse(false));
        }
    }

    @GetMapping("all")
    public ResponseEntity<List<Product>> getAll(){
        List<Product> products = productService.getAll();
        return  ResponseEntity.ok(products);
    }


    @GetMapping("/catalog/{id}")
    public ResponseEntity<ProductByIdResponse> getItem(@PathVariable String id) {
        try{
            var mayBeProduct = productService.getById(id);
            if(mayBeProduct.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ProductByIdResponse(false));
            }
            var product = mayBeProduct.get();
            return ResponseEntity.ok(new ProductByIdResponse(true, List.of(product)));
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ProductByIdResponse(false));
        }
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
                    .body(new CartResponse(false));
        }
    }

    @GetMapping("cart/item/{id}")
    public ResponseEntity<ProductByIdResponse> itemFromCart(@PathVariable String id){
        try{
            Product product = productService.itemFromCart(id);
            if(product == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ProductByIdResponse(false));
            }
            return ResponseEntity.ok(new ProductByIdResponse(true, List.of(product)));
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ProductByIdResponse(false));
        }
    }

    @DeleteMapping("cart/item/{id}")
    public  ResponseEntity<ApiResponse> deleteItemFromCart(@PathVariable String id){
        try{
            boolean isSuccess = productService.deleteItemFromCart(id);
            if(!isSuccess){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false));
            }
            return ResponseEntity.ok(new ApiResponse(true));
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new ApiResponse(false));
        }
    }


    @PostMapping("cart/checkout")
    public  ResponseEntity<CartResponse> checkout() {
        try{
            CartResponse response = productService.checkoutCart();
            if(!response.success){
                return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                        .body(response);
            }
            return ResponseEntity.ok(response);
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CartResponse(false));
        }
    }

    @PostMapping("cart/item/{id}")
    public  ResponseEntity<ApiResponse> addItemtoCart(@PathVariable String id){
        try{
            Set<String> cartItemIds = productService.addItem(id);
            if(cartItemIds.isEmpty() || !cartItemIds.contains(id)){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse(false));
            }
            return ResponseEntity.ok(new ApiResponse(true));
        }
        catch (NotImplementedException ex){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                    .body(new ApiResponse(false));
        }
        catch (IllegalArgumentException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false));
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false));
        }
    }

}
