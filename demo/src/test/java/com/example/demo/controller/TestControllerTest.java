package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.model.response.CartResponse;
import com.example.demo.service.ProductService;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestController.class)
class TestControllerTest {

    @MockBean
    private ProductService productService;

    @InjectMocks
    private TestController testController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetCatalogSize_whenSizeIsGreaterThanZero_thenReturnOk() throws Exception {
        when(productService.getCatalogSize()).thenReturn(10);

        mockMvc.perform(get("/demo/size"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(10));
    }

    @Test
    void testGetCatalogSize_whenSizeIsZero_thenReturnNotImplemented() throws Exception {
        when(productService.getCatalogSize()).thenReturn(0);

        mockMvc.perform(get("/demo/size"))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetItem_whenProductExists_thenReturnOk() throws Exception {
        Product product = new Product("Test Product", 10, "001", 1);
        when(productService.getById("001")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/demo/catalog/001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.products[0].id").value("001"));
    }

    @Test
    void testGetItem_whenProductDoesNotExist_thenReturnNotFound() throws Exception {
        when(productService.getById("001")).thenReturn(Optional.empty());

        mockMvc.perform(get("/demo/catalog/001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testGetCart_thenReturnOk() throws Exception {
        CartResponse cartResponse = new CartResponse(true);
        when(productService.getCart()).thenReturn(cartResponse);

        mockMvc.perform(get("/demo/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testItemFromCart_whenProductExistsInCart_thenReturnOk() throws Exception {
        Product product = new Product("Test Product", 10, "001", 1);
        when(productService.itemFromCart("001")).thenReturn(product);

        mockMvc.perform(get("/demo/cart/item/001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.products[0].id").value("001"));
    }

    @Test
    void testItemFromCart_whenProductDoesNotExistInCart_thenReturnNotFound() throws Exception {
        when(productService.itemFromCart("001")).thenReturn(null);

        mockMvc.perform(get("/demo/cart/item/001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testDeleteItemFromCart_whenProductExistsInCart_thenReturnOk() throws Exception {
        when(productService.deleteItemFromCart("001")).thenReturn(true);

        mockMvc.perform(delete("/demo/cart/item/001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testDeleteItemFromCart_whenProductDoesNotExistInCart_thenReturnNotFound() throws Exception {
        when(productService.deleteItemFromCart("001")).thenReturn(false);

        mockMvc.perform(delete("/demo/cart/item/001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testCheckout_whenCartIsNotEmpty_thenReturnOk() throws Exception {
        CartResponse cartResponse = new CartResponse(true);
        when(productService.checkoutCart()).thenReturn(cartResponse);

        mockMvc.perform(post("/demo/cart/checkout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testCheckout_whenCartIsEmpty_thenReturnNotImplemented() throws Exception {
        CartResponse cartResponse = new CartResponse(false);
        when(productService.checkoutCart()).thenReturn(cartResponse);

        mockMvc.perform(post("/demo/cart/checkout"))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testAddItemToCart_whenItemIsAdded_thenReturnOk() throws Exception {
        when(productService.addItem("001")).thenReturn(Set.of("001"));

        mockMvc.perform(post("/demo/cart/item/001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testAddItemToCart_whenItemIsNotAdded_thenReturnNotFound() throws Exception {
        when(productService.addItem("001")).thenReturn(Collections.emptySet());

        mockMvc.perform(post("/demo/cart/item/001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testAddItemToCart_whenItemIsAlreadyAdded_thenReturnNotImplemented() throws Exception {
        when(productService.addItem("001")).thenThrow(NotImplementedException.class);

        mockMvc.perform(post("/demo/cart/item/001"))
                .andExpect(status().isNotImplemented())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void testAddItemToCart_whenItemDoesNotExist_thenReturnNotFound() throws Exception {
        when(productService.addItem("001")).thenThrow(IllegalArgumentException.class);

        mockMvc.perform(post("/demo/cart/item/001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

}