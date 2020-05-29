package com.dager.analyser.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleContext implements Serializable {
    private static final long serialVersionUID = 4930495581595456964L;

    private String traceId;

    private Integer code;

    private String message;

    private Object data;
}
