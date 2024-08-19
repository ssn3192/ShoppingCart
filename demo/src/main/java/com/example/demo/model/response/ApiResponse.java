package com.example.demo.model.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;

public class ApiResponse {
    public boolean success;

    public ApiResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
