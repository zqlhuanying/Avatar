package com.personal.config;

import java.util.Properties;

/**
 * @author zhuangqianliao
 */
public interface ConfigChangeListener {

    String getNamespace();

    void onConfigChange(String namespace, Properties newProperties);
}
