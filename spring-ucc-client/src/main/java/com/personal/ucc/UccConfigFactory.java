package com.personal.ucc;

import com.personal.config.Config;
import com.personal.config.ConfigFactory;

/**
 * @author zhuangqianliao
 */
public class UccConfigFactory implements ConfigFactory {

    @Override
    public Config create(String namespace) {
        return new UccConfig(namespace, new UccConfigRepository(namespace));
    }
}
