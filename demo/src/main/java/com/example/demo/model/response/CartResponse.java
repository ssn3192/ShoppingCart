package com.example.demo.model.response;

import com.example.demo.model.Product;

import java.util.ArrayList;

public class CartResponse  extends ApiResponse{

    public ArrayList<Product> products = new ArrayList<>();
    public int totalCost;
}
