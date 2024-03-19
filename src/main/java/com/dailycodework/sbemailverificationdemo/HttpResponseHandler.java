package com.dailycodework.sbemailverificationdemo;


public class HttpResponseHandler {
    private int statusCode;
    private String data;

    public HttpResponseHandler(int statusCode, String data) {
        this.statusCode = statusCode;
        this.data = data;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getData() {
        return data;
    }

    public static HttpResponseHandler successResponse(String data) {
        return new HttpResponseHandler(200, data);
    }
}
