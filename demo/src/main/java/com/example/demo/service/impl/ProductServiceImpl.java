package com.example.demo.service.impl;

import com.example.demo.dao.DataConfig;
import com.example.demo.dao.ProductDao;
import com.example.demo.model.Product;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.model.response.CartResponse;
import com.example.demo.service.ProductService;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.util.*;

@Component
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;
    Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    public ProductServiceImpl(ProductDao productDao) {
        this.productDao = productDao;
    }


    @Override
    public Optional<Product> getById(String id) {
        if(StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Product Id can not be blank");
        }
        return productDao.findById(id);
    }

    @Override
    public List<Product> getAll() {
        return productDao.getAll();
    }

    @Override
    public int getCatalogSize() {
        List<Product> products = getAll();
        List<Product> availableProducts = products.stream().filter(val -> val.quantity() > 0).toList();
        return availableProducts.size();
    }

    @Override
    public CartResponse getCart() {
        Map<String, Product> carts = productDao.getCart();
        int total = 0;
        final var products = new ArrayList<Product>();
        if(!carts.isEmpty()){
            for(Product product : carts.values()){
                total = total + product.price();
                products.add(product);
            }
        }

        return new CartResponse(true, products, total);
    }

    @Override
    public Set<String> addItem(String id) {
        if(productDao.getCart().containsKey(id)){
            throw new NotImplementedException("Product already added");
        }
        Set<String> cart = new HashSet<>();
        Optional<Product> mayBeProduct = getById(id);

        if(mayBeProduct.isEmpty()){
            throw new IllegalArgumentException("Product not available");
        }
        Product product = mayBeProduct.get();
        //case when any item is purchased and checkout successfully and if user comes again to buy product then it should
        //throw exception as item quantity would be 0 which is not available
        if(product.quantity() == 0){
                throw new IllegalArgumentException("Product already purchased");
        }
        productDao.createCart(id, product);
        boolean isItemAdded = productDao.updateCatalog(id, false);
        if(isItemAdded){
            cart = productDao.getCart().keySet();
        }
        return cart;
    }


    @Override
    public Product itemFromCart(String id) {
        Map<String, Product> item = productDao.getCart();
        if(item.containsKey(id)){
            return item.get(id);
        }
        return null;
    }

    @Override
    public Map<String,Product> getCartDetails() {
        return productDao.getCart();
    }

    @Override
    public void reAddItemBacktoCatalog(String id) {
       productDao.updateCatalog(id, true);
    }

    @Override
    public boolean deleteItemFromCart(String id) {
        if(!getCartDetails().containsKey(id)){
            return false;
        }
        getCartDetails().remove(id);
        //when removed item from cart, make it available back in catalog by updating quantity to 1
        reAddItemBacktoCatalog(id);
        return true;
    }

    @Override
    public CartResponse checkoutCart() {
        if(getCartDetails().isEmpty()){
            return new CartResponse(false);
        }
        CartResponse response = getCart();
        getCartDetails().clear();
        return response;
    }

}
