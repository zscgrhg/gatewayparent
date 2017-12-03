package com.example.servicerest.jack;

import java.util.Collection;

public class Pager<D> {
    private int total;
    private int page;
    private int pageSize;
    private Collection<D> data;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Collection<D> getData() {
        return data;
    }

    public void setData(Collection<D> data) {
        this.data = data;
    }
}
