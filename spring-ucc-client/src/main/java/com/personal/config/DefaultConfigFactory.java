package com.personal.config;

/**
 * @author zhuangqianliao
 */
public class DefaultConfigFactory implements ConfigFactory {

    @Override
    public Config create(String namespace) {
        return new DefaultConfig(namespace, new DefaultConfigRepository());
    }
}
