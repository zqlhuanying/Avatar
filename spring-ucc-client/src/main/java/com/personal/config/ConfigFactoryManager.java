package com.personal.config;

/**
 * @author zhuangqianliao
 */
public interface ConfigFactoryManager {

    ConfigFactory getConfigFactory(String namespace);
}
