package com.personal.config;

import com.google.common.collect.Maps;
import com.personal.exception.ConfigException;
import com.personal.service.Injectors;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class DefaultConfigManager implements ConfigManager {

    private static final Map<String, Config> CONFIGS = Maps.newConcurrentMap();
    private ConfigFactoryManager configFactoryManager;
    private Function<String, Config> configFunction;

    public DefaultConfigManager() {
        this.configFactoryManager = Injectors.getInstance(ConfigFactoryManager.class);
        this.configFunction = new ConfigFunction();
    }

    @Override
    public Config getConfig(String namespace) {
        return CONFIGS.computeIfAbsent(namespace, this.configFunction);
    }

    private class ConfigFunction implements Function<String, Config> {
        @Override
        public Config apply(String namespace) {
            ConfigFactory factory = configFactoryManager.getConfigFactory(namespace);
            if (factory == null) {
                log.info("Can not find ConfigFactory for the namespace {}", namespace);
                throw new ConfigException(String.format("ConfigFactory(%s) is not exists", namespace));
            }
            return factory.create(namespace);
        }
    }
}
