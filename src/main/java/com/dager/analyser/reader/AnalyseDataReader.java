package com.dager.analyser.reader;

import com.dager.analyser.base.PageDTO;
import com.dager.analyser.base.PageRequest;
import lombok.Data;

import java.util.function.Function;

/**
 * @author dager
 */
@Data
public class AnalyseDataReader<R extends PageRequest, T> {


    private R request;

    private Function<R , PageDTO<T>> service;

    public AnalyseDataReader() {
    }


    public PageDTO<T> load() {
        return loadData();
    }

    private PageDTO<T> loadData() {
        return this.getService().apply(request);
    }

}
