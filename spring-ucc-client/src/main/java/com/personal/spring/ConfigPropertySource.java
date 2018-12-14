package com.personal.spring;

import com.personal.config.Config;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * @author zhuangqianliao
 */
public class ConfigPropertySource extends EnumerablePropertySource<Config> {
    private static final String[] EMPTY_ARRAY = new String[0];

    public ConfigPropertySource(String name, Config source) {
        super(name, source);
    }

    @Override
    public String[] getPropertyNames() {
        Set<String> propertyNames = this.source.getPropertyNames();
        if (propertyNames.isEmpty()) {
            return EMPTY_ARRAY;
        }
        return propertyNames.toArray(new String[0]);
    }

    @Nullable
    @Override
    public Object getProperty(String name) {
        return this.source.getProperty(name, null);
    }
}
