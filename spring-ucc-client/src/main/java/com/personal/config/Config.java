package com.personal.config;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author zhuangqianliao
 */
public interface Config {

    String getProperty(String key, String defaultValue);

    Short getShortProperty(String key, Short defaultValue);

    Integer getIntProperty(String key, Integer defaultValue);

    Long getLongProperty(String key, Long defaultValue);

    Boolean getBooleanProperty(String key, Boolean defaultValue);

    Date getDateProperty(String key, Date defaultValue);

    Map<String, String> getMapProperty(String key, Map<String, String> defaultValue);

    /**
     * Return a set of the property names
     *
     * @return the property names
     */
    Set<String> getPropertyNames();
}
