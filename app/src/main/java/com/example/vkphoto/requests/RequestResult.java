package com.example.vkphoto.requests;

import java.util.List;

public class RequestResult<T> {
    private List<T> list;
    private int total;

    public RequestResult(List<T> list, int total) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public int getTotal() {
        return total;
    }
}
