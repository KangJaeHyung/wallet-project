package com.wallet.base;

import lombok.Data;

@Data
public class ResponseDto<T> {

    private String message;
    private T data;
    private int statusCode;
    private String errorMessage;
    private String errorCode;

    public static <T> ResponseDto<T> success(String message, int statusCode, T data) {
        ResponseDto<T> response = new ResponseDto<>();
        response.setMessage(message);
        response.setStatusCode(statusCode);
        response.setData(data);
        return response;
    }

    public static <T> ResponseDto<T> error(String message, String errorCode, int statusCode) {
        ResponseDto<T> response = new ResponseDto<>();
        response.setMessage(message);
        response.setErrorMessage(message);
        response.setErrorCode(errorCode);
        response.setStatusCode(statusCode);
        return response;
    }
}
