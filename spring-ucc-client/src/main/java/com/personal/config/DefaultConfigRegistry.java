package com.personal.config;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class DefaultConfigRegistry implements ConfigRegistry {

    /**
     * Namespace --> ConfigFactory
     */
    private static final Map<String, ConfigFactory> CONFIG_FACTORIES = Maps.newConcurrentMap();
    /**
     * Namespace --> NamespaceHolder
     */
    private static final Map<String, NamespaceHolder> NAMESPACE_HOLDER = Maps.newConcurrentMap();
    private static final ConfigFactory DEFAULT_CONFIG_FACTORY = new DefaultConfigFactory();

    @Override
    public void registry(String namespace, NamespaceHolder holder) {
        registry(namespace, holder, DEFAULT_CONFIG_FACTORY);
    }

    @Override
    public void registry(String namespace, NamespaceHolder holder, ConfigFactory configFactory) {
        if (NAMESPACE_HOLDER.containsKey(namespace)) {
            log.warn("NamespaceHolder({}) will be overridden by {}", namespace, holder.getClass());
        }
        NAMESPACE_HOLDER.put(namespace, holder);

        if (CONFIG_FACTORIES.containsKey(namespace)) {
            log.warn("ConfigFactory({}) will be overridden by {}", namespace, configFactory.getClass());
        }
        CONFIG_FACTORIES.put(namespace, configFactory);
    }

    @Override
    public ConfigFactory getConfigFactory(String namespace) {
        return CONFIG_FACTORIES.get(namespace);
    }

    @Override
    public NamespaceHolder getNamespaceHolder(String namespace) {
        return NAMESPACE_HOLDER.get(namespace);
    }
}
