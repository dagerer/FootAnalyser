package com.dager.analyser;

import org.junit.jupiter.api.Test;


/**
 * @author G. Seinfeld
 * @since 2020/06/03
 */
class FootAnalyseTest {

    @Test
    void analyse() {

        FootAnalyse<Object> analyser = FootAnalyse.builder().build();
        analyser.analyse();
    }
}