package com.example.demo.service.impl;

import com.example.demo.dao.ProductDao;
import com.example.demo.model.Product;
import com.example.demo.model.response.CartResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void testGetById_whenIdIsBlank_thenThrowException() {
        assertThrows(IllegalArgumentException.class, () -> productService.getById(""));
    }

    @Test
    void testGetById_whenProductExists_thenReturnProduct() {
        String id = "001";
        Product product = new Product("Test Product", 10 , id, 1);

        when(productDao.findById(id)).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getById(id);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    void testGetAll_whenProductsExist_thenReturnAllProducts() {
        List<Product> products = Arrays.asList(
                new Product("Product 1", 10 , "001", 1),
                new Product("Product 2", 20, "002", 1)
        );

        when(productDao.getAll()).thenReturn(products);

        List<Product> result = productService.getAll();

        assertEquals(products, result);
    }

    @Test
    void testGetCatalogSize_whenProductsExist_thenReturnAvailableProductCount() {
        List<Product> products = Arrays.asList(
                new Product("Product 1", 10 , "001", 1),
                new Product("Product 2", 20, "002", 0)
        );

        when(productDao.getAll()).thenReturn(products);

        int catalogSize = productService.getCatalogSize();

        assertEquals(1, catalogSize);
    }

    @Test
    void testGetCart_whenCartIsNotEmpty_thenReturnCartResponse() {
        Map<String, Product> cartItems = new HashMap<>();
        cartItems.put("001", new Product("Product 1", 10 , "001", 1));

        when(productDao.getCart()).thenReturn(cartItems);

        CartResponse result = productService.getCart();

        assertEquals(1, result.getProducts().size());
        assertEquals(10, result.getTotalCost());
    }

    @Test
    void testAddItem_whenProductAlreadyInCart_thenThrowException() {
        String id = "1";
        when(productDao.getCart()).thenReturn(Collections.singletonMap(id, new Product("Product 1", 10 , "001", 1)));

        assertThrows(NotImplementedException.class, () -> productService.addItem(id));
    }

    @Test
    void testAddItem_whenProductNotFound_thenThrowIllegalArgumentException() {
        String id = "1";
        when(productDao.getCart()).thenReturn(new HashMap<>());
        when(productDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> productService.addItem(id));
    }

    @Test
    void testAddItem_whenProductQuantityIsZero_thenThrowIllegalArgumentException() {
        String id = "001";
        Product product = new Product("Product 1", 10 , "001", 0);

        when(productDao.getCart()).thenReturn(new HashMap<>());
        when(productDao.findById(id)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> productService.addItem(id));
    }

    @Test
    void testAddItem_whenProductIsAddedSuccessfully_thenReturnUpdatedCart() {
        String id = "001";
        Product product = new Product("Product 1", 10 , "001", 1);
        Map<String, Product> cartMap = new HashMap<>();
        cartMap.put(id, product);

        when(productDao.getCart()).thenReturn(new HashMap<>(), cartMap);
        when(productDao.findById(id)).thenReturn(Optional.of(product));
        when(productDao.updateCatalog(id, false)).thenReturn(true);

        Set<String> result = productService.addItem(id);

        assertTrue(result.contains(id));
        verify(productDao).createCart(id, product);
        verify(productDao).updateCatalog(id, false);
    }

    @Test
    void testItemFromCart_whenProductInCart_thenReturnProduct() {
        String id = "001";
        Product product = new Product("Product 1", 10 , "001", 1);

        when(productDao.getCart()).thenReturn(Collections.singletonMap(id, product));

        Product result = productService.itemFromCart(id);

        assertEquals(product, result);
    }

    @Test
    void testItemFromCart_whenProductNotInCart_thenReturnNull() {
        when(productDao.getCart()).thenReturn(new HashMap<>());

        Product result = productService.itemFromCart("1");

        assertNull(result);
    }

    @Test
    void testGetCartDetails_whenCartIsNotEmpty_thenReturnCart() {
        Map<String, Product> cartItems = new HashMap<>();
        cartItems.put("001", new Product("Product 1", 10 , "001", 1));

        when(productDao.getCart()).thenReturn(cartItems);

        Map<String, Product> result = productService.getCartDetails();

        assertEquals(cartItems, result);
    }

    @Test
    void testReAddItemBackToCatalog_whenCalled_thenUpdateCatalog() {
        String id = "001";

        productService.reAddItemBacktoCatalog(id);

        verify(productDao).updateCatalog(id, true);
    }

    @Test
    void testDeleteItemFromCart_whenItemNotInCart_thenReturnFalse() {
        String id = "1";
        when(productDao.getCart()).thenReturn(new HashMap<>());

        boolean result = productService.deleteItemFromCart(id);

        assertFalse(result);
        verify(productDao, never()).updateCatalog(anyString(), anyBoolean());
    }

    @Test
    void testDeleteItemFromCart_whenItemInCart_thenReturnTrue() {
        String id = "1";
        Map<String, Product> cartItems = new HashMap<>();
        cartItems.put(id, new Product("Product 1", 10 , "001", 1));

        when(productDao.getCart()).thenReturn(cartItems);

        boolean result = productService.deleteItemFromCart(id);

        assertTrue(result);
        assertFalse(cartItems.containsKey(id));
        verify(productDao, times(1)).updateCatalog(id, true);
    }

    @Test
    void testCheckoutCart_whenCartIsEmpty_thenReturnEmptyCartResponse() {
        when(productDao.getCart()).thenReturn(new HashMap<>());

        CartResponse result = productService.checkoutCart();

        assertFalse(result.isSuccess());
        assertNull(result.getProducts());
        assertEquals(0, result.getTotalCost());
    }

    @Test
    void testCheckoutCart_whenCartIsNotEmpty_thenReturnCartResponse() {
        String id = "1";
        Map<String, Product> cartItems = new HashMap<>();
        Product product = new Product("Product 1", 10 , "001", 1);
        cartItems.put(id, product);

        when(productDao.getCart()).thenReturn(cartItems);

        CartResponse result = productService.checkoutCart();

        assertTrue(result.isSuccess());
        assertEquals(1, result.getProducts().size());
        assertEquals(10, result.getTotalCost());
        assertTrue(cartItems.isEmpty());
    }
}