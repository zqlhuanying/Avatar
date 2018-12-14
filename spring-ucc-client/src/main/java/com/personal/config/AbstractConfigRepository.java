package com.personal.config;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author zhuangqianliao
 */
public abstract class AbstractConfigRepository implements ConfigRepository {

    private List<ConfigChangeListener> listeners = Lists.newCopyOnWriteArrayList();

    @Override
    public void addChangeListener(ConfigChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeChangeListener(ConfigChangeListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void onConfigChange(String namespace, Properties newProperties) {
        for (ConfigChangeListener listener : listeners) {
            if (Objects.equals(listener.getNamespace(), namespace)) {
                listener.onConfigChange(namespace, newProperties);
            }
        }
    }
}
