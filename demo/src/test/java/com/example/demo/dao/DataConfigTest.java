package com.example.demo.dao;

import com.example.demo.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataConfigTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private Resource resource;

    @InjectMocks
    private DataConfig dataConfig;

    @Test
    void testGetCatalog() {
        Map<String, Product> catalog = dataConfig.getCatalog();
        assertNotNull(catalog);
        assertTrue(catalog.isEmpty());
    }

    @Test
    void testCreateCart() {
        Product product = new Product("Test Product", 10 , "001", 1);
        dataConfig.createCart("001", product);

        Map<String, Product> cart = dataConfig.getCart();
        assertNotNull(cart);
        assertEquals(1, cart.size());
        assertEquals("Test Product", cart.get("001").name());
    }

    @Test
    void testGetCart() {
        Map<String, Product> cart = dataConfig.getCart();
        assertNotNull(cart);
        assertTrue(cart.isEmpty());
    }

    @Test
    void testCommit() {
        Product product = new Product("Test Product", 10 , "001", 1);
        Map<String, Product> newCatalog = Map.of("001", product);
        dataConfig.commit(newCatalog);

        Map<String, Product> catalog = dataConfig.getCatalog();
        assertNotNull(catalog);
        assertEquals(1, catalog.size());
        assertEquals("Test Product", catalog.get("001").name());
    }

    @Test
    void testInit() throws IOException {
        when(resourceLoader.getResource("classpath:/static/product.json")).thenReturn(resource);
        when(resource.getFile()).thenReturn(new File("src/test/resources/product-test.json"));

        dataConfig.init();

        Map<String, Product> catalog = dataConfig.getCatalog();
        assertNotNull(catalog);
        assertEquals(1, catalog.size());
        assertEquals("Test Product", catalog.get("001").name());
    }

    @Test
    void testInit_withIOException() throws IOException {
        when(resourceLoader.getResource("classpath:/static/product.json")).thenReturn(resource);
        when(resource.getFile()).thenThrow(new IOException("File not found"));

        dataConfig.init();

        Map<String, Product> catalog = dataConfig.getCatalog();
        assertNotNull(catalog);
        assertTrue(catalog.isEmpty());
    }

    @Test
    void testInit_withGenericException() throws IOException {
        when(resourceLoader.getResource("classpath:/static/product.json")).thenReturn(resource);
        when(resource.getFile()).thenThrow(new RuntimeException("Generic error"));

        dataConfig.init();

        Map<String, Product> catalog = dataConfig.getCatalog();
        assertNotNull(catalog);
        assertTrue(catalog.isEmpty());
    }
}