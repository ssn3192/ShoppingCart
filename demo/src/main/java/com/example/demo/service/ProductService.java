package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.model.response.CartResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public interface ProductService {

    /**
     *
     * @param id
     * @return
     */
    Optional<Product> getById(String id);

    List<Product> getAll();

    int getCatalogSize();

    CartResponse getCart();

    Set<String> addItem(String id);

    Product itemFromCart(String id);

    Map<String,Product> getCartDetails();

    void reAddItemBacktoCatalog(String id);
}
