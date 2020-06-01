package com.dager.analyser.context.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author dager
 */
@Data
public class AnalyseInfoDTO implements Serializable {

    private String information;

    private String recordDate;
}
