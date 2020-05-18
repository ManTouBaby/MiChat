package com.hy.michat.rabbitMQ;

/**
 * @author:MtBaby
 * @date:2020/05/11 20:24
 * @desc:
 */
public class MQExchange<T> {
    private T data;
    private String exchangeType;//MQ类型

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }
}
