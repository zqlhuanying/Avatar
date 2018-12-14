package com.personal.config;

/**
 * @author zhuangqianliao
 */
public interface ConfigFactory {

    /**
     * Create a config for the namespace
     * @param namespace the namespace
     * @return the config instance
     */
    Config create(String namespace);
}
