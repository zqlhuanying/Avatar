package com.personal.config;

import com.alibaba.fastjson.JSONObject;
import com.personal.util.ClassLoaderUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhuangqianliao
 */
@Slf4j
public class DefaultConfig extends AbstractConfig {

    private final String namespace;
    private Properties localProperties;
    private AtomicReference<Properties> remoteProperties;

    public DefaultConfig(String namespace, ConfigRepository configRepository) {
        this.namespace = namespace;
        this.localProperties = loadLocalProperties(namespace);
        this.remoteProperties = new AtomicReference<>();
        this.remoteProperties.set(configRepository.getConfig());
        configRepository.addChangeListener(this);
    }

    /**
     * The template for get the value
     */
    @Override
    public String getProperty(String key, String defaultValue) {
        // Step 1. get system property i.e. -DKey=value
        String value = System.getProperty(key);

        if (value == null) {
            // Step 2. get env property i.e. PATH...
            value = System.getenv(key);
        }

        if (value == null && this.localProperties != null) {
            // Step 3. get local property
            value = this.localProperties.getProperty(key);
        }

        if (value == null && this.remoteProperties.get() != null) {
            // Step 4. get remote property
            value = this.remoteProperties.get().getProperty(key);
        }

        return value == null ? defaultValue : value;
    }

    @Override
    public Set<String> getPropertyNames() {
        Properties properties = remoteProperties.get();
        if (properties == null) {
            return Collections.emptySet();
        }
        return properties.stringPropertyNames();
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    private Properties loadLocalProperties(String namespace) {
        return ClassLoaderUtil.loadLocalProperties(namespace);
    }

    @Override
    public void onConfigChange(String namespace, Properties newProperties) {
        log.info("namespace({}) config changed. New-Properties: {}", namespace, JSONObject.toJSONString(newProperties));

        remoteProperties.compareAndSet(remoteProperties.get(), newProperties);
    }
}
