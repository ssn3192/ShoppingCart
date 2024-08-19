package com.example.demo.model.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


public class CatalogSizeResponse extends ApiResponse {
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private Integer count;

    public CatalogSizeResponse(boolean success,Integer count) {
        super(success);
        this.count = count;
    }

    public CatalogSizeResponse(boolean success) {
        super(success);
    }

    public Integer getCount() {
        return count;
    }
}
