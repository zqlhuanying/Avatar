package com.personal.service;

/**
 * @author zhuangqianliao
 * Use Google Guice
 */
public interface Injector {

    /**
     * Get the instance with the given class
     * @param clazz the class
     * @param <T> the class type
     * @return instance
     */
    <T> T getInstance(Class<T> clazz);
}
