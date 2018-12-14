package com.personal.config;

import java.util.Properties;

/**
 * @author zhuangqianliao
 */
public interface ConfigRepository {

    /**
     * @return the config
     */
    Properties getConfig();

    void addChangeListener(ConfigChangeListener listener);

    void removeChangeListener(ConfigChangeListener listener);
}
