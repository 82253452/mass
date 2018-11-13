package com.f4w.freemarker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
public class TreadPoolConfig {
    private static final int NTHREADS = 10;

    /**
     * 线程池
     *
     * @return
     */
    @Bean(value = "threadPoolExecutor")
    public ExecutorService threadPoolExecutor() {
        ExecutorService pool = Executors.newFixedThreadPool(NTHREADS);
        return pool;
    }
}
