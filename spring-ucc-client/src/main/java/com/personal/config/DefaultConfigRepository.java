package com.personal.config;

import java.util.Properties;

/**
 * @author zhuangqianliao
 */
public class DefaultConfigRepository extends AbstractConfigRepository {

    @Override
    public Properties getConfig() {
        return new Properties();
    }
}
