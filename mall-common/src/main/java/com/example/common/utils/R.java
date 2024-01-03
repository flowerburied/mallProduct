/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.example.common.utils;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */

import org.apache.http.HttpStatus; // Assuming you're using HttpStatus from Apache HTTP Components

public class R<T> extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    // Default constructor sets default values for "code" and "msg"
    public R() {
        put("code", 0);
        put("msg", "success");
    }

    // Static method to create an error response with default HTTP 500 code and a generic error message
    public static <T> R<T> error() {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
    }

    // Static method to create an error response with a custom error message
    public static <T> R<T> error(String msg) {
        return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    // Static method to create an error response with custom code and message
    public static <T> R<T> error(int code, String msg) {
        R<T> r = new R<>();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    // Static method to create a success response with a custom message
    public static <T> R<T> ok(String msg) {
        R<T> r = new R<>();
        r.put("msg", msg);
        return r;
    }

    // Static method to create a success response with a map of key-value pairs
    public static <T> R<T> ok(Map<String, Object> map) {
        R<T> r = new R<>();
        r.putAll(map);
        return r;
    }

    // Static method to create a default success response
    public static <T> R<T> ok() {
        return new R<>();
    }

    // Override put method to allow method chaining
    public R<T> put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    // Getter for the "code" property
    public Integer getCode() {
        return (Integer) this.get("code");
    }

    // Setter for the "data" property
    public R<T> setData(T data) {
        this.put("data", data);
        return this;
    }

    // Getter for the "data" property
    public T getData() {
        return (T) this.get("data");
    }
}



