package com.example.demo.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;


public class CatalogSizeResponse extends ApiResponse {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public Integer count;
}
