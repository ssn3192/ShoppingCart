package com.example.demo.model.response;

import com.example.demo.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.ArrayList;

@JsonInclude(Include.NON_NULL)
public class ProductByIdResponse extends ApiResponse{

    public ArrayList<Product> products = new ArrayList<>();
}
