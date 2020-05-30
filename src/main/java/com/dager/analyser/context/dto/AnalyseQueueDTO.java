package com.dager.analyser.context.dto;

import lombok.Data;

import java.io.Serializable;


/**
 * @author dager
 */
@Data
public class AnalyseQueueDTO<T> implements Serializable {
    private static final long serialVersionUID = -1596321019970308620L;

    private T data;

    private String recordDate;
}
