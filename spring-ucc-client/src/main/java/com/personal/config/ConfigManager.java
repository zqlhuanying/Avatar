package com.personal.config;

/**
 * @author zhuangqianliao
 */
public interface ConfigManager {

    /**
     * Get the config with the namespace
     * @param namespace the namespace
     * @return the config
     */
    Config getConfig(String namespace);
}
