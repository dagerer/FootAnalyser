package com.dager.analyser.producer.base;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PageDTO<E> implements Serializable {

    private static final long serialVersionUID = -5143271052585573989L;
    private Integer totalCount;

    private Integer pageSize;

    private Integer pageNo;

    private List<E> content;

    private Integer totalPage;

    public PageDTO() {

    }

    public PageDTO(Integer pageSize, Integer pageNo) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        totalCount = 0;
        content = new ArrayList<>();
    }

    public PageDTO(Integer totalCount, Integer pageSize, Integer pageNo, List<E> content) {
        this.pageSize = pageSize;
        this.pageNo = pageNo;
        this.totalCount = totalCount;
        this.content = content;
    }

    public Integer getTotalPage() {
        if (totalCount == null || pageSize == null || pageSize == 0) {
            return 0;
        }

        return (totalCount + pageSize - 1) / pageSize;
    }
}
