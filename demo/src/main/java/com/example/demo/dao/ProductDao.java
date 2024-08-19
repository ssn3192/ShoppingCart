package com.example.demo.dao;

import com.example.demo.model.Product;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.Objects.requireNonNull;

@Repository
public class ProductDao {
    private final DataConfig dataConfig;

    //autowire
    ProductDao(DataConfig dataConfig) {
        this.dataConfig = requireNonNull(dataConfig);
    }

    public List<Product> getAll(){
       Collection<Product> products = dataConfig.getCatalog().values();
       return products.stream().toList();
    }

    public Optional<Product> findById(String id) {
       var product =  dataConfig.getCatalog().getOrDefault(id, null);
       if(product == null) {
           return Optional.empty();
       }

       return Optional.of(product);
    }

    public void createCart(String id, Product product){
        dataConfig.getCart()
                .putIfAbsent(id,
                        new Product(product.name(), product.price(), product.id(), product.quantity()));
        //dataConfig.createCart(id, product);
    }
    public Map<String, Product> getCart(){
        return dataConfig.getCart();
    }

    public boolean updateCatalog(String id, boolean isAddingItemToCatalog){
        Map<String, Product> copyCatalog = new HashMap<>(dataConfig.getCatalog());
        Product product  = copyCatalog.getOrDefault(id, null);
        if(product != null){
            int newQuantity = isAddingItemToCatalog ? 1 : 0;
            Product updatedProduct = new Product(product.name(), product.price(), product.id(), newQuantity);
            copyCatalog.put(product.id(), updatedProduct);
            dataConfig.commit(copyCatalog);
            return true;
        }
        return false;
    }
}
