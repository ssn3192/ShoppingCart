package com.example.demo.model.response;

import com.example.demo.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

@JsonInclude(Include.NON_NULL)
public class ProductByIdResponse extends ApiResponse{

    public List<Product> products;

    public ProductByIdResponse(boolean success, List<Product> products) {
        super(success);
        this.products = products;
    }

    public ProductByIdResponse(boolean success) {
        super(success);
    }

    public List<Product> getProducts() {
        return products;
    }
}
