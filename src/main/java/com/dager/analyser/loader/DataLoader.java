package com.dager.analyser.loader;

import com.dager.analyser.base.PageDTO;

/**
 * @author G. Seinfeld
 * @since 2020/05/30
 */
public interface DataLoader<T> {
    /**
     * 加载数据，每次加载一页数据
     * @return 一页数据
     */
    PageDTO<T> load();
}
