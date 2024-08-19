package com.example.demo.dao;

import com.example.demo.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class DataConfig {
    Logger LOG = LoggerFactory.getLogger(DataConfig.class);

    private static final String DATA_FILE_NAME = "/static/product.json";

    @Autowired
    private ResourceLoader resourceLoader;

    private Map<String, Product> catalog = new HashMap<>();
    private Map<String, Product> cart = new HashMap<>();

    public Map<String, Product> getCatalog() {
        return catalog;
    }

    public void createCart(String id, Product product){
        cart.putIfAbsent(id, new Product(product.name(), product.price(), product.id(), product.quantity()));
    }

    public Map<String, Product> getCart(){
        return cart;
    }

    public void commit(Map<String, Product> newCatalog){
        this.catalog = newCatalog;
    }

    @PostConstruct
    public void init() {
        LOG.info("Initialization of catalog data");
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Resource resource = resourceLoader.getResource("classpath:" + DATA_FILE_NAME);
            List<Product> products = objectMapper.readValue(
                    new File(resource.getFile().getPath()),
                    new TypeReference<>() {}
            );

            catalog = products.stream().collect(Collectors.toMap(Product::id, Function.identity()));


            //LOG.info(getCatalog().toString());

        }catch (IOException e) {
            LOG.error("Error while deserializing data", e);
        }catch (Exception e) {
            LOG.error("Error while reading data", e);
        }
    }


}
