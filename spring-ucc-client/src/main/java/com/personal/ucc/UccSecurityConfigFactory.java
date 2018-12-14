package com.personal.ucc;

import com.personal.config.Config;
import com.personal.config.ConfigFactory;

/**
 * @author zhuangqianliao
 */
public class UccSecurityConfigFactory implements ConfigFactory {

    @Override
    public Config create(String namespace) {
        return new UccConfig(namespace, new UccSecurityConfigRepository(namespace));
    }
}
