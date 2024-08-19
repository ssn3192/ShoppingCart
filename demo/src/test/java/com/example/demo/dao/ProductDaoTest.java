package com.example.demo.dao;

import com.example.demo.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductDaoTest {
    @Mock
    private DataConfig dataConfig;

    @InjectMocks
    private ProductDao productDao;

    @Test
    void testGetAll_whenCatalogIsNotEmpty_thenReturnListOfProducts() {
        Map<String, Product> catalog = new HashMap<>();
        catalog.put("1", new Product("Product 1", 10, "1", 5));
        catalog.put("2", new Product("Product 2", 20, "2", 3));

        when(dataConfig.getCatalog()).thenReturn(catalog);

        List<Product> products = productDao.getAll();

        assertEquals(2, products.size());
        assertTrue(products.stream().anyMatch(p -> p.id().equals("1")));
        assertTrue(products.stream().anyMatch(p -> p.id().equals("2")));
    }

    @Test
    void testGetAll_whenCatalogIsEmpty_thenReturnEmptyList() {
        when(dataConfig.getCatalog()).thenReturn(new HashMap<>());

        List<Product> products = productDao.getAll();

        assertTrue(products.isEmpty());
    }

    @Test
    void testFindById_whenProductExists_thenReturnProduct() {
        String id = "001";
        Product product = new Product("Product 1", 10, id, 1);
        Map<String, Product> catalog = new HashMap<>();
        catalog.put(id, product);

        when(dataConfig.getCatalog()).thenReturn(catalog);

        Optional<Product> result = productDao.findById(id);

        assertTrue(result.isPresent());
        assertEquals(product, result.get());
    }

    @Test
    void testFindById_whenProductDoesNotExist_thenReturnEmptyOptional() {
        String id = "001";
        when(dataConfig.getCatalog()).thenReturn(new HashMap<>());

        Optional<Product> result = productDao.findById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void testCreateCart_whenProductIsAdded_thenProductShouldBeInCart() {
        String id = "001";
        Product product = new Product("Product 1", 10, id, 1);
        Map<String, Product> cart = new HashMap<>();

        when(dataConfig.getCart()).thenReturn(cart);

        productDao.createCart(id, product);

        assertTrue(cart.containsKey(id));
        assertEquals(product.name(), cart.get(id).name());
        assertEquals(product.price(), cart.get(id).price());
        assertEquals(product.id(), cart.get(id).id());
        assertEquals(product.quantity(), cart.get(id).quantity());
    }

    @Test
    void testGetCart_whenCartIsNotEmpty_thenReturnCart() {
        String id = "001";
        Product product = new Product("Product 1", 10, id, 1);
        Map<String, Product> cart = new HashMap<>();
        cart.put(id, product);

        when(dataConfig.getCart()).thenReturn(cart);

        Map<String, Product> result = productDao.getCart();

        assertEquals(cart, result);
        assertTrue(result.containsKey(id));
    }

    @Test
    void testGetCart_whenCartIsEmpty_thenReturnEmptyMap() {
        when(dataConfig.getCart()).thenReturn(new HashMap<>());

        Map<String, Product> result = productDao.getCart();

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdateCatalog_whenProductExistsAndIsAddingItem_thenReturnTrueAndUpdateCatalog() {
        String id = "001";
        Product product = new Product("Product 1", 10, id, 0);
        Map<String, Product> catalog = new HashMap<>();
        catalog.put(id, product);

        when(dataConfig.getCatalog()).thenReturn(catalog);

        boolean result = productDao.updateCatalog(id, true);

        assertTrue(result);
        ArgumentCaptor<Map<String, Product>> catalogCaptor = ArgumentCaptor.forClass(Map.class);
        verify(dataConfig).commit(catalogCaptor.capture());
        final var expectedCatalog = catalogCaptor.getValue();
        assertEquals(1, expectedCatalog.get(id).quantity());
    }

    @Test
    void testUpdateCatalog_whenProductExistsAndIsRemovingItem_thenReturnTrueAndUpdateCatalog() {
        String id = "001";
        Product product = new Product("Product 1", 10, id, 1);
        Map<String, Product> catalog = new HashMap<>();
        catalog.put(id, product);

        when(dataConfig.getCatalog()).thenReturn(catalog);

        boolean result = productDao.updateCatalog(id, false);

        assertTrue(result);
        ArgumentCaptor<Map<String, Product>> catalogCaptor = ArgumentCaptor.forClass(Map.class);
        verify(dataConfig).commit(catalogCaptor.capture());
        final var expectedCatalog = catalogCaptor.getValue();
        assertEquals(0, expectedCatalog.get(id).quantity());
    }

    @Test
    void testUpdateCatalog_whenProductDoesNotExist_thenReturnFalse() {
        String id = "001";
        when(dataConfig.getCatalog()).thenReturn(new HashMap<>());

        boolean result = productDao.updateCatalog(id, true);

        assertFalse(result);
        verify(dataConfig, never()).commit(anyMap());
    }
}