package com.atguigu.core.exception;

/**
 * @description:
 * @author: 十一。
 * @date: Created in 2019-12-18 20:32
 * @version: 1.0
 * @modified By:十一。
 */
public class OrderException extends RuntimeException {

    public OrderException() {
        super();
    }

    public OrderException(String message) {
        super(message);
    }
}
