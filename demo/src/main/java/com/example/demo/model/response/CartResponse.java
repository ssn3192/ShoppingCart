package com.example.demo.model.response;

import com.example.demo.model.Product;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class CartResponse  extends ApiResponse{

    private List<Product> products;
    private int totalCost;

    public CartResponse(boolean success, List<Product> products, int totalcost) {
        super(success);
        this.products = products;
        this.totalCost = totalcost;
    }

    public CartResponse(boolean success) {
        super(success);
    }

    public List<Product> getProducts() {
        return products;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void addProduct(Product product) {
        if(CollectionUtils.isEmpty(products)) {
            products = new ArrayList<>();
        }
        products.add(product);
    }

}
