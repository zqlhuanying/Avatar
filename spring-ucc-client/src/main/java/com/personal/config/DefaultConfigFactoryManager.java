package com.personal.config;

import com.personal.service.Injectors;

/**
 * @author zhuangqianliao
 */
public class DefaultConfigFactoryManager implements ConfigFactoryManager {

    private ConfigRegistry configRegistry;

    public DefaultConfigFactoryManager() {
        this.configRegistry = Injectors.getInstance(ConfigRegistry.class);
    }

    @Override
    public ConfigFactory getConfigFactory(String namespace) {
        return this.configRegistry.getConfigFactory(namespace);
    }
}
