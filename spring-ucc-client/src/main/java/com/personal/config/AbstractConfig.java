package com.personal.config;

import com.google.common.base.Splitter;
import com.personal.util.FunctionUtils;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;


/**
 * @author zhuangqianliao
 */
public abstract class AbstractConfig implements Config, ConfigChangeListener {

    @Override
    public Short getShortProperty(String key, Short defaultValue) {
        return doGetProperty(key, defaultValue, FunctionUtils.stringToShort());
    }

    @Override
    public Integer getIntProperty(String key, Integer defaultValue) {
        return doGetProperty(key, defaultValue, FunctionUtils.stringToInteger());
    }

    @Override
    public Long getLongProperty(String key, Long defaultValue) {
        return doGetProperty(key, defaultValue, FunctionUtils.stringToLong());
    }

    @Override
    public Boolean getBooleanProperty(String key, Boolean defaultValue) {
        return doGetProperty(key, defaultValue, FunctionUtils.stringToBoolean());
    }

    @Override
    public Date getDateProperty(String key, Date defaultValue) {
        return doGetProperty(key, defaultValue, FunctionUtils.stringToDate());
    }

    @Override
    public Map<String, String> getMapProperty(String key, Map<String, String> defaultValue) {
        String value = getProperty(key, null);
        if (value == null) {
            return defaultValue;
        }
        checkMapValue(key, value);
        return FunctionUtils.stringToMapWithSplitter(Splitter.on(",").withKeyValueSeparator(":")).apply(value);
    }

    private <T> T doGetProperty(String key, T defaultValue, Function<String, T> convert) {
        String value = getProperty(key, null);
        if (value == null) {
            return defaultValue;
        }
        return convert.apply(value);
    }

    /**
     * Simple check
     * count(':') - count(',') = 1 or count(':') - count(',') = -1
     */
    private void checkMapValue(String key, String mapValue) {
        int count = 0;
        for (int i = 0, size = mapValue.length(); i < size; i++) {
            if (':' == mapValue.charAt(i)) {
                count++;
            }
            if (',' == mapValue.charAt(i)) {
                count--;
            }
        }
        if (count != 1 && count != -1) {
            throw new IllegalArgumentException(String.format("Ucc key: %s is not Map-Liked type", key));
        }
    }
}
