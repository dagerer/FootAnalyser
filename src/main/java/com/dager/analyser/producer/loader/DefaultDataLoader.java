package com.dager.analyser.producer.loader;

import com.dager.analyser.producer.base.PageDTO;
import com.dager.analyser.producer.base.PageRequest;
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
        if (service == null) {
            return null;
        }
        return service.apply(request);
    }

}
