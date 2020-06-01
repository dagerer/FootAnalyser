package com.dager.analyser.loader;

import com.dager.analyser.base.PageDTO;
import com.dager.analyser.base.PageRequest;
import lombok.Data;

import java.util.function.Function;

/**
 * @author dager
 */
@Data
public class DefaultDataLoader<R extends PageRequest, T> implements DataLoader<T> {


    private R request;

    private Function<R , PageDTO<T>> service;

    public DefaultDataLoader() {
    }


    @Override
    public PageDTO<T> load() {
        return loadData();
    }

    private PageDTO<T> loadData() {
        return this.getService().apply(request);
    }

}
