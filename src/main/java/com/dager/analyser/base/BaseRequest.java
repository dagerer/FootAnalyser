package com.dager.analyser.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author dager
 */
@Getter
@Setter
public class BaseRequest implements Serializable {

    private String traceId;
}
