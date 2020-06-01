package com.dager.analyser.context;

import lombok.Data;
import lombok.Setter;

/**
 * @author G. Seinfeld
 * @since 2020/05/30
 */
@Data
public class Configuration {

    private int batchNum;

    private int threadNum;

    private int maxAvailableNum;

    public Configuration(){
        this.batchNum = 5;
        this.threadNum = 10;
        this.maxAvailableNum = 10;
    }

}
