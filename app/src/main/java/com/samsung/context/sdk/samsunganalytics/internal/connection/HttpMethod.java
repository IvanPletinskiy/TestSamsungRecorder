package com.samsung.context.sdk.samsunganalytics.internal.connection;

public enum HttpMethod {
    GET("GET"),
    POST("POST");
    
    String method;

    private HttpMethod(String str) {
        this.method = str;
    }

    public String getMethod() {
        return this.method;
    }
}
